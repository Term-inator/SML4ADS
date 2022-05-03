"""
@Project : AutonomousDrivingSimulation 
@File : custom_xodr_test.py
@Author : zheng_chenghang
@Date : 2022/4/29 15:30 
"""
import random
import carla

client = carla.Client('127.0.0.1', 2000)
client.set_timeout(5.0)
# Get World and Actors
with open('../maps/custom.xodr') as file:
    client.generate_opendrive_world(file.read())
world = client.get_world()
carlaMap: carla.Map = world.get_map()
bp_library = world.get_blueprint_library()
bp = bp_library.find('vehicle.tesla.model3')
bp.set_attribute('role_name', 'ego')
color = bp.get_attribute('color').recommended_values
bp.set_attribute('color', color[0])

spawn_points = carlaMap.get_spawn_points()
ego_tf = carlaMap.get_waypoint_xodr(0, -1, 3).transform
ego = world.spawn_actor(bp, carla.Transform(ego_tf.location + carla.Location(0, 0, 0.3), ego_tf.rotation))
ego.set_simulate_physics(False)
ego.add_impulse(carla.Vector3D(1000, 1000, 0))
