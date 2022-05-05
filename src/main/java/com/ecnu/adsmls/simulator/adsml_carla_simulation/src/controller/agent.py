"""
@Project : AutonomousDrivingSimulation
@Time : 2022/2/8-16:20
@Author : zch
@Description : 每一个车辆对应一个Agent类，基于Action类（用于计算下一步目的地和速度）和VehicleController（计算车辆控制数据）
"""
import random
from datetime import datetime
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

from src.controller.vehicle_controller import VehicleController
from src.controller.action import Action
from src.interface.BehaviorTree import BehaviorTree
from src.controller.enums import *
from src.utils.utils import get_speed


def get_distance(wp1, wp2):
    """
    获取两个waypoint之间的距离
    :param wp1:
    :param wp2:
    :return: float.
    """
    return wp1.transform.location.distance(wp2.transform.location)


class Agent:
    """
        每一辆车对应一个Agent，记录相关状态，控制车辆行驶
    """

    def __init__(self, carla_map, parser, vehicle, behavior_tree: BehaviorTree, action: Action, args_lateral=None,
                 args_longitudinal=None, max_speed=30, min_speed=10, model='asy'):
        self.carla_map = carla_map
        self.vehicle = vehicle
        self.parser = parser
        self.behavior_tree = behavior_tree
        self.action = action
        self.next_wp = None
        self.prev_road_id = -1  # 之前行驶的road的id
        self.prev_wp = None  # 上一个路径点
        self.turn_target_wp = None  # 转弯或变道时用到，判断行为是否完成
        self.start_time = None  # 当前行为的开始时间
        self.clock = 0.0
        self.target_vel = 15  # 目标速度
        self.junction_flag = True  # 用于判断是否刚进入junction，只刚进入时转弯行为才有效
        self.max_speed = max_speed
        self.min_speed = min_speed
        self.is_end = False  # 行为是否结束
        self.min_duration = 0.0
        self.change_flag = False
        # self.begin_flag = True
        self.behavior_node = behavior_tree.get_root_node()  # 当前行为节点
        self.controller = VehicleController(vehicle, args_lateral, args_longitudinal)
        self.start_time = datetime.now()
        self.model = model

    def run(self, args):
        """
        计算下一步控制
        :param args: 状态迁移用到的参数
        :return:
        """
        # 更新信息
        loc = self.vehicle.get_transform().location
        if loc.x == 0.0 and loc.y == 0.0 and loc.z == 0.0:
            return
        current_wp = self.carla_map.get_waypoint(loc)
        args = self.add_args(args, current_wp)
        self.update_state(current_wp, args)
        if self.is_end:
            print('end')
            return  # 若结束，则什么都不做
        state = self.behavior_node.state
        if state == VehicleState.IDLE:
            self.vehicle.enable_constant_velocity(carla.Vector3D(0, 0, 0))
            return
        if self.next_wp is None:
            self.next_wp = current_wp.next(3)[0]
            print(f'init wp:roadId:{self.next_wp.road_id} laneId:{self.next_wp.lane_id}')
        self.__update_prev_road_id(current_wp)
        # 根据情况做出计算
        if get_distance(current_wp, self.next_wp) < 3:
            # 到达下一个路径点
            self.change_flag = False
            report = f'arrive next wp, state:{state}'
            if state == VehicleState.ACCELERATE:
                self.target_vel = min(self.behavior_node.target_vel, self.max_speed)
            elif state == VehicleState.BRAKE:
                self.target_vel = 0
            elif (state == VehicleState.TURNLEFT or state == VehicleState.TURNRIGHT) and not self.junction_flag:
                # 若当前状态为转弯，并且不是刚进入junction，则已经计算出了转弯的路径点，只用跟随lane行驶即可
                state = VehicleState.KEEP
            # report += f' target vel: {self.target_vel}'
            # print(report)
            self.__compute_wp_vel(state, False)
        elif current_wp.is_junction and self.junction_flag and (state == VehicleState.TURNLEFT or
                                                                state == VehicleState.TURNRIGHT):
            self.change_flag = False
            # 刚进入junction,并且行为已经迁移至转弯，那么计算转向路径点，若没有符合的转向路径点则结束
            print(f'at junction, junction id={current_wp.get_junction().id}, dir={state}')
            self.__compute_wp_vel(state, is_junction=True)
            self.junction_flag = False
        elif self.change_flag:
            print(f'change flag=True, state:{self.behavior_node.state}')
            self.change_flag = False
            if (state == VehicleState.TURNLEFT or state == VehicleState.TURNRIGHT) and not self.junction_flag:
                pass
            else:
                self.__compute_wp_vel(state, current_wp.is_junction)
        # 更新is_junction
        if current_wp.is_junction and self.junction_flag:
            self.junction_flag = False
        if not current_wp.is_junction:
            self.junction_flag = True
        # 执行命令
        control = self.controller.run_step(self.behavior_node.acc, self.behavior_node.target_vel, self.next_wp)
        if state != VehicleState.KEEP and (control.throttle > 0 or control.brake > 0):
            self.vehicle.disable_constant_velocity()
        if self.behavior_node.acc > 0 and get_speed(self.vehicle)/3.6 < self.behavior_node.target_vel:
            control.throttle = 0
            control.brake = 0
            print(f'control[throttle:{control.throttle};brake:{control.brake};steer:{control.steer}]')
            self.vehicle.apply_control(control)
            self.vehicle.add_force(self.compute_force_from_acceleration(self.behavior_node.acc))
        elif self.behavior_node.acc < 0 and get_speed(self.vehicle)/3.6 > self.behavior_node.target_vel:
            control.throttle = 0
            control.brake = 0
            print(f'control[throttle:{control.throttle};brake:{control.brake};steer:{control.steer}]')
            self.vehicle.apply_control(control)
            self.vehicle.add_force(self.compute_force_from_acceleration(self.behavior_node.acc))
        else:
            print(f'control[throttle:{control.throttle};brake:{control.brake};steer:{control.steer}]')
            self.vehicle.add_force(carla.Vector3D(0, 0, 0))
            self.vehicle.apply_control(control)

    def __compute_wp_vel(self, state, is_junction):
        """
        根据当前行为计算下一步路径点和目标速度
        :param state:
        :param is_junction:
        :return:
        """
        self.prev_wp = self.next_wp
        # print(f'prev_road_id:{self.prev_wp.road_id}')
        if (state == VehicleState.TURNLEFT or state == VehicleState.TURNRIGHT) and not is_junction:
            state = VehicleState.KEEP
        next_wp, vel, target_wp = self.action.compute_next_wpt_vel(self.vehicle, state, is_junction=is_junction,
                                                                   prev_road_id=self.prev_road_id, prev_wp=self.prev_wp)
        if next_wp is not None:
            self.next_wp = next_wp
            print(f'next wp: roadId:{next_wp.road_id}; laneId:{next_wp.lane_id}')
            if target_wp is not None:
                # 若是转弯或变道，则设置turn_target_wp,用于判断是否完成该行为
                # print('set turn target wp')
                self.turn_target_wp = target_wp
        elif next_wp is None:
            print('next wp is None')
            self.next_wp, vel, self.turn_target_wp = self.action.compute_next_wpt_vel(self.vehicle,
                                                                                      is_junction=is_junction,
                                                                                      state=VehicleState.KEEP,
                                                                                      prev_wp=self.prev_wp,
                                                                                      prev_road_id=self.prev_road_id)
            print(f'{self.next_wp}, {self.turn_target_wp}')
        # self.target_vel = vel

    def __update_prev_road_id(self, current_wp):
        if current_wp.road_id != self.prev_road_id and not current_wp.is_junction:
            # print(f'{self.prev_road_id} {current_wp.road_id}')
            self.prev_road_id = current_wp.road_id

    def update_state(self, wp, args=None):
        """
        尝试更新状态
        :param wp: 当前路径点
        :param args: dict.状态迁移用到的参数
        :return:
        """
        if self.model == 'sy':
            time_interval = self.clock
        else:
            current_time = datetime.now()
            time_interval = current_time.timestamp() - self.start_time.timestamp()
        print(f'time interval:{time_interval}')
        if self.behavior_node.state == VehicleState.TURNLEFT or \
                self.behavior_node.state == VehicleState.TURNRIGHT or \
                self.behavior_node.state == VehicleState.CHANGELEFT or \
                self.behavior_node.state == VehicleState.CHANGERIGHT:
            # 由于这四种行为的duration为0，所以等到行为完成再迁移出去
            if self.turn_target_wp is None or get_distance(wp, self.turn_target_wp) < 1:
                # 认为行为完成有两种情况：1.turn_target_wp为None，既没有符合行为的路径点，无法执行，认为完成
                #                    2.当前车辆和turn_target_wp的距离很接近，认为完成
                self.turn_target_wp = None
                # 非强制迁出，既等待迁出条件满足或为叶节点时迁出
                temp_node = self.behavior_tree.traver_tree(is_force=False, _args=args)
                if temp_node is not None:
                    print(f'duration:{time_interval}, model:{self.model}')
                    print(args)
                    print(f'current state:{self.behavior_node.state}')
                    print(f'change state:{temp_node}')
                    self.behavior_node = temp_node
                    self.__set_min_duration()
                    self.start_time = datetime.now()
                    self.clock = 0.0
                    self.change_flag = True
                    self.target_vel = get_speed(
                        self.vehicle) if self.behavior_node.target_vel == 0.0 else self.behavior_node.target_vel * 3.6
                elif self.behavior_tree.is_end:
                    self.behavior_node = None
                    self.is_end = True
        else:
            # 除变道、转弯的其他行为按一下规则迁移
            print(f'node:{self.behavior_node}')
            if self.behavior_node.duration != -1 and time_interval >= self.behavior_node.duration:
                # 若执行该行为的时间大于duration，强制迁出，不用满足guard条件
                self.behavior_node = self.behavior_tree.traver_tree(is_force=True, _args=None)
                self.__set_min_duration()
                print(f'duration:{time_interval}, model:{self.model}')
                print(args)
                print(f'force change state:{self.behavior_node if self.behavior_node is not None else None}')
                self.start_time = datetime.now()
                self.change_flag = True
                self.clock = 0.0
                if self.behavior_node is not None:
                    self.target_vel = get_speed(
                        self.vehicle) if self.behavior_node.target_vel == 0.0 else self.behavior_node.target_vel * 3.6
            elif self.behavior_node.duration == -1 and len(self.behavior_node.transitions) > 0:
                # 若在duration时间之前满足guard条件，则迁出
                temp_node = self.behavior_tree.traver_tree(is_force=False, _args=args)
                if temp_node is not None:
                    self.behavior_node = temp_node
                    self.__set_min_duration()
                    print(f'duration:{time_interval}, model:{self.model}')
                    print(args)
                    print(f'change state:{temp_node}')
                    self.start_time = datetime.now()
                    self.clock = 0.0
                    self.change_flag = True
                    self.target_vel = get_speed(
                        self.vehicle) if self.behavior_node.target_vel == 0.0 else self.behavior_node.target_vel * 3.6
            elif self.behavior_node.duration == -1 and len(self.behavior_node.transitions) == 0:
                self.is_end = True
        if self.behavior_node is None and self.behavior_tree.is_end:
            self.is_end = True
        else:
            self.is_end = False

    def compute_force_from_acceleration(self, acc):
        physics_control = self.vehicle.get_physics_control()
        mass = physics_control.mass
        tf = self.vehicle.get_transform()
        forward_vector = tf.get_forward_vector()
        force = mass * acc * 1.1
        force_vector = forward_vector * force
        print(f'vector:{forward_vector}; mass:{mass}; force:{force};force_vec:{force_vector}')
        return force_vector

    def add_args(self, args, current_wp):
        """
        添加和本车辆相关的参数
        :param args:
        :param current_wp:
        :return:
        """
        args['is_junction'] = current_wp.is_junction
        args['next_junction'] = self.__distance_to_next_junction(current_wp)
        lane_change = current_wp.lane_change
        args['has_left_lane'] = True if lane_change == carla.LaneChange.Left or lane_change == carla.LaneChange.Both \
            else False
        args['hasLeftLane'] = True if lane_change == carla.LaneChange.Left or lane_change == carla.LaneChange.Both \
            else False
        args['has_right_lane'] = True if lane_change == carla.LaneChange.Right or lane_change == carla.LaneChange.Both \
            else False
        args['hasRightLane'] = True if lane_change == carla.LaneChange.Right or lane_change == carla.LaneChange.Both \
            else False
        args['duration'] = datetime.now().timestamp() - self.start_time.timestamp() if self.model == 'asy' else self.clock
        return args

    def __distance_to_next_junction(self, current_wp):
        if current_wp.is_junction:
            # 当前处于junction内
            return -1
        distance = 0.0
        next_wp = current_wp.next(1.0)[0]
        if next_wp.road_id == current_wp.road_id:
            if next_wp.s > current_wp.s:
                cont_point = 'end'
            else:
                cont_point = 'start'
            current_road = self.parser.get_road_by_id(current_wp.road_id)
            if current_road.road_link is None:
                return 21000000
            distance += (current_road.length - current_wp.s) if cont_point == 'end' else current_wp.s
            successor_type = current_road.road_link.suc_type if cont_point == 'end' else current_road.road_link.pre_type
            successor_id = current_road.road_link.suc_id if cont_point == 'end' else current_road.road_link.pre_id
            while successor_type != 'junction':
                successor_road = self.parser.get_road_by_id(successor_id)
                if successor_road is None:
                    break
                distance += successor_road.length
                road_link = successor_road.road_link
                successor_id = road_link.pre_id if current_road.id_ == road_link.suc_id else road_link.suc_id
                successor_type = road_link.pre_type if current_road.id_ == road_link.suc_id else road_link.suc_type
                current_road = successor_road
            # print(distance)
            return distance
        elif not next_wp.is_junction:
            distance += 0.5
            current_id = current_wp.road_id
            successor_id = next_wp.road_id
            successor_type = 'road'
            while successor_type != 'junction':
                successor_road = self.parser.get_road_by_id(successor_id)
                if successor_road is None:
                    break
                distance += successor_road.length
                road_link = successor_road.road_link
                temp = successor_id
                successor_id = road_link.pre_id if current_id == road_link.suc_id else road_link.suc_id
                successor_type = road_link.pre_type if current_id == road_link.suc_id else road_link.suc_type
                current_id = temp
            return distance
        else:
            # print(0.5)
            return 0.5

    def __set_min_duration(self):
        if self.behavior_node is None:
            return
        state = self.behavior_node.state
        if (state == VehicleState.KEEP or state == VehicleState.ACCELERATE) and len(
                self.behavior_node.transitions) == 1:
            transition = self.behavior_node.transitions[0]
            if len(transition[1]) == 0:
                self.min_duration = random.randint(0, self.behavior_node.duration)
                # print(f'set min duration:{self.min_duration}')
            else:
                self.min_duration = 0
        else:
            self.min_duration = 0

    def tick(self, step):
        """
        对本agent的时钟计时
        :return:
        """
        loc = self.vehicle.get_transform().location
        if loc.x != 0.0 or loc.y != 0.0 or loc.z != 0.0:
            self.clock += step
