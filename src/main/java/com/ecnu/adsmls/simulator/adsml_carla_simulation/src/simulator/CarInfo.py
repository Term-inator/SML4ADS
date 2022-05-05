"""
@Project : AutonomousDrivingSimulation 
@File : CarInfo.py
@Author : zheng_chenghang
@Date : 2022/4/17 12:45 
"""
import carla


class CarInfo:
    """
    车辆运行时信息实体
    """

    def __init__(self):
        self.roadId = -1
        self.laneSectionId = -1
        self.laneId = -1
        self.junctionId = -1
        self.intersection = False
        self.road_s = 0.0
        self.lane_s = 0.0
        self.offset = 0.0
        self.width = 0.0
        self.length = 0.0
        self.speed = 0.0
        self.acceleration = 0.0
        self.model = ''
        self.t = 0.0
        self.waypoint: carla.Waypoint = None
        self.vehicle = None

    def __repr__(self):
        return f'CarInfo[acceleration:{self.acceleration};speed:{self.speed}]'
