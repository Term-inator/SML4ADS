"""
@Project : AutonomousDrivingSimulation
@Time : 2022/1/28-11:05
@Author : zch
@Description : 控制模块变道功能测试文件
"""
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

    spectator = world.get_spectator()
    spectator.set_transform(carla.Transform(ego_tf.location, ego_tf.rotation))

    controller = VehicleController(ego, args_lateral=args_lateral_dict, args_longitudinal=args_longitudinal_dict)
    action = Action(carlaMap, 15)

    next_wp = carlaMap.get_waypoint(ego_tf.location).next(wp_step)[0]
    while True:
        if next_wp.transform.location.distance(ego.get_transform().location) < 1:
            ego_wp = carlaMap.get_waypoint(ego.get_transform().location)
            print(f'wp rotation:{ego_wp.transform.rotation}')
            print(f'car rotation:{ego.get_transform().rotation}')
            if ego_wp.is_junction:
                print('at junction, can not change lane')
                next_wps = ego_wp.next(wp_step)
                next_wp = next_wps[0]
                continue
            next_wp, _ = action.compute_next_wpt_vel(ego, VehicleState.CHANGELEFT)
            if next_wp is None:
                print('can not change lane to left, try to change to the right')
                next_wp, _ = action.compute_next_wpt_vel(ego, VehicleState.CHANGERIGHT)
            if next_wp is None:
                print('can not change lane to right, keep forward')
                next_wp = ego_wp.next(wp_step)[0]
            print(f'next_wp_loc: {next_wp.transform.location}, next_wp_lane_id: {next_wp.lane_id}')

        if next_wp:
            control = controller.run_step(15, next_wp)
            ego.apply_control(control)
        else:
            print('next_wp is None')


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
