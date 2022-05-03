"""
@Project : AutonomousDrivingSimulation
@Time : 2022/1/19-11:28
@Author : zch
@Description : 
"""
import math
import carla
import random
from src.controller.enums import RoadOption


def get_speed(vehicle):
    """
    获取carla中车辆的速度
    :param vehicle: 车辆
    :return: float.速度(km/h)
    """
    vel = vehicle.get_velocity()
    return 3.6 * math.sqrt(vel.x ** 2 + vel.y ** 2 + vel.z ** 2)


def get_acc(vehicle):
    """

    :param vehicle:
    :return:
    """
    acc = vehicle.get_acceleration()
    return math.sqrt(acc.x ** 2 + acc.y ** 2 + acc.z ** 2)


def compute_connection(current_wpt, target_wpt, threshold=35):
    """
    在要转弯的时候，计算当前路径点和目标路径点之间的连线和车辆向前的方向的夹角，从而判断是否是
    转弯的路径点
    :param current_wpt: simulator.waypoint.当前路径点
    :param target_wpt: simulator.waypoint.下一个目标路径点
    :param threshold: int.判断是否转弯的角度阈值
    :return: RoadOption.方向
    """
    n = target_wpt.transform.rotation.yaw
    n = n % 360.0
    c = current_wpt.transform.rotation.yaw
    c = c % 360.0
    diff_angle = (n - c) % 180.0
    if diff_angle < threshold or diff_angle > (180 - threshold):
        return RoadOption.FOLLOW_LANE
    elif diff_angle > 90.0:
        return RoadOption.LEFT
    else:
        return RoadOption.RIGHT


def destroy_all_actors(client, world):
    """
    销毁模拟器中所有车辆
    :param client: simulator.Client.模拟器客户端
    :param world: simulator.World
    :return: None
    """
    vehicles = world.get_actors().filter('vehicle.*')
    client.apply_batch([carla.command.DestroyActor(x) for x in vehicles])


def create_car(world, carlaMap, bp_name, car_name, spawn_point=-1, is_junction=True):
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


def get_bp(bp_library, bp_name, role_name):
    bp = bp_library.find(bp_name)
    bp.set_attribute('role_name', role_name)
    color = bp.get_attribute('color').recommended_values
    bp.set_attribute('color', color[0])
    return bp


def init_spectator(world, tf):
    spectator = world.get_spectator()
    spectator.set_transform(carla.Transform(tf.location, tf.rotation))
