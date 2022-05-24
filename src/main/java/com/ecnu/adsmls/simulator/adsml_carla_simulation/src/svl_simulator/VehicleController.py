"""
@Project : AutonomousDrivingSimulation 
@File : VehicleController.py
@Author : zheng_chenghang
@Date : 2022/5/16 16:13 
"""
import numpy as np
import math
import os
import sys
import lgsvl
from collections import deque

try:
    curr_dir = os.getcwd()
    parent_dir = curr_dir[:curr_dir.rfind(os.path.sep)]
    src_dir = parent_dir[:parent_dir.rfind(os.path.sep)]
    sys.path.append(parent_dir)
except IndexError:
    print('append path error!')

from src.carla_simulator.controller.enums import VehicleState


def get_speed(vehicle: lgsvl.EgoVehicle):
    """
    获取车辆的速度
    :param vehicle:
    :return:
    """
    current_state: lgsvl.AgentState = vehicle.state
    print(f'speed in km/h: {current_state.speed*3.6}')
    return current_state.speed * 3.6


class VehicleController:
    """
    车辆控制器，利用横向和纵向两个PID控制器进行车辆控制
    """

    def __init__(self, vehicle, args_lateral=None, args_longitudinal=None, max_throttle=0.75,
                 max_brake=0.3, max_steering=0.8):
        """
        构造方法
        :param vehicle: carla_simulator.Vehicle.车辆引用
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
        # self._world = self._vehicle.get_world()
        self.past_steering = 0
        # self.state = VehicleState.IDLE

        if args_longitudinal is not None:
            self._lon_controller = PIDLongitudinalController(self._vehicle, **args_longitudinal)
        else:
            self._lon_controller = PIDLongitudinalController(self._vehicle)

        if args_lateral is not None:
            self._lat_controller = PIDLateralController(self._vehicle, **args_lateral)
        else:
            self._lat_controller = PIDLateralController(self._vehicle)

    def run_step(self, target_speed, transform):
        """
        计算下一步车辆的行为，返回控制类
        :param target_speed: float.下一步的目标速度
        :param transform: carla_simulator.WayPoint下一步的目标路径点
        :return: carla_simulator.VehicleControl
        """
        # 调用PID控制器
        acceleration = self._lon_controller.run_step(target_speed * 3.6)
        current_steering = self._lat_controller.run_step(transform)
        print(f'current_steering: {current_steering}')
        control = lgsvl.VehicleControl()
        # 界定油门或刹车数值
        if acceleration >= 0.0:
            control.throttle = min(abs(acceleration), self.max_throttle)
            control.braking = 0.0
        else:
            control.throttle = 0.0
            control.braking = min(abs(acceleration), self.max_brake)
        # 界定转向数值
        if current_steering > self.past_steering + 0.1:
            current_steering = self.past_steering + 0.1
        elif current_steering < self.past_steering - 0.1:
            current_steering = self.past_steering - 0.1

        if current_steering >= 0:
            steering = min(self.max_steer, current_steering)
        else:
            steering = max(-self.max_steer, current_steering)

        control.steering = steering
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
        :param vehicle: carla_simulator.Vehicle.被控制车辆
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

    def run_step(self, target_speed, debug=False):
        """
        计算下一步的速度控制
        :param target_speed: float.目标速度（km/h）
        :param debug: bool.是否打印debug日志
        :return: float.油门
        """
        current_speed = get_speed(self._vehicle)

        if debug:
            print('Current speed = {}'.format(current_speed))

        return self._pid_control(target_speed, current_speed)

    def _pid_control(self, target_speed, current_speed):
        """
        计算油门/刹车
        :param target_speed: float.目标速度（km/h）
        :param current_speed: float.当前速度（km/h）
        :return: float.油门/刹车
        """
        error = target_speed - current_speed
        self._error_buffer.append(error)
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
        :param vehicle: carla_simulator.Vehicle.被控制车辆
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

    def run_step(self, transform):
        """
        计算下一步转向
        :param transform: carla_simulator.Waypoint下一步目标路径点
        :return: float.方向
        """
        return self._pid_control(transform, self._vehicle.transform)

    def _pid_control(self, transform, vehicle_transform):
        """
        计算转向
        :param transform: carla_simulator.Waypoint下一步目标路径点
        :param vehicle_transform: carla_simulator.Transform汽车当前位置
        :return: float.转向[-1,1]
        """
        v_begin = vehicle_transform.position
        v_end = v_begin + lgsvl.Vector(x=math.cos(math.radians(vehicle_transform.rotation.y)),
                                       y=0, z=math.sin(math.radians(vehicle_transform.rotation.y)))
        v_vec = np.array([v_end.x - v_begin.x, 0.0, v_end.z - v_begin.z])
        # v_vec = np.array([vehicle_transform.rotation.x, vehicle_transform.rotation.y, vehicle_transform.rotation.z])
        w_vec = np.array([transform.position.x -
                          v_begin.x, 0.0, transform.position.z - v_begin.z])
        print(f'v_vec: {v_vec}, w_vec: {w_vec}')
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
