"""
@Project : AutonomousDrivingSimulation
@Time : 2022/1/30-13:32
@Author : zch
@Description : 尝试实现各种功能的脚本文件
"""
import random

from src.parser.map_parser import MapParser
from src.carla_simulator.controller.vehicle_controller import VehicleController
from src.carla_simulator.carla_simulator import args_lateral_dict, args_longitudinal_dict
from src.carla_simulator.controller.action import Action
from src.carla_simulator.controller.enums import VehicleState, RoadOption
import carla


client = None
world = None
carlaMap = None
bp_library = None


def main():
    ego = create_car('vehicle.tesla.model3', 'ego', 0)

    while True:
        current_tf = ego.get_transform()
        forward_vec = current_tf.get_forward_vector()
        ego_vehicle_physics_control = ego.get_physics_control()
        ego_mass = ego_vehicle_physics_control.mass
        force = forward_vec * ego_mass * 5
        ego.add_force(force)
        print(ego.get_acceleration())


def waypoint_next_try():
    parser = MapParser()
    parser.parse('./Town05.xodr')
    ego = create_car('vehicle.tesla.model3', 'ego', is_junction=False)
    controller = VehicleController(ego, args_lateral=args_lateral_dict, args_longitudinal=args_longitudinal_dict)
    action = Action(carlaMap, ego, 3)

    ego_wp = carlaMap.get_waypoint(ego.get_transform().location)
    next_wp = ego_wp.next(5)[0]
    prev_wp = ego_wp
    prev_road_id = ego_wp.road_id
    junction_flag = True
    while True:
        ego_wp = carlaMap.get_waypoint(ego.get_transform().location)
        prev_road_id = update_prev_road_id(prev_road_id, ego_wp)
        if ego_wp.is_junction and junction_flag:
            print(f'incoming id={prev_road_id}')
            junction_flag = False
            junction_id = ego_wp.get_junction().id
            junction = parser.get_junction_by_id(junction_id)
            possible_wps = get_possible_wps_as_incoming(parser, junction, prev_road_id)
            print(f'length of income possible wps: {len(possible_wps)}')
            for index, wp in enumerate(possible_wps):
                print(f'wp {index}: road_id={wp.road_id}, lane_id={wp.lane_id}, lane_type={wp.lane_type}')
            loss_ids = get_loss_road_id(junction, possible_wps, prev_road_id)
            loss_wps = []
            if len(loss_ids) > 0:
                loss_wps = get_possible_wps_as_loss(parser, junction, loss_ids)
                for (k, v) in loss_wps.items():
                    print(f'road id:{k}, length of possible wps:{len(v)}')
                    possible_wps.extend(v)
                    for i, wp in enumerate(v):
                        print(f'wp {i}: road id:{wp.road_id}, lane id:{wp.lane_id}')
            for wp in possible_wps:
                direction = Action.compute_relative_angle(prev_wp, wp)
                if direction == RoadOption.LEFT:
                    prev_wp = next_wp
                    next_wp = wp.previous(10.0)[0]
        if not ego_wp.is_junction:
            junction_flag = True
        if ego_wp.transform.location.distance(next_wp.transform.location) < 1:
            prev_wp = next_wp
            next_wp, _ = action.compute_next_wpt_vel(ego, VehicleState.KEEP)
        if next_wp:
            control = controller.run_step(20, next_wp, ego_wp.is_junction)
            ego.apply_control(control)


def get_possible_wps_as_loss(parser, junction, loss_ids):
    results = {}
    for id_ in loss_ids:
        possible_wps = []
        conns = get_connections_by_incoming_road(junction, id_)
        road = parser.get_road_by_id(id_)
        for conn in conns:
            conn_cont_point = conn.cont_point  # contact point on connecting road
            conn_road_id = conn.conn_road  # id of connecting road
            conn_road = parser.get_road_by_id(conn_road_id)
            in_cont_point = conn_road.road_link.pre_cont_point if conn_cont_point == 'start' else \
                conn_road.road_link.suc_cont_point
            side = 0
            for ll in conn.lane_links:
                from_id = ll.from_
                lane = parser.get_lane(id_, from_id)
                if lane is not None:
                    side = (-abs(from_id)) / from_id
                    break
            if side == 0:
                continue
            for i in range(6):
                lane_id = int(i * side)
                if parser.get_lane(id_, lane_id) is not None:
                    if in_cont_point == 'start':
                        s = 0.3
                    elif in_cont_point == 'end':
                        s = road.length - 0.3
                    else:
                        print(f'can not find s value of road {id_}')
                        continue
                    wp = carlaMap.get_waypoint_xodr(id_, lane_id, s)
                    if wp is not None:
                        possible_wps.append(wp)
            if len(possible_wps) > 0:
                results[id_] = possible_wps
                break
    return results


