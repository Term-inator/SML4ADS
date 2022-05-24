"""
@Project : AutonomousDrivingSimulation 
@File : lgsvl_simulator.py
@Author : zheng_chenghang
@Date : 2022/5/15 10:56 
"""
import math
import os
import queue
import random
import sys
import cv2
import csv
import traceback
import lgsvl
import logging
import json

try:
    curr_dir = os.getcwd()
    parent_dir = curr_dir[:curr_dir.rfind(os.path.sep)]
    src_dir = parent_dir[:parent_dir.rfind(os.path.sep)]
    sys.path.append(parent_dir)
except IndexError:
    print('append path error!')

from src.interface.simulator import *


class LGSVLSimulator(Simulator):
    """
    针对SVL的Simulator实现
    """

    def __init__(self, img_path, mp4_path, data_path='', address="127.0.0.1", port=8181):
        super().__init__(img_path, mp4_path, data_path)
        self.client = lgsvl.Simulator(address=address, port=port)

    def destroy(self):
        """

        :return:
        """
        super().destroy()

    def createSimulation(self, scene):
        """

        :param scene:
        :return:
        """
        return LGSVLSimulation(scene, self.client, self.img_path, self.mp4_path, self.data_path)


class LGSVLSimulation(Simulation):
    """
    针对LGSVL的Simulation实现
    """

    def __init__(self, scene, client, img_path, mp4_path, data_path):
        super().__init__(scene)
        self.scene = scene
        self.client: lgsvl.Simulator = client
        self.img_path = img_path
        self.mp4_path = mp4_path
        self.data_path = data_path
        self.models = []
        self.agents = {}
        self.read_config()
        if self.client.current_scene == self.scene.map:
            self.client.reset()
        else:
            self.client.load(self.scene.map)
        if self.data_path != "":
            self.data_file = open(self.data_path, 'w', encoding='utf-8')
            self.data_writer = csv.writer(self.data_file)
            self.data_writer.writerow(
                ('name', 'location', 'velocity', 'acceleration', 'forward', 'bounding box', 'semantic tags'))
        else:
            self.data_file = None

    def read_config(self):
        """

        :return:
        """
        with open(os.path.abspath('./svl_simulator/config.json'), 'r', encoding='utf-8') as file:
            json_file = json.load(file)
            self.models = json_file['models']
        print('read config finished')

    def record_data(self):
        for name, state in self.curr_states.items():
            loc = state.waypoint.position
            forward_vec = lgsvl.utils.transform_to_forward(state.waypoint)
            bounding_box = state.vehicle.bounding_box
            self.data_writer.writerow((name, loc, state.speed, state.acceleration, forward_vec,
                                       bounding_box, None))

    def tick_timestamp(self):
        self.timestamp += self.time_step

    def run(self):
        """

        :return:
        """
        try:
            print('create objs')
            self.create_all_objs(False)
            print('create objs finished')
        except Exception as e:
            print(e.args)
            print(traceback.format_exc())
        finally:
            self.destroy()

    def build_guard_args(self) -> dict:
        """

        :return:
        """
        args = {'guardLibrary': self.scene.guardLibrary, 'clock': self.timestamp}
        return args

    def step(self, args):
        super().step(args)

    def create_cars_in_simulation(self, cars, is_test: bool = False):
        """

        :param cars:
        :param is_test:
        :return:
        """
        logging.info('Begin generate cars.')
        spawn_points = self.client.get_spawn()
        for car in cars:
            logging.info(f'Begin generate car {car.name}.')
            if car.location_type != 'Global Position' and car.location_type != 'Random Position':
                logging.error(f'LGSVL simulator does not support location type:{car.location_type}')
            vector = lgsvl.Vector(car.x, 0, car.y)
            transform = self.client.map_point_on_lane(vector)
            if transform is None:
                transform = lgsvl.Transform(spawn_points[0].position, spawn_points[0].rotation)
                spawn_points.pop()
            model = car.model
            if model == 'random':
                model = random.choice(self.models)
            vector_forward = lgsvl.utils.transform_to_forward(transform)
            state = lgsvl.AgentState()
            state.transform = transform
            state.velocity = vector_forward * car.init_speed
            vehicle = self.client.add_agent(model, lgsvl.AgentType.EGO, state)
            self.vehicles.append(vehicle)
            self.agents[car.name] = vehicle
            logging.info(f'Generate car {car.name} finished.')
        logging.info('Generated all cars.')

    def destroy(self):
        super().destroy()

    def current_state(self):
        """

        :return:
        """
        states = {}
        for name, car in self.agents.items():
            state: lgsvl.AgentState = car.state
            velocity = state.velocity
            info = CarInfo()
            info.name = name
            info.speed = math.sqrt(velocity.x ** 2 + velocity.y ** 2 + velocity.z ** 2)
            info.vehicle = car
            info.waypoint = state.transform
            states[name] = info
        self.curr_states = states

    def check_end(self):
        super().check_end()

    def test_scene(self, img_path) -> TestResult:
        return super().test_scene(img_path)
