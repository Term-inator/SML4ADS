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
with open('D:\\StudyFiles\\Programs\\Python\\ADSMLSimulation\\Cases\\Others\\custom.xodr') as file:
    client.generate_opendrive_world(file.read())
world: carla.World = client.get_world()
carlaMap: carla.Map = world.get_map()
spectator = world.get_spectator()
waypoint = carlaMap.get_waypoint_xodr(0, -2, 20)
wp_tf = waypoint.transform
spawn_loc = wp_tf.location + carla.Location(0, 0, 5)
spawn_tf = carla.Transform(spawn_loc, wp_tf.rotation)
spectator.set_transform(spawn_tf)