def get_possible_wps_as_incoming(parser, junction, in_id):
    print(f'junction id: {junction.id_}')
    if junction is not None:
        print('junction found')
        conns = get_connections_by_incoming_road(junction, in_id)
        if len(conns) > 0:
            print('connections found')
        wps = get_wps(parser, conns)
        return wps
    return []


def get_connections_by_incoming_road(junction, incoming_id):
    conns = junction.connections
    results = []
    for conn in conns:
        if conn.in_road == incoming_id:
            results.append(conn)
    return results


def get_wps(parser, conns):
    results = []
    for conn in conns:
        conn_road_id = conn.conn_road
        conn_road = parser.get_road_by_id(conn_road_id)
        cont_point = conn.cont_point
        if conn_road is None:
            continue
        print(f'connecting road id={conn_road_id}')
        if cont_point == 'end':
            suc_road_id = conn_road.road_link.pre_id
            suc_cont_point = conn_road.road_link.pre_cont_point
        elif cont_point == 'start':
            suc_road_id = conn_road.road_link.suc_id
            suc_cont_point = conn_road.road_link.suc_cont_point
        else:
            print('can not find suc road of this conn road')
            continue
        if suc_cont_point == 'end':
            suc_road = parser.get_road_by_id(suc_road_id)
            s = suc_road.length - 0.3
        elif suc_cont_point == 'start':
            s = 0.3
        else:
            print('fail to get s value')
        for ll in conn.lane_links:
            print(f'from id={ll.from_}, to id={ll.to}')
            for lane in conn_road.road_lanes.lane_section.__lanes:
                if ll.to == lane.lane_id:
                    print(f'{lane}, same lane')
                    lane_suc_id = 0
                    if cont_point == 'start':
                        lane_suc_id = lane.suc_id
                    elif cont_point == 'end':
                        lane_suc_id = lane.pre_id
                    else:
                        print('can not find reverse lane')
                        continue
                    if lane_suc_id == 0:
                        print('can not find reverse lane')
                        continue
                    wp = carlaMap.get_waypoint_xodr(suc_road_id, lane_suc_id, s)
                    print(f'suc road id={suc_road_id}, suc lane id={lane_suc_id}')
                    if wp is not None and wp.lane_type == carla.LaneType.Driving:
                        flag = True
                        for result in results:
                            if result.road_id == wp.road_id and result.lane_id == wp.lane_id:
                                flag = False
                        if flag:
                            results.append(wp)
                    elif wp is None:
                        print(f'can not find such a waypoint, road id:{suc_road_id}, lane id:{lane.suc_id}, cont point:'
                              f'{suc_cont_point} s:{s}')
                    continue
    return results


def update_prev_road_id(prev_road_id, ego_wp):
    if ego_wp.road_id != prev_road_id and not ego_wp.is_junction:
        print(f'prev_road_id={prev_road_id}, current_road_id={ego_wp.road_id}')
        return ego_wp.road_id
    return prev_road_id


def get_loss_road_id(junction, wps, in_id):
    all_wps_ids = []
    loss_ids = []
    for wp in wps:
        flag = True
        for id_ in all_wps_ids:
            if id_ == wp.road_id:
                flag = False
                break
        if flag:
            all_wps_ids.append(wp.road_id)
    print(f'all road ids of possible wps:{all_wps_ids}')
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
    print(f'all loss ids:{loss_ids}')
    return loss_ids


def create_car(bp_name, car_name, spawn_point=-1, is_junction=True):
    bp = get_bp(bp_name, car_name)
    temp_points = carlaMap.get_spawn_points()
    spawn_points = []
    if not is_junction:
        for point in temp_points:
            wp = carlaMap.get_waypoint(point.location)
            if not wp.is_junction:
                spawn_points.append(point)
    else:
        spawn_points.extend(temp_points)
    if spawn_point == -1:
        ego_tf = random.choice(spawn_points)
    else:
        ego_tf = spawn_points[spawn_point]
    ego = world.spawn_actor(bp, carla.Transform(ego_tf.location + carla.Location(0, 0, 0.6), ego_tf.rotation))
    init_spectator(ego_tf)
    return ego


def get_bp(bp_name, role_name):
    bp = bp_library.find(bp_name)
    bp.set_attribute('role_name', role_name)
    color = bp.get_attribute('color').recommended_values
    bp.set_attribute('color', color[0])
    return bp


def init_spectator(tf):
    spectator = world.get_spectator()
    spectator.set_transform(carla.Transform(tf.location, tf.rotation))


if __name__ == "__main__":
    client = carla.Client('127.0.0.1', 2000)
    world = client.get_world()
    carlaMap = world.get_map()
    bp_library = world.get_blueprint_library()
    try:
        vehicles = world.get_actors().filter('vehicle.*')
        client.apply_batch([carla.command.DestroyActor(x) for x in vehicles])
        waypoint_next_try()
    finally:
        vehicles = world.get_actors().filter('vehicle.*')
        client.apply_batch([carla.command.DestroyActor(x) for x in vehicles])