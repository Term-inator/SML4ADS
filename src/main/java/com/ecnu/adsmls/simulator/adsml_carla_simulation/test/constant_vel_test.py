"""
@Project : AutonomousDrivingSimulation 
@File : constant_vel_test.py
@Author : zheng_chenghang
@Date : 2022/4/3 12:52 
"""
import datetime
import math

import carla
import random
from src.controller.enums import VehicleState
from src.controller.vehicle_controller import VehicleController
from src.simulator.carla_simulator import args_lateral_dict, args_longitudinal_dict
from src.controller.action import Action

client = None
world = None
carlaMap = None
wp_step = 15


def action_change_lane():
    vehicles = world.get_actors().filter('vehicle.*')
    client.apply_batch([carla.command.DestroyActor(x) for x in vehicles])

    # 生成ego车辆，并设置相机位置和角度
    bp_library = world.get_blueprint_library()
    bp = bp_library.find('vehicle.tesla.model3')
    bp.set_attribute('role_name', 'ego')
    color = bp.get_attribute('color').recommended_values
    bp.set_attribute('color', color[0])

    spawn_points = carlaMap.get_spawn_points()
    ego_tf = spawn_points[random.randint(0, len(spawn_points))]
    ego = world.spawn_actor(bp, carla.Transform(ego_tf.location + carla.Location(0, 0, 0.6), ego_tf.rotation))
    # ego.enable_constant_velocity(ego_tf.get_forward_vector() * 5);

    spectator = world.get_spectator()
    spectator.set_transform(carla.Transform(ego_tf.location, ego_tf.rotation))

    start = datetime.datetime.now()
    while True:
        vec = ego.get_velocity()
        print(f'{math.sqrt(vec.x**2+vec.y**2+vec.z**2)}')
        if datetime.datetime.now().timestamp() - start.timestamp() >= 3:
            print('brake')
            control = carla.VehicleControl()
            control.brake = 0.3
            ego.apply_control(control)
        else:
            print('throttle')
            control = carla.VehicleControl()
            control.throttle = 0.75
            ego.apply_control(control)


if __name__ == "__main__":
    client = carla.Client('127.0.0.1', 2000)
    world = client.get_world()
    carlaMap = world.get_map()
    try:
        vehicles = world.get_actors().filter('vehicle.*')
        client.apply_batch([carla.command.DestroyActor(x) for x in vehicles])
        action_change_lane()
    finally:
        vehicles = world.get_actors().filter('vehicle.*')
        client.apply_batch([carla.command.DestroyActor(x) for x in vehicles])
