"""
@Project : AutonomousDrivingSimulation
@Time : 2022/1/19-11:28
@Author : zch
@Description : 所有控制车辆需要用到的枚举类
"""
from enum import Enum


class VehicleState(Enum):
    IDLE = 0
    KEEP = 1
    ACCELERATE = 2
    BRAKE = 3
    CHANGELEFT = 4
    CHANGERIGHT = 5
    TURNLEFT = 6
    TURNRIGHT = 7
    LANEOFFSET = 8


class RoadOption(Enum):
    VOID = -1
    LEFT = 1
    RIGHT = 2
    STRAIGHT = 3
    CHANGE_LANE_LEFT = 4
    CHANGE_LANE_RIGHT = 5
    FOLLOW_LANE = 6
