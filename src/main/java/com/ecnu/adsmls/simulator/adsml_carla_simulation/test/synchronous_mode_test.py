'''
@Project : AutonomousDrivingSimulation 
@File : synchronous_mode_test.py
@Author : zheng_chenghang
@Date : 2022/2/19 12:57 
'''
import carla
import queue
from src.controller.vehicle_controller import VehicleController
from src.simulator.carla_simulator import args_lateral_dict, args_lateral_dict_junction, args_longitudinal_dict
from src.utils.utils import *


def sensor_callback(data, queue):
    queue.put(data.frame)
    print(f'{data.frame}')


if __name__ == "__main__":
    client = carla.Client('127.0.0.1', 2000)
    world = client.get_world()
    carlaMap = world.get_map()
    settings = world.get_settings()
    settings.synchronous_mode = True
    settings.fixed_delta_seconds = 0.1  # NOTE: Should not exceed 0.1
    world.apply_settings(settings)

    try:
        destroy_all_actors(client, world)
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
        ego_tf = random.choice(not_junction_wps).transform
        ego = world.spawn_actor(bp, carla.Transform(ego_tf.location + carla.Location(0, 0, 0.6), ego_tf.rotation))

        # Find the blueprint of the sensor.
        blueprint = world.get_blueprint_library().find('sensor.camera.rgb')
        # Modify the attributes of the blueprint to set image resolution and field of view.
        blueprint.set_attribute('image_size_x', '1920')
        blueprint.set_attribute('image_size_y', '1080')
        blueprint.set_attribute('fov', '110')
        # Set the time in seconds between sensor captures
        # blueprint.set_attribute('sensor_tick', '0.1')
        transform = carla.Transform(carla.Location(x=0.8, z=1.7))
        sensor = world.spawn_actor(blueprint, transform)

        queue = queue.Queue()
        sensor.listen(lambda data: sensor_callback(data, queue))
        spectator = world.get_spectator()
        spectator.set_transform(carla.Transform(ego_tf.location, ego_tf.rotation))
        controller = VehicleController(ego, args_lateral=args_lateral_dict,
                                       args_lateral_junction=args_lateral_dict_junction,
                                       args_longitudinal=args_longitudinal_dict)
        client.start_recorder('D:/Carla/Data/log/record.log')
        while True:
            ego_tf = ego.get_transform()
            current_wp = carlaMap.get_waypoint(ego_tf.location)
            spec_loc = ego_tf.location + carla.Location(0, 0, 10)
            spec_rot = carla.Rotation(-90, 0, 0)
            spectator.set_transform(carla.Transform(spec_loc, spec_rot))
            control = controller.run_step(15, current_wp.next(3)[0], False)
            ego.apply_control(control)
            world.tick()
            queue.get()
            print('tick')

    finally:
        settings = world.get_settings()
        settings.synchronous_mode = False
        settings.fixed_delta_seconds = None  # NOTE: Should not exceed 0.1
        world.apply_settings(settings)
        client.stop_recorder()
        print('stop recorder')
        destroy_all_actors(client, world)
