"""
@Project : AutonomousDrivingSimulation
@Time : 2022/1/19-11:28
@Author : zch
@Description : 定义Action类，根据汽车状态计算车辆各种行为下一步路径点和速度的静态方法
"""
import carla
import os
import sys

try:
    curr_dir = os.getcwd()
    parent_dir = curr_dir[:curr_dir.rfind(os.path.sep)]
    src_dir = parent_dir[:parent_dir.rfind(os.path.sep)]
    sys.path.append(parent_dir)
except IndexError:
    print('append path error!')

from src.controller.enums import VehicleState, RoadOption
from src.utils.utils import get_speed


class Action:
    """
    计算车辆下一步行为的类
    """
    def __init__(self, carla_map: carla.Map, parser, step=5):
        """
        构造方法
        :param carla_map: simulator.Map. 车辆所在地图
        :param step: float. 下一个路径点的距离（米）
        """
        assert carla_map is not None
        assert isinstance(carla_map, carla.Map)
        self.map = carla_map
        self.step = step
        self.parser = parser

    def compute_next_wpt_vel(self, vehicle, state, is_junction, prev_road_id=0, prev_wp=None):
        """
        根据汽车状态（行为）计算下一步路径点和目标速度
        :param is_junction:
        :param prev_wp: simulator.Waypoint. 如果是在路口，需要提供进入junction之前的waypoint，方便计算方向
        :param prev_road_id: int. 如果是在路口，需要提供进入junction的road_id
        :param vehicle: simulator.Vehicle.被控制车辆
        :param state: structs.VehicleState.汽车状态
        :return: (simulator.Waypoint, float).目标路径点和目标速度
        """
        vel = 0.0
        next_wp = None
        target_wp = None
        if not isinstance(vehicle, carla.Vehicle) or not isinstance(state, VehicleState):
            raise RuntimeError('参数类型错误')
        if state == VehicleState.CHANGELEFT:
            next_wp = self.__change_lane(prev_wp, True)
            vel = get_speed(vehicle)
            target_wp = next_wp
            # print(f'{target_wp}')
            return next_wp, vel, target_wp
        elif state == VehicleState.CHANGERIGHT:
            next_wp = self.__change_lane(prev_wp, False)
            vel = get_speed(vehicle)
            target_wp = next_wp
            return next_wp, vel, target_wp
        elif state == VehicleState.KEEP:
            vel = get_speed(vehicle)
            next_wp = prev_wp.next(self.step)[0]
            return next_wp, vel, target_wp
        elif state == VehicleState.TURNLEFT or state == VehicleState.TURNRIGHT:
            vel = get_speed(vehicle)
            options = self.__turn_follow_connecting(vehicle, prev_wp, prev_road_id)
            for wp1, wp2, option in options:
                if (state == VehicleState.TURNLEFT and option == RoadOption.LEFT) or \
                        (state == VehicleState.TURNRIGHT and option == RoadOption.RIGHT) or \
                        (state == VehicleState.KEEP and option == RoadOption.STRAIGHT):
                    next_wp = wp1
                    target_wp = wp2
            if next_wp is None:
                next_wp = prev_wp.next(self.step)[0]
                target_wp = None
            return next_wp, vel, target_wp
        elif state == VehicleState.ACCELERATE or state == VehicleState.BRAKE:
            vel = 0.0
            next_wp = prev_wp.next(self.step)[0]
            return next_wp, vel, target_wp
        elif state == VehicleState.LANEOFFSET:
            return next_wp, vel, target_wp

    def __change_lane(self, prev_wp, direction=None) -> carla.Waypoint:
        """
        汽车便道，计算下一个路径点
        :param prev_wp: 上一个路径点
        :param direction: bool. True:左；False:右
        :return: simulator.WayPoint.下一个路径点
        """
        right_change_wp = None
        left_change_wp = None
        left_wp = prev_wp.get_left_lane()
        right_wp = prev_wp.get_right_lane()
        if left_wp is not None and left_wp.lane_type == carla.LaneType.Driving \
                and left_wp.lane_id * prev_wp.lane_id > 0:
            left_wps = list(left_wp.next(20))
            if left_wps:
                left_change_wp = left_wps[0]
        elif right_wp is not None and right_wp.lane_type == carla.LaneType.Driving \
                and right_wp.lane_id * prev_wp.lane_id > 0:
            right_wps = list(right_wp.next(20))
            if right_wps:
                right_change_wp = right_wps[0]
        if direction:
            return left_change_wp
        else:
            return right_change_wp

    def __keep_forward(self, vehicle):
        """
        向前行驶，计算下一个路径点
        :param vehicle: simulator.Vehicle.
        :return:simulator.Waypoint.
        """
        current_tf = vehicle.get_transform()
        current_wpt = self.map.get_waypoint(current_tf.location)
        next_wps = list(current_wpt.next(self.step))
        return next_wps[0]

    # TODO: 有一种情况：三条路连接的交叉路口，但是其他两条路方向一致，比如相对当前方向向右。当要保持前进时，没有符合条件的路径点，
    #  因为所有可用路径点都是向右的，于是会随机选择一个路径点。但是，当选择的是左侧的路径点进行转弯会与道路方向相反。
    def __turn_direction(self, vehicle, prev_road_id, prev_wp):
        """
        计算在交叉路口可能的下一个路径点与对应的方向
        :param vehicle: simulator.Vehicle.控制的车辆
        :param prev_road_id:
        :param prev_wp:
        :return: list(simulator.Waypoint, RoadOption)
        """
        options = []
        current_wp = self.map.get_waypoint(vehicle.get_transform().location)
        junction = current_wp.get_junction()
        possible_wps = self.__get_possible_wps_as_income(junction.id, prev_road_id)
        loss_ids = self.__get_loss_road_id(junction.id, possible_wps, prev_road_id)
        loss_wps = self.__get_possible_wps_as_loss(junction.id, loss_ids)
        for k, v in loss_wps.items():
            possible_wps.extend(v)
        for wp in possible_wps:
            option = self.compute_relative_angle(prev_wp, wp)
            options.append((wp, option))
        return options

    @staticmethod
    def compute_connection(current_wpt, target_wpt, threshold=25):
        """
        在要转弯的时候，计算当前路径点和目标路径点之间的连线和车辆向前的方向的夹角，从而判断是否是
        转弯的路径点
        :param current_wpt: simulator.waypoint.当前路径点
        :param target_wpt: simulator.waypoint.下一个目标路径点
        :param threshold: int.判断是否转弯的角度阈值
        :return: RoadOption.方向
        """
        n = target_wpt.transform.rotation.yaw
        print(f'target rotation:{n}')
        n %= 360.0
        c = current_wpt.transform.rotation.yaw
        print(f'current rotation:{c}')
        c %= 360.0
        diff_angle = (n - c) % 180.0
        # diff_angle = n - c
        print(f'diff_angle: {n - c}')
        # if diff_angle < threshold or diff_angle > (180 - threshold):
        if threshold > diff_angle > (-threshold):
            print('straight')
            return RoadOption.STRAIGHT
        elif (-90 + threshold) > diff_angle > (-90 - threshold):
            print('left')
            return RoadOption.LEFT
        elif (90 + threshold) > diff_angle > (90 - threshold):
            print('right')
            return RoadOption.RIGHT

    @staticmethod
    def compute_relative_angle(current_wpt, target_wpt, threshold=25):
        """
        给定两个路径点，计算两个路径点方向向量的夹角，从而判断是直走、左转还是右转
        :param current_wpt: simulator.Waypoint. 当前路径点
        :param target_wpt: simulator.Waypoint. 目标路径点
        :param threshold: int. 判断一个方向的左右偏差阈值
        :return: structs.RoadOption
        """
        n = target_wpt.transform.rotation.yaw
        c = current_wpt.transform.rotation.yaw
        print(f'target rotation:{n}')
        print(f'current rotation:{c}')
        n %= 360
        c %= 360
        diff_angle = (n - c) % 360
        print(f'diff_angle: {diff_angle}')
        if threshold > diff_angle >= 0 or 360 >= diff_angle > (360 - threshold):
            print('straight')
            return RoadOption.STRAIGHT
        elif (270 + threshold) >= diff_angle >= (270 - threshold):
            print('left')
            return RoadOption.LEFT
        elif (90 + threshold) >= diff_angle >= (90 - threshold):
            print('right')
            return RoadOption.RIGHT

    def __get_possible_wps_as_income(self, junction_id, in_id):
        junction = self.parser.get_junction_by_id(junction_id)
        if junction is not None:
            print('junction found')
            conns = self.__get_connections_by_in_road(junction, in_id)
            if len(conns) > 0:
                print('connections found')
            wps = self.__get_wps(conns)
            return wps
        return []

    def __get_connections_by_in_road(self, junction, in_id):
        conns = junction.connections
        results = []
        for conn in conns:
            if conn.in_road == in_id:
                results.append(conn)
        return results

    def __get_wps(self, connections):
        results = []
        for conn in connections:
            conn_road_id = conn.conn_road
            conn_road = self.parser.get_road_by_id(conn_road_id)
            cont_point = conn.cont_point
            if conn_road is None:
                continue
            if cont_point == 'end':
                suc_road_id = conn_road.road_link.pre_id
                suc_cont_point = conn_road.road_link.pre_cont_point
            elif cont_point == 'start':
                suc_road_id = conn_road.road_link.suc_id
                suc_cont_point = conn_road.road_link.suc_cont_point
            else:
                continue
            if suc_cont_point == 'end':
                suc_road = self.parser.get_road_by_id(suc_road_id)
                s = suc_road.length - 0.3
            else:
                s = 0.3
            for ll in conn.lane_links:
                for lane in conn_road.road_lanes.lane_section.lanes:
                    if ll.to == lane.lane_id:
                        lane_suc_id = 0
                        if cont_point == 'start':
                            lane_suc_id = lane.suc_id
                        elif cont_point == 'end':
                            lane_suc_id = lane.pre_id
                        else:
                            continue
                        if lane_suc_id == 0:
                            continue
                        wp = self.map.get_waypoint_xodr(suc_road_id, lane_suc_id, s)
                        if wp is not None and wp.lane_type == carla.LaneType.Driving:
                            flag = True
                            for result in results:
                                if result.road_id == wp.road_id and result.lane_id == wp.lane_id:
                                    flag = False
                            if flag:
                                results.append(wp)
                        continue
        return results

    def __get_loss_road_id(self, junction_id: int, wps, in_id: int):
        all_wps_ids = []
        loss_ids = []
        junction = self.parser.get_junction_by_id(junction_id)
        for wp in wps:
            flag = True
            for id_ in all_wps_ids:
                if id_ == wp.road_id:
                    flag = False
                    break
            if flag:
                all_wps_ids.append(wp.road_id)
        for conn in junction.connections:
            id1 = conn.in_road
            flag = True
            for id2 in all_wps_ids:
                if id1 == id2 or id1 == in_id:
                    flag = False
                    break
            for id3 in loss_ids:
                if id1 == id3:
                    flag = False
                    break
            if flag:
                loss_ids.append(id1)
        return loss_ids

    def __get_possible_wps_as_loss(self, junction_id: int, loss_ids):
        junction = self.parser.get_junction_by_id(junction_id)
        results = {}
        for id_ in loss_ids:
            possible_wps = []
            conns = self.__get_connections_by_in_road(junction, id_)
            road = self.parser.get_road_by_id(id_)
            for conn in conns:
                conn_cont_point = conn.cont_point  # contact point on connecting road
                conn_road_id = conn.conn_road  # id of connecting road
                conn_road = self.parser.get_road_by_id(conn_road_id)
                in_cont_point = conn_road.road_link.pre_cont_point if conn_cont_point == 'start' else \
                    conn_road.road_link.suc_cont_point
                side = 0
                for ll in conn.lane_links:
                    from_id = ll.from_
                    lane = self.parser.get_lane(id_, from_id)
                    if lane is not None:
                        side = (-abs(from_id)) / from_id
                        break
                if side == 0:
                    continue
                for i in range(6):
                    lane_id = int(i * side)
                    if self.parser.get_lane(id_, lane_id) is not None:
                        if in_cont_point == 'start':
                            s = 0.3
                        elif in_cont_point == 'end':
                            s = road.length - 0.3
                        else:
                            continue
                        wp = self.map.get_waypoint_xodr(id_, lane_id, s)
                        if wp is not None:
                            possible_wps.append(wp)
                if len(possible_wps) > 0:
                    results[id_] = possible_wps
                    break
        return results

    def __turn_follow_connecting(self, vehicle: carla.Vehicle, prev_wp: carla.Waypoint, prev_road_id: int):
        """
        完成计算转弯路径点的功能，顺着connecting road进行转弯
        :param vehicle: 被控制车辆
        :param prev_wp: 之前的wp
        :param prev_road_id:
        :return:
        """
        current_wp = self.map.get_waypoint(vehicle.get_transform().location)
        junction = self.parser.get_junction_by_id(current_wp.get_junction().id)
        conns = self.__get_connections_by_in_road(junction, prev_road_id)
        wps = []
        for conn in conns:
            cont_point = conn.cont_point
            conn_road = self.parser.get_road_by_id(conn.conn_road)
            if cont_point == 'start':
                s = conn_road.length - 0.5
            else:
                s = 0.5
            for lane in conn_road.road_lanes.lane_section.lanes:
                wp = self.map.get_waypoint_xodr(conn.conn_road, lane.lane_id, s)
                if wp is not None:
                    print(f'road {wp.road_id}, lane {wp.lane_id}, s {wp.s}')
                    option = Action.compute_relative_angle(prev_wp, wp)
                    if cont_point == 'start':
                        next_wp = self.map.get_waypoint_xodr(conn.conn_road, lane.lane_id, 5)
                    else:
                        next_wp = self.map.get_waypoint_xodr(conn.conn_road, lane.lane_id, conn_road.length - 5)
                    print(f'road {next_wp.road_id}, lane {next_wp.lane_id}, s {next_wp.s}')
                    wps.append((next_wp, wp, option))
        print(f'length {len(wps)}')
        return wps

    def __lane_offset(self, offset):
        pass
