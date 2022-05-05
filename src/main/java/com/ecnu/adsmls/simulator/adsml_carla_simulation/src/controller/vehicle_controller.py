"""
@Project : AutonomousDrivingSimulation
@Time : 2022/1/19-11:28
@Author : zch
@Description : 车辆控制器类，包括横向（转向）和纵向（速度）两个PID控制器以及集成的控制器
"""
import carla
import numpy as np
import math
import os
import sys
from collections import deque

try:
    curr_dir = os.getcwd()
    parent_dir = curr_dir[:curr_dir.rfind(os.path.sep)]
    src_dir = parent_dir[:parent_dir.rfind(os.path.sep)]
    sys.path.append(parent_dir)
except IndexError:
    print('append path error!')

from src.controller.enums import VehicleState
from src.utils.utils import get_speed, get_acc


class VehicleController:
    """
    车辆控制器，利用横向和纵向两个PID控制器进行车辆控制
    """

    def __init__(self, vehicle, args_lateral=None, args_longitudinal=None, max_throttle=0.75,
                 max_brake=0.3, max_steering=0.8):
        """
        构造方法
        :param vehicle: simulator.Vehicle.车辆引用
        :param args_lateral: dict.横向（转向）控制器参数
            -k_p: 比例项
            -k_d: 差分项
            -k_i: 集分项
            -dt: 时间间隔（秒）
        :param args_longitudinal: dict.纵向（速度）控制器参数
            -k_p: 比例项
            -k_d: 差分项
            -K-i: 集分项
            -dt: 时间间隔（秒）
        :param max_throttle: float.最大油门
        :param max_brake: float.最大刹车
        :param max_steering: float.最大转向
        """
        self.max_brake = max_brake
        self.max_throttle = max_throttle
        self.max_steer = max_steering

        self._vehicle = vehicle
        self._world = self._vehicle.get_world()
        self.past_steering = self._vehicle.get_control().steer
        self.state = VehicleState.IDLE

        if args_longitudinal is not None:
            self._lon_controller = PIDLongitudinalController(self._vehicle, **args_longitudinal)
        else:
            self._lon_controller = PIDLongitudinalController(self._vehicle)

        if args_lateral is not None:
            self._lat_controller = PIDLateralController(self._vehicle, **args_lateral)
        else:
            self._lat_controller = PIDLateralController(self._vehicle)

    def run_step(self, acc, target_speed, waypoint):
        """
        计算下一步车辆的行为，返回控制类
        :param acc: 加速度
        :param target_speed: float.下一步的目标速度
        :param waypoint: simulator.WayPoint下一步的目标路径点
        :return: simulator.VehicleControl
        """
        acceleration = self._lon_controller.run_step(acc, target_speed*3.6)
        current_steering = self._lat_controller.run_step(waypoint)
        control = carla.VehicleControl()
        if acceleration >= 0.0:
            control.throttle = min(acceleration, self.max_throttle)
            control.brake = 0.0
        else:
            control.throttle = 0.0
            control.brake = min(abs(acceleration), self.max_brake)

        if current_steering > self.past_steering + 0.1:
            current_steering = self.past_steering + 0.1
        elif current_steering < self.past_steering - 0.1:
            current_steering = self.past_steering - 0.1

        if current_steering >= 0:
            steering = min(self.max_steer, current_steering)
        else:
            steering = max(-self.max_steer, current_steering)

        control.steer = steering
        control.hand_brake = False
        control.manual_gear_shift = False
        self.past_steering = steering

        return control


class PIDLongitudinalController:
    """
    纵向PID控制器，控制车辆速度
    """

    def __init__(self, vehicle, k_p=1.0, k_d=0.0, k_i=0.0, dt=0.03):
        """
        构造方法
        :param vehicle: simulator.Vehicle.被控制车辆
        :param k_p: float.比例项
        :param k_d: float.差分项
        :param k_i: float.积分项
        :param dt: int.时间间隔
        """
        self._vehicle = vehicle
        self._k_p = k_p
        self._k_d = k_d
        self._k_i = k_i
        self._dt = dt
        self._error_buffer = deque(maxlen=10)

    def run_step(self, acc, target_speed, debug=False):
        """
        计算下一步的速度控制
        :param acc: 加速度
        :param target_speed: float.目标速度（km/h）
        :param debug: bool.是否打印debug日志
        :return: float.油门
        """
        current_speed = get_speed(self._vehicle)

        if debug:
            print('Current speed = {}'.format(current_speed))

        return self._pid_control(acc, target_speed, current_speed)

    def _pid_control(self, acc, target_speed, current_speed):
        """
        计算油门/刹车
        :param acc:加速度
        :param target_speed: float.目标速度（km/h）
        :param current_speed: float.当前速度（km/h）
        :return: float.油门/刹车
        """
        error = target_speed - current_speed
        self._error_buffer.append(error)
        # if acc < 0 and current_speed > target_speed:
        #     return -np.clip(-acc/8, 0, 1)
        # elif acc >= 0 and current_speed < target_speed:
        #     return np.clip(acc/3, 0, 1)
        # else:
        if len(self._error_buffer) >= 2:
            _de = (self._error_buffer[-1] - self._error_buffer[-2]) / self._dt
            _ie = sum(self._error_buffer) * self._dt
        else:
            _de = 0.0
            _ie = 0.0

        return np.clip((self._k_p * error) + (self._k_d * _de) + (self._k_i * _ie), -1.0, 1.0)


