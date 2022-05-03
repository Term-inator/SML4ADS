"""
@Project : AutonomousDrivingSimulation 
@File : read_xodr_test.py
@Author : zheng_chenghang
@Date : 2022/4/21 12:25 
"""
import carla

client = carla.Client('127.0.0.1', 2000)
world = client.get_world()
print('parsing xodr file')
with open("../maps/map.xodr") as odr_file:
    world = client.generate_opendrive_world(odr_file.read())
print('parsing finished')
carlaMap = world.get_map()


