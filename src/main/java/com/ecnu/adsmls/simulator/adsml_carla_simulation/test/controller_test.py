"""
@Project : AutonomousDrivingSimulation
@Time : 2022/1/30-13:32
@Author : zch
@Description : 控制模块转弯功能测试文件
"""
import os
import random

import carla
import queue
from datetime import datetime
from src.interface.BehaviorTree import BehaviorTree
from src.carla_simulator.controller.enums import VehicleState
from src.carla_simulator.controller.action import Action
from src.carla_simulator.carla_simulator import args_lateral_dict, args_longitudinal_dict
from src.carla_simulator.controller.agent import Agent
from src.utils.MyTimer import MyTimer
from src.utils.utils import destroy_all_actors
from src.parser.map_parser import MapParser


def t_tick():
    global t
    t += 1


def sensor_callback(data, sensor_queue):
    sensor_queue.put(data.frame)
    # print(f'{data.frame}')


carlaClient = None
world = None
carlaMap = None
wp_step = 3
t_timer = MyTimer(datetime.now(), 1, t_tick)


def action_turn():
    """

    :return:
    """
    # 生成ego车辆，并设置相机位置和角度
    bp_library = world.get_blueprint_library()
    bp = bp_library.find('vehicle.tesla.model3')
    bp.set_attribute('role_name', 'ego')
    color = bp.get_attribute('color').recommended_values
    bp.set_attribute('color', color[0])

    spawn_points = carlaMap.get_spawn_points()
    not_junction_wps = []
    for tf in spawn_points:
        wp = carlaMap.get_waypoint(tf.location)
        if not wp.is_junction:
            not_junction_wps.append(wp)
    # ego_tf = spawn_points[138]
    # ego = world.spawn_actor(bp, ego_tf)
    ego_tf = random.choice(not_junction_wps).transform
    ego = world.spawn_actor(bp, carla.Transform(ego_tf.location + carla.Location(0, 0, 0.6), ego_tf.rotation))

    spectator = world.get_spectator()
    spectator.set_transform(carla.Transform(ego_tf.location, ego_tf.rotation))
    print(f'spectator:{spectator.id}')

    # Find the blueprint of the sensor.
    blueprint = world.get_blueprint_library().find('sensor.camera.rgb')
    # Modify the attributes of the blueprint to set image resolution and field of view.
    blueprint.set_attribute('image_size_x', '1920')
    blueprint.set_attribute('image_size_y', '1080')
    blueprint.set_attribute('fov', '110')
    sensor = world.spawn_actor(blueprint, ego_tf)

    sensor_queue = queue.Queue()
    sensor.listen(lambda data: sensor_callback(data, sensor_queue))

    path = f'./{carlaMap.name}.xodr'
    if not os.path.exists(path):
        carlaMap.save_to_disk(path)
    parser = MapParser()
    parser.parse(path)
    action = Action(carlaMap, parser, wp_step)
    behavior_tree = BehaviorTree()
    behavior_tree.build_tree_from_json(path='../test2.tree')
    agent = Agent(carlaMap, parser, ego, behavior_tree, action, args_lateral=args_lateral_dict,
                  args_longitudinal=args_longitudinal_dict)
    global t
    t = 0
    # t_timer.start()
    carlaClient.start_recorder(f'D:/Carla/Data/log/record.log')
    print('start recorder')
    start_time = datetime.now()
    while True:
        ego_tf = ego.get_transform()
        spec_loc = ego_tf.location + carla.Location(0, 0, 10)
        spec_rot = ego_tf.rotation
        spec_rot.pitch -= 60
        spectator.set_transform(carla.Transform(spec_loc, spec_rot))
        args = {'t': t}
        agent.run(VehicleState.IDLE, args)
        if agent.is_end:
            # t_timer.cancel()
            # print(f'总时长：{datetime.now().timestamp() - start_time.timestamp()}')
            carlaClient.apply_batch([carla.command.DestroyActor(sensor)])
            print(f'总时长:{t}')
            break
        world.tick()
        t += 0.1
        agent.tick()
        sensor_queue.get()


if __name__ == "__main__":
    carlaClient = carla.Client('127.0.0.1', 2000)
    world = carlaClient.get_world()
    carlaMap = world.get_map()
    settings = world.get_settings()
    settings.synchronous_mode = True
    settings.fixed_delta_seconds = 0.1
    world.apply_settings(settings)
    try:
        action_turn()
    finally:
        # t_timer.cancel()
        settings = world.get_settings()
        settings.synchronous_mode = False
        settings.fixed_delta_seconds = None
        world.apply_settings(settings)
        carlaClient.stop_recorder()
        print('stop recorder')
        destroy_all_actors(carlaClient, world)