class PIDLateralController:
    """
    横向（汽车转向）PID控制器
    """

    def __init__(self, vehicle, k_p=1.0, k_d=0.0, k_i=0.0, dt=0.03):
        """
        构造方法
        :param vehicle: simulator.Vehicle.被控制车辆
        :param k_p: float.比例项
        :param k_d: float.差分项
        :param k_i: float.积分项
        :param dt: int.时间间隔（秒）
        """
        self._vehicle = vehicle
        self._k_p = k_p
        self._k_d = k_d
        self._k_i = k_i
        self._dt = dt
        self._e_buffer = deque(maxlen=10)

    def run_step(self, waypoint):
        """
        计算下一步转向
        :param waypoint: simulator.Waypoint下一步目标路径点
        :return: float.方向
        """
        return self._pid_control(waypoint, self._vehicle.get_transform())

    def _pid_control(self, waypoint, vehicle_transform):
        """
        计算转向
        :param waypoint: simulator.Waypoint下一步目标路径点
        :param vehicle_transform: simulator.Transform汽车当前位置
        :return: float.转向[-1,1]
        """
        v_begin = vehicle_transform.location
        v_end = v_begin + carla.Location(x=math.cos(math.radians(vehicle_transform.rotation.yaw)),
                                         y=math.sin(math.radians(vehicle_transform.rotation.yaw)))
        v_vec = np.array([v_end.x - v_begin.x, v_end.y - v_begin.y, 0.0])
        w_vec = np.array([waypoint.transform.location.x -
                          v_begin.x, waypoint.transform.location.y -
                          v_begin.y, 0.0])
        _dot = math.acos(np.clip(np.dot(w_vec, v_vec) /
                                 (np.linalg.norm(w_vec) * np.linalg.norm(v_vec)), -1.0, 1.0))
        _cross = np.cross(v_vec, w_vec)
        if _cross[2] < 0:
            _dot *= -1.0
        self._e_buffer.append(_dot)
        if len(self._e_buffer) >= 2:
            _de = (self._e_buffer[-1] - self._e_buffer[-2]) / self._dt
            _ie = sum(self._e_buffer) * self._dt
        else:
            _de = 0.0
            _ie = 0.0
        return np.clip((self._k_p * _dot) + (self._k_d * _de) +
                       (self._k_i * _ie), -1.0, 1.0)


class PIDAccelerationController:
    """
    纵向PID控制器，控制车辆速度
    """

    def __init__(self, vehicle, k_p=1.0, k_d=0.0, k_i=0.0, dt=0.03):
        """
        构造方法
        :param vehicle: simulator.Vehicle.被控制车辆
        :param k_p: float.比例项
        :param k_d: float.差分项
        :param k_i: float.积分项
        :param dt: int.时间间隔
        """
        self._vehicle = vehicle
        self._k_p = k_p
        self._k_d = k_d
        self._k_i = k_i
        self._dt = dt
        self._error_buffer = deque(maxlen=10)

    def run_step(self, target_acc, debug=False):
        """
        计算下一步的速度控制
        :param target_acc:
        :param debug: bool.是否打印debug日志
        :return: float.油门
        """
        current_acc = get_acc(self._vehicle)

        if debug:
            print('Current speed = {}'.format(current_acc))

        return self._pid_control(target_acc, current_acc)

    def _pid_control(self, target_acc, current_acc):
        """
        计算油门/刹车
        :param acc:加速度
        :param target_speed: float.目标速度（km/h）
        :param current_speed: float.当前速度（km/h）
        :return: float.油门/刹车
        """
        error = target_acc - current_acc
        self._error_buffer.append(error)
        # if acc < 0 and current_speed > target_speed:
        #     return -np.clip(-acc/8, 0, 1)
        # elif acc >= 0 and current_speed < target_speed:
        #     return np.clip(acc/3, 0, 1)
        # else:
        if len(self._error_buffer) >= 2:
            _de = (self._error_buffer[-1] - self._error_buffer[-2]) / self._dt
            _ie = sum(self._error_buffer) * self._dt
        else:
            _de = 0.0
            _ie = 0.0

        return np.clip((self._k_p * error) + (self._k_d * _de) + (self._k_i * _ie), -1.0, 1.0)