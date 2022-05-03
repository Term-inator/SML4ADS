import math
import numpy as np
import carla
import time
from src.controller.enums import RoadOption
from src.utils.utils import *

client = None
world = None
carlaMap = None


def main():
    destroy_all_actors(client, world)

    start_time = time.time()
    bp_library = world.get_blueprint_library()
    bp = bp_library.find('vehicle.tesla.model3')
    bp.set_attribute('role_name', 'ego')
    color = bp.get_attribute('color').recommended_values
    bp.set_attribute('color', color[0])

    spawn_points = carlaMap.get_spawn_points()
    ego_tf = spawn_points[0]
    ego = world.spawn_actor(bp, carla.Transform(ego_tf.location + carla.Location(0, 0, 0.6), ego_tf.rotation))

    spectator = world.get_spectator()
    spectator.set_transform(carla.Transform(ego_tf.location, ego_tf.rotation))

    flag = True
    change_wp = None
    i = 0
    while True:
        ego_wp = carlaMap.get_waypoint(ego.get_transform().location)
        if flag and time.time() - start_time > 10:
            print('---try to change lane---')
            change_wp = change_lane(ego)
            flag = False
        if change_wp and change_wp.transform.location.distance(ego.get_transform().location) < 1:
            next_wps = change_wp.next(10)
            if ego_wp.is_junction:
                i += 1
                print('at junction %d --- %d next points' % (i, len(next_wps)))
                change_wp = None
                for wp in next_wps:
                    if compute_connection(ego_wp, wp) == RoadOption.RIGHT:
                        print('turn right')
                        change_wp = wp
                if not change_wp:
                    change_wp = next_wps[0]
            else:
                change_wp = next_wps[0]

        ego_control = carla.VehicleControl()
        ego_control.throttle = 0.3
        if change_wp:
            steer = calculate_steer(change_wp, ego.get_transform())
            ego_control.steer = steer
            # print('change lane; steer %f' % (steer))
        ego.apply_control(ego_control)


def change_lane(vehicle, direction=None):
    """
    变道，返回坐车道或者右车道下一个路径点
    :param vehicle: 控制的车辆
    :param direction: 变道方向
    :return: 路径点
    """
    current_tf = vehicle.get_transform()
    current_wp = carlaMap.get_waypoint(current_tf.location)
    left_wp = current_wp.get_left_lane()
    right_wp = current_wp.get_right_lane()
    change_wp = None
    if left_wp and left_wp.lane_id * current_wp.lane_id > 0:
        change_wp = left_wp.next(10)[0]
    elif right_wp and right_wp.lane_id * current_wp.lane_id > 0:
        change_wp = right_wp.next(10)[0]
    return change_wp


def keep_forward(vehicle):
    """
    让车辆保持目前的速度、沿着当前道路行驶，返回导航的下一个路径点。
    :param vehicle: 控制车辆
    :return: 下一个路径点
    """
    current_tf = vehicle.get_transform()
    current_wp = carlaMap.get_waypoint(current_tf.location)
    return current_wp.next(10)[0]


def calculate_steer(waypoint, vehicle_transform, k_p=1.0, k_d=0.0, k_i=0.0, dt=0.03):
    """
    根据当前位置和下一个目标位置计算汽车转向
    :param waypoint: simulator.Waypoint 下一个路径点
    :param vehicle_transform: simulator.Transform 汽车当前位置
    :param k_p: 比例系数
    :param k_d: 微分系数
    :param k_i: 积分系数
    :param dt: 时间间隔
    :return: [-1,1] 汽车方向
    """
    v_begin = vehicle_transform.location
    v_end = v_begin + carla.Location(x=math.cos(math.radians(vehicle_transform.rotation.yaw)),
                                     y=math.sin(math.radians(vehicle_transform.rotation.yaw)))

    v_vec = np.array([v_end.x - v_begin.x, v_end.y - v_begin.y, 0.0])
    w_vec = np.array([waypoint.transform.location.x -
                      v_begin.x, waypoint.transform.location.y -
                      v_begin.y, 0.0])
    _dot = math.acos(np.clip(np.dot(w_vec, v_vec) /
                             (np.linalg.norm(w_vec) * np.linalg.norm(v_vec)), -1.0, 1.0))

    _cross = np.cross(v_vec, w_vec)

    if _cross[2] < 0:
        _dot *= -1.0
    _de = 0.0
    _ie = 0.0

    return np.clip((k_p * _dot) + (k_d * _de) + (k_i * _ie), -1.0, 1.0)


def compute_connection(current_wpt,  target_wpt, threshold=30):
    """
    计算目前路径点和目标路径点的方向关系
    :param current_wpt: 当前路径点
    :param target_wpt: 目标路径点
    :param threshold: 比较阈值
    :return: 方向
    """
    n = target_wpt.transform.rotation.yaw
    n %= 360.0

    c = current_wpt.transform.rotation.yaw
    c %= 360.0

    diff_angle = (n - c) % 180.0
    if diff_angle < threshold or diff_angle > (180 - threshold):
        return RoadOption.FOLLOW_LANE
    elif diff_angle > 90.0:
        return RoadOption.LEFT
    else:
        return RoadOption.RIGHT


if __name__ == "__main__":
    client = carla.Client('127.0.0.1', 2000)
    world = client.get_world()
    carlaMap = world.get_map()
    try:
        main()
    finally:
        destroy_all_actors(client, world)
