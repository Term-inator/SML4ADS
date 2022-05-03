"""
@Project : AutonomousDrivingSimulation 
@File : read_map_try.py
@Author : zheng_chenghang
@Date : 2022/3/17 17:50 
"""
import carla

if __name__ == "__main__":
    client = carla.Client('127.0.0.1', 2000)
    with open('../src/simulator/Carla/Maps/Town06.xodr') as odr_file:
        world = client.generate_opendrive_world(odr_file.read())
    m = world.get_map()
    print(m.get_waypoint(0, 0, 0))

