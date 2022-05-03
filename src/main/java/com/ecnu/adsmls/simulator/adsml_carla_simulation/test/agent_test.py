import random
import sys
import carla
import numpy as np


try:
    sys.path.append('D:/Carla/WindowsNoEditor/PythonAPI/carla')
except IndexError:
    pass

from agents.navigation.behavior_agent import BehaviorAgent  # pylint: disable=import-error
from agents.navigation.basic_agent import BasicAgent  # pylint: disable=import-error


client = None
world = None
carlaMap = None


def main():
    vehicles = world.get_actors().filter('vehicle.*')
    client.apply_batch([carla.command.DestroyActor(x) for x in vehicles])

    bp_library = world.get_blueprint_library()
    bp = bp_library.find('vehicle.tesla.model3')
    bp.set_attribute('role_name', 'ego')
    color = bp.get_attribute('color').recommended_values
    bp.set_attribute('color', color[0])

    start_point = carlaMap.get_waypoint_xodr(1, 2, 0)
    start_next_points = start_point.next(20)
    all_points = carlaMap.generate_waypoints(50)
    routine = []
    for point in all_points:
        if point.lane_id == 2:
            routine.append(point)

    spawn_points = carlaMap.get_spawn_points()
    number_of_spawn_points = len(spawn_points)
    ego_tf, leading_tf = get_tow_transforms(spawn_points, 30)
    ego_tf, leading_tf = get_tow_tf_between(routine, 30, 50)
    ego_tf = start_point.transform
    leading_tf = start_next_points[0].transform

    ego = None
    leading = None
    leading_agent = None
    if number_of_spawn_points > 0:
        random.shuffle(spawn_points)
        transform = spawn_points[0]

        leading = world.spawn_actor(bp, carla.Transform(leading_tf.location + carla.Location(0, 0, 0.6),
                                                            leading_tf.rotation))

        ego = world.spawn_actor(bp, carla.Transform(ego_tf.location + carla.Location(0, 0, 0.6), ego_tf.rotation))
        leading_agent = BehaviorAgent(leading, behavior='normal')
        leading_agent.set_destination(carlaMap.get_waypoint(leading.get_transform().location).next(10)[0].transform.location)
        leading.set_target_velocity(leading_tf.get_forward_vector()*2)

        spectator = world.get_spectator()
        spectator.set_transform(ego_tf)

    is_overtaking = True
    lane_id = start_point.lane_id
    while True:
        control = carla.VehicleControl()
        lane_wpt = carlaMap.get_waypoint(ego.get_transform().location)
        ego_fw_vector = ego.get_transform().get_forward_vector()
        lane_fw_vector = lane_wpt.transform.get_forward_vector()
        print(ego_fw_vector)
        print(lane_fw_vector)
        v1 = [ego_fw_vector.x, ego_fw_vector.y]
        v2 = [lane_fw_vector.x, lane_fw_vector.y]
        if leading_agent.done():
            print('leading arrived destination')
            leading_agent.set_destination(carlaMap.get_waypoint(leading.get_transform()).next()[0])
        if is_overtaking:
            if lane_wpt.lane_id == lane_id:
                control.throttle = 0.5
                control.steer = -0.5
                control.brake = 0
            else:
                if abs(angle(v1, v2)) < 0.03:
                    ego.set_transform(carla.Transform(ego.get_transform().location, lane_wpt.transform.rotation))
                    control.throttle = 0.5
                    is_overtaking = False
                else:
                    control.throttle = 0.5
                    control.steer = 0.5
                    control.brake = 0.0
        else:

            if angle(v1, v2) > 0:
                print('angle > 0 --- %f' % (angle(v1, v2)))
                control.steer = 0.1
            elif angle(v1, v2) < 0:
                print('angle > 0 --- %f' % (angle(v1, v2)))
                control.steer = -0.1
            else:
                control.steer = 0.0
            control.throttle = 0.5
        ego.apply_control(control)


def angle(v1, v2):
    cos_ = np.dot(v1, v2)/(np.linalg.norm(v1) * np.linalg.norm(v2))
    sin_ = np.cross(v1, v2)/(np.linalg.norm(v1)*np.linalg.norm(v2))
    arctan2_ = np.arctan2(sin_, cos_)
    return arctan2_/np.pi


def get_tow_tf_between(routine, mi, ma):
    tf1 = None
    tf2 = None
    for i in range(len(routine)):
        for j in range(1, len(routine)):
            if mi < routine[i].transform.location.distance(routine[j].transform.location) < ma:
                tf1 = routine[i].transform
                tf2 = routine[j].transform
    vector_forward = tf1.get_forward_vector()
    dis_x = tf1.location.x - tf2.location.x
    if dis_x * vector_forward.x > 0:
        return tf2, tf1
    return tf1, tf2


def generate_wpt(lane_id=2, distance=30):
    way_points = carlaMap.generate_waypoints(distance)
    routine = []
    for point in way_points:
        if point.lane_id == lane_id:
            routine.append(point.transform)
    return routine


def get_tow_transforms(tf_list, distance):
    for tf in tf_list:
        ego_point = carlaMap.get_waypoint(tf.location)
        next_points = ego_point.next(distance)
        if not ego_point.is_junction:
            for point in next_points:
                if point.lane_id == ego_point.lane_id and point.section_id == ego_point.section_id and point.road_id == \
                        ego_point.road_id and not point.is_junction:
                    print(tf)
                    print('%d --- %d --- %d' % (ego_point.lane_id, ego_point.road_id, ego_point.section_id))
                    print(point.transform)
                    print('%d --- %d --- %d' % (point.lane_id, point.road_id, point.section_id))
                    return tf, point.transform
    return None


def get_next_tf(tf, dis=30):
    ego_points = carlaMap.get_waypoint(tf)
    next_points = ego_points.next(dis)
    return next_points[0].transform


if __name__ == "__main__":
    client = carla.Client('127.0.0.1', 2000)
    world = client.get_world()
    carlaMap = world.get_map()
    try:
        main()
    finally:
        vehicles = world.get_actors().filter('vehicle.*')
        client.apply_batch([carla.command.DestroyActor(x) for x in vehicles])