"""
@Project : AutonomousDrivingSimulation 
@File : constant_vel_test.py
@Author : zheng_chenghang
@Date : 2022/4/3 12:52 
"""
import datetime
import math
import queue

import carla

client = None
world = None
carlaMap = None
wp_step = 15


def action_change_lane():
    vehicles = world.get_actors().filter('vehicle.*')
    client.apply_batch([carla.command.DestroyActor(x) for x in vehicles])

    # 生成ego车辆，并设置相机位置和角度
    bp_library = world.get_blueprint_library()
    bp = bp_library.find('vehicle.audi.a2')
    bp.set_attribute('role_name', 'ego')
    color = bp.get_attribute('color').recommended_values
    bp.set_attribute('color', color[0])
    blueprint = bp_library.find('sensor.camera.rgb')
    blueprint.set_attribute('image_size_x', '1000')
    blueprint.set_attribute('image_size_y', '1000')
    blueprint.set_attribute('fov', '60')

    ego_tf = carlaMap.get_waypoint_xodr(0, -1, 10).transform
    ego = world.spawn_actor(bp, carla.Transform(ego_tf.location + carla.Location(0, 0, 0.6), ego_tf.rotation))
    ego_tf = ego.get_transform()
    spec_loc = carla.Location(-12, 0, 5)
    spec_rot = ego_tf.rotation
    sensor = world.spawn_actor(blueprint, carla.Transform(spec_loc, spec_rot), ego, carla.AttachmentType.Rigid)
    sensor_queue = queue.Queue()

    def sensor_callback(data, q):
        """

        :param data:
        :param q:
        """
        q.put(data.frame)

    sensor.listen(lambda data: sensor_callback(data, sensor_queue))
    curr_tf = ego.get_transform()
    while curr_tf.location.x == 0 and curr_tf.location.y == 0:
        curr_tf = ego.get_transform()
    ego.enable_constant_velocity(curr_tf.get_right_vector() * 5)

    spec_tf = ego_tf
    spec_tf.location += carla.Location(0, 0, 20)
    spec_tf.rotation.pitch = -90
    spectator = world.get_spectator()
    spectator.set_transform(spec_tf)

    start = datetime.datetime.now()
    while True:
        vec = ego.get_velocity()
        velocity = math.sqrt(vec.x**2+vec.y**2+vec.z**2)
        acc = ego.get_acceleration()
        acceleration = math.sqrt(acc.x**2+acc.y**2+acc.z**2)
        print(f'vel: {velocity}, acc: {acceleration}')
        if 2 < datetime.datetime.now().timestamp() - start.timestamp() < 4:
            control = carla.VehicleControl()
            control.brake = 0
            control.throttle = 0
            control.steer = -0.5
            ego.apply_control(control)
        elif datetime.datetime.now().timestamp() - start.timestamp() > 4:
            break
        world.tick()
        sensor_queue.get()


if __name__ == "__main__":
    client = carla.Client('127.0.0.1', 2000)
    world = client.get_world()
    with open("D:\Python\Pycharm Programs\AutonomousDrivingSimulation\maps\custom.xodr") as odr_file:
        world = client.generate_opendrive_world(odr_file.read())
    carlaMap = world.get_map()
    try:
        vehicles = world.get_actors().filter('vehicle.*')
        client.apply_batch([carla.command.DestroyActor(x) for x in vehicles])
        action_change_lane()
    finally:
        vehicles = world.get_actors().filter('vehicle.*')
        client.apply_batch([carla.command.DestroyActor(x) for x in vehicles])
