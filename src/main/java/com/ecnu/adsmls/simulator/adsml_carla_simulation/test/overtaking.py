import carla
import random


client = None
world = None
carla_map = None


def spawn_leading_car(tf):
    print("---spawning vehicle---")
    bp = world.get_blueprint_library().find('vehicle.tesla.model3')
    bp.set_attribute('role_name', 'leading')
    color = random.choice(bp.get_attribute('color').recommended_values)
    bp.set_attribute('color', color)

    spawn_points = carla_map.get_spawn_points()
    number_of_spawn_points = len(spawn_points)

    if 0 < number_of_spawn_points:
        random.shuffle(spawn_points)
        transform = spawn_points[0]
        vehicle = world.spawn_actor(bp, carla.Transform(tf.location + carla.Location(0, 0, 0.6), tf.rotation))
        vehicle.set_target_velocity(carla.Vector3D(10, 0, 0))
        print("---successfully---")
        return vehicle
    else:
        print("---failed---")
        return None


def spawn_ego_car(tf):
    print("---spawn ego car---")
    bp = world.get_blueprint_library().find('vehicle.tesla.model3')
    bp.set_attribute('role_name', 'ego')
    color = random.choice(bp.get_attribute('color').recommended_values)
    bp.set_attribute('color', color)

    ego_car = world.spawn_actor(bp, carla.Transform(tf.location + carla.Location(0, 0, 0.6), tf.rotation))
    return ego_car


def main():
    destroy_vehicles()

    road_pairs = carla_map.get_topology()
    start_point = carla_map.get_waypoint_xodr(1, 2, 0)
    for pair in road_pairs:
        print('%s --- %s' % (pair[0].transform.location, pair[1].transform.location))
    # tm = client.get_trafficmanager(8000)
    # leading_car = spawn_leading_car(road_pairs[0][1].transform)
    ego_car = spawn_ego_car(start_point.transform)
    spec = world.get_spectator()
    spec.set_transform(start_point.transform)
    # if leading_car:
    #     leading_car.set_autopilot(True, tm.get_port())
    while True:
        world.wait_for_tick()
        # set_spec(leading_car)


def destroy_vehicles():
    vehicles = world.get_actors().filter('vehicle.*')
    client.apply_batch([carla.command.DestroyActor(x) for x in vehicles])


if __name__ == '__main__' :
    client = carla.Client('127.0.0.1', 2000)
    client.set_timeout(4.0)
    world = client.get_world()
    carla_map = world.get_map()
    main()