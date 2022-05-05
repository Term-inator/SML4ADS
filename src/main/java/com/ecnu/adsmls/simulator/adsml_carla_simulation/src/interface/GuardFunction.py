"""
@Project : AutonomousDrivingSimulation 
@File : GuardFunction.py
@Author : zheng_chenghang
@Date : 2022/5/2 12:47 
"""
import carla
import os
import sys

try:
    curr_dir = os.getcwd()
    parent_dir = curr_dir[:curr_dir.rfind(os.path.sep)]
    src_dir = parent_dir[:parent_dir.rfind(os.path.sep)]
    sys.path.append(parent_dir)
except IndexError:
    print('append path error!')

from src.simulator.CarInfo import CarInfo


guard_args = {}
GUARD_FUNCTIONS = ['hasObjWithinDisInLane', 'hasObjWithinDisInLeftLane', 'hasObjWithinDisInRightLane',
                   'withinDisToObjsInLane', 'withinDisToObjsInRoad', 'isInSameLane']


def hasObjWithinDisInLane(car: CarInfo, dis):
    """
    检查是否在一定距离内存在物体
    :param car:
    :param dis:
    :return:
    """
    dis = float(dis)
    for k, v in guard_args.items():
        car_loc: carla.Location = car.waypoint.transform.location
        if isinstance(v, CarInfo):
            v_loc = v.waypoint.transform.location
            if car_loc.distance(v_loc) <= dis:
                if v.laneId == car.laneId and v.roadId == car.roadId:
                    return True
                else:
                    i = 1
                    wp: carla.Waypoint = car.waypoint.next(1)[0]
                    while i <= dis + 1:
                        if wp.road_id == v.roadId and wp.lane_id == v.laneId:
                            return True
                        wp: carla.Waypoint = wp.next(1)[0]
                        i += 1
    return False


def hasObjWithinDisInLeftLane(car: CarInfo, dis):
    """
    检查是否在一定距离内存在物体
    :param car:
    :param dis:
    :return:
    """
    dis = float(dis)
    if not guard_args['hasLeftLane']:
        return False
    for k, v in guard_args.items():
        car_loc: carla.Location = car.waypoint.transform.location
        if isinstance(v, CarInfo):
            v_loc = v.waypoint.transform.location
            if car_loc.distance(v_loc) <= dis:
                if abs(v.laneId) < abs(car.laneId) and v.roadId == car.roadId:
                    return True
                else:
                    i = 1
                    wp: carla.Waypoint = car.waypoint.get_left_lane().next(1)[0]
                    while i <= dis + 1:
                        if wp.road_id == v.roadId and wp.lane_id == v.laneId:
                            return True
                        wp: carla.Waypoint = wp.next(1)[0]
                        i += 1
    return False


def hasObjWithinDisInRightLane(car, dis):
    """
    检查是否在一定距离内存在物体
    :param car:
    :param dis:
    :return:
    """
    dis = float(dis)
    if not guard_args['hasRightLane']:
        return False
    for k, v in guard_args.items():
        car_loc: carla.Location = car.waypoint.transform.location
        if isinstance(v, CarInfo):
            v_loc = v.waypoint.transform.location
            if car_loc.distance(v_loc) <= dis:
                if abs(v.laneId) > abs(car.laneId) and v.roadId == car.roadId:
                    return True
                else:
                    i = 1
                    wp: carla.Waypoint = car.waypoint.get_right_lane().next(1)[0]
                    while i <= dis + 1:
                        if wp.road_id == v.roadId and wp.lane_id == v.laneId:
                            return True
                        wp: carla.Waypoint = wp.next(1)[0]
                        i += 1
    return False


def withinDisToObjsInLane(curr_car: CarInfo, other_car: CarInfo, dis):
    """
    检查当前车辆是否在给定车辆的距离范围内
    :param curr_car:
    :param other_car:
    :param dis:
    :return:
    """
    dis = float(dis)
    curr_loc: carla.Location = curr_car.waypoint.transform.location
    other_loc: carla.Location = other_car.waypoint.transform.location
    if curr_loc.distance(other_loc) <= dis:
        if curr_car.roadId == other_car.roadId and curr_car.laneId == other_car.laneId:
            return True
        else:
            i = 1
            wp = curr_car.waypoint.next(1)[0]
            while i <= dis + 1:
                if wp.road_id == other_car.roadId and wp.lane_id == other_car.laneId:
                    return True
                i += 1
                wp = wp.next(1)[0]
    return False


def withinDisToObjsInRoad(curr_car, other_car, dis):
    """
    检查当前车辆是否在给定车辆的距离范围内
    :param curr_car:
    :param other_car:
    :param dis:
    :return:
    """
    dis = float(dis)
    curr_loc: carla.Location = curr_car.waypoint.transform.location
    other_loc: carla.Location = other_car.waypoint.transform.location
    if curr_loc.distance(other_loc) <= dis:
        if curr_car.roadId == other_car.roadId:
            return True
        else:
            i = 1
            wp = curr_car.waypoint.next(1)[0]
            while i <= dis + 1:
                if wp.road_id == other_car.roadId:
                    return True
                i += 1
                wp = wp.next(1)[0]
    return False


def isInSameLane(car1, car2):
    """
    是否有在同一车道内
    :param car1:
    :param car2:
    :return:
    """
    if car1.roadId == car2.roadId and car1.laneId == car2.laneId:
        return True
    return False


def set_guard_args(args: dict):
    """

    :param args:
    """
    global guard_args
    guard_args = args
