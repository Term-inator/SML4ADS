"""
@Project : AutonomousDrivingSimulation
@Time : 2022/2/11-11:37
@Author : zch
@Description :
"""
import carla

if __name__ == "__main__":
    client = carla.Client('127.0.0.1', 2000)
    world = client.get_world()
    carlaMap = world.get_map()
    client.replay_file('D:/Carla/Data/log/record.log', 0, 0, 0)
