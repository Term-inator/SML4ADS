"""
@Project : AutonomousDrivingSimulation
@File : carla_simulator.py
@Author : zheng_chenghang
@Date : 2022/2/22 14:29
"""
import os
import queue
import sys
import cv2
import csv
import traceback

try:
    curr_dir = os.getcwd()
#     parent_dir = curr_dir[:curr_dir.rfind('\\')]
#     src_dir = parent_dir[:parent_dir.rfind('\\')]
#     sys.path.append(parent_dir)
    print(curr_dir + "/src/main/java/com/ecnu/adsmls/simulator/adsml_carla_simulation")
    sys.path.append(curr_dir + "/src/main/java/com/ecnu/adsmls/simulator/adsml_carla_simulation")
except IndexError:
    print('append path error!')

from src.interface.simulator import *
from src.parser.map_filter import MapFilter
from src.parser.map_parser import MapParser
from src.controller.action import Action
from src.controller.agent import Agent
from src.utils.utils import *
from src.interface.GuardFunction import *

dt = 0.1
max_throt = 0.75
max_brake = 0.3
max_steer = 0.8
args_lateral_dict = {'k_p': 0.8, 'k_i': 0.05, 'k_d': 0.1, 'dt': dt}
args_longitudinal_dict = {'k_p': 1.0, 'k_i': 0.05, 'k_d': 0, 'dt': dt}


class CarlaSimulator(Simulator):
    """
    针对Carla仿真器对Simulator的实现
    """

    def __init__(self, img_path, mp4_path, address='127.0.0.1', port=2000, render=True, record=''):
        super().__init__()
        self.client = carla.Client(address, port)
        self.client.set_timeout(5)
        self.render = render  # 是否渲染仿真
        self.record = record  # 是否运行recorder
        self.scenario_number = 0  # 正在进行仿真的场景数量
        self.img_path = img_path
        self.mp4_path = mp4_path

    def destroy(self):
        """
        仿真完成后的销毁
        """
        super().destroy()

    def createSimulation(self, scene):
        """
        创建Simulation对象
        :param scene: 场景对象
        :return: Simulation
        """
        return CarlaSimulation(scene, self.client, self.render, self.scenario_number, self.img_path, self.mp4_path,
                               recorder=self.record)


class CarlaSimulation(Simulation):
    """
    针对Carla仿真器实现的Simulation
    """

    def __init__(self, scene, client, render, scenario_number, img_path, mp4_path,
                 recorder='D:/record.log', data_path="D:/Carla/Data/csv/data.csv"):
        super().__init__(scene)
        self.client = client
        self.render = render
        self.recorder = recorder
        self.scenario_number = scenario_number
        self.img_path = img_path
        self.mp4_path = mp4_path
        if scene.map != 'Town05' and scene.mapType == 'default':
            self.world = self.client.load_world(scene.map)
        elif scene.mapType == 'custom':
            if scene.map.endswith('.xodr'):
                print('parsing xodr file')
                with open(scene.map) as odr_file:
                    self.world = self.client.generate_opendrive_world(odr_file.read())
                print('parsing finished')
            else:
                raise RuntimeError(f'CARLA only supports OpenDrive maps')
        self.world = self.client.get_world()
        self.map = self.world.get_map()
        self.bpl = self.world.get_blueprint_library()
        self.agents = None
        self.parser = None
        self.camera = None
        self.pic_count = 0
        self.parse_map()
        self.data_path = data_path
        self.action = Action(self.map, self.parser, 3)
        self.models = []
        self.read_config()

        if self.data_path != "":
            self.data_file = open(self.data_path, 'w', encoding='utf-8')
            self.data_writer = csv.writer(self.data_file)
            self.data_writer.writerow(('name', 'x', 'y', 'velocity'))
        else:
            self.data_file = None

    def step(self, args):
        """
        针对Carla仿真器实现的仿真方法，每一次计算整个仿真场景
        :return:
        """
        args = dict(self.curr_states, **args)
        print(f'args:{args}')
        for name, agent in self.agents.items():
            if not agent.is_end:
                print(f'agent {name}:')
                agent.run(args)
                agent.tick(self.time_step)
            else:
                self.client.apply_batch([carla.command.DestroyActor(agent.vehicle)])

    def do_settings(self):
        """

        :return:
        """
        settings = self.world.get_settings()
        settings.no_rendering_mode = True if not self.render else False
        if self.time_step > 0:
            settings.synchronous_mode = True
            settings.fixed_delta_seconds = self.time_step
        else:
            pass
        self.world.apply_settings(settings)

    def wait(self):
        """
        等待车辆初始化完成
        :return:
        """
        for vehicle in self.vehicles:
            tf = vehicle.get_transform()
            if tf.x == 0.0 and tf.y == 0.0 and tf.z == 0.0:
                return True
            else:
                continue
        return False

    def run(self):
        """
        开始模拟
        :return: 模拟结果
        """
        self.do_settings()
        result = SimulationResult()
        result.start_time = datetime.now()
        try:
            print('create objs')
            self.create_all_objs(False)
            print('create objs finished')
            self.set_spectator()
            if self.time_step > 0:
                sensor_queue = self.generate_synchronous_camera(self.img_path)
            if self.recorder != '':
                self.client.start_recorder(self.recorder)
            spectator = self.world.get_spectator()
            spectator.set_transform(self.vehicles[0].get_transform())
            while True:
                ego_tf = self.vehicles[0].get_transform()
                ego_loc = ego_tf.location
                if ego_loc.x == 0.0 and ego_loc.y == 0.0:
                    ego_tf = self.vehicles[0].get_transform()
                forward_vector = ego_tf.get_forward_vector()
                spec_loc = ego_tf.location + carla.Location(0, 0, 5)
                spec_loc += forward_vector * (-10)
                spec_rot = ego_tf.rotation
                spectator.set_transform(carla.Transform(spec_loc, spec_rot))
                if self.camera is not None:
                    self.camera.set_transform(carla.Transform(spec_loc, spec_rot))

                self.current_state()
                if self.check_end():
                    result.end_time = datetime.now()
                    break
                if self.data_file is not None:
                    self.record_data()
                args = self.build_guard_args()
                self.step(args)
                if self.time_step > 0:
                    self.world.tick()
                    self.timestamp += self.time_step
                    sensor_queue.get()
            result.duration = self.timestamp
            self.generate_mp4()
            return result
        except Exception as err:
            print(err.args)
            print(traceback.format_exc())
        finally:
            settings = self.world.get_settings()
            settings.synchronous_mode = False
            settings.fixed_delta_seconds = None
            self.world.apply_settings(settings)
            self.client.stop_recorder()
            self.destroy()
            self.generate_mp4()

    def create_cars_in_simulation(self, cars, is_test=False):
        """
        针对Carla仿真器实现的创建车辆的函数
        :param cars: list. 车辆对象，包含初始化车辆的信息
        :param is_test:
        :return: list[].
        """
        spawn_wps = self.get_vehicle_spawn_point(length=len(cars))
        self.agents = {}
        for index, car in enumerate(cars):
            print(f'car {index}:')
            if car.model != 'random':
                bp = self.bpl.find(car.model)
            else:
                bp = self.bpl.find(random.choice(self.models))
            print(f'blueprint: {bp.id}')
            bp.set_attribute('role_name', car.name)
            if car.location_type == 'Lane Position':
                if car.init_lane_id == 0:
                    spawn_wp = spawn_wps[index]
                else:
                    offset = MapFilter.choice_lane_random(car.road_min_offset, car.road_max_offset)
                    print(f'offset {offset}')
                    spawn_wp = self.map.get_waypoint_xodr(car.init_road_id, car.init_lane_id, offset)
                    if spawn_wp is None:
                        raise RuntimeError('cannot get spawn point from given road and lane ids.')
            else:
                spawn_wp = self.map.get_waypoint(car.x, car.y)
                if spawn_wp is None:
                    raise RuntimeError('cannot get spawn point from given x and y values.')
            spawn_tf = spawn_wp.transform
            spawn_tf.rotation.yaw += car.road_deviation
            location = spawn_tf.location + carla.Location(0, 0, 0.2)
            if car.min_lateral_offset != 0 or car.max_lateral_offset != 0:
                lateral_offset = MapFilter.choice_lane_random(car.min_lateral_offset, car.max_lateral_offset)
                vector = lateral_offset * spawn_tf.get_right_vector()
                location += carla.Location(vector)
                print(f'deviation:{car.road_deviation}, offset:{lateral_offset}, vector:{vector}')
            print(location)
            tf = carla.Transform(location, spawn_tf.rotation)
            vehicle = self.world.spawn_actor(bp, tf)
            if car.init_speed > 0 and not is_test:
                vec = vehicle.get_transform().get_forward_vector()
                vec.z = 0.0
                vel = car.init_speed * vec
                vehicle.enable_constant_velocity(vel)
            self.vehicles.append(vehicle)
            model = 'asy' if self.time_step == 0 else 'sy'
            agent = Agent(self.map, self.parser, vehicle, car.behavior_tree, self.action,
                          args_lateral=args_lateral_dict, args_longitudinal=args_longitudinal_dict, model=model)
            self.agents[car.name] = agent
            print(f'generate car {index} finished')
        return self.vehicles

    def check_conflict(self, current_tf):
        """
        检测多辆车辆生成点是否冲突
        :return: bool
        """
        for tf in self.used_spawn_points:
            if tf.x == current_tf.x and tf.y == current_tf.y and tf.z == current_tf.z:
                return True
        return False

    def get_vehicle_spawn_point(self, allow_junction=False, length=1):
        """
        取得一个生成车辆的点
        :param allow_junction: 该生成点是否允许在junction
        :param length: 获取的生成点的个数
        :return: [carla.Waypoint]
        """
        spawn_tfs = list(self.map.get_spawn_points())
        wps = []
        if allow_junction:
            spawn_tfs = random.choices(spawn_tfs, k=length)
            for spawn_tf in spawn_tfs:
                wps.append(self.map.get_waypoint(spawn_tf.location))
        else:
            # return self.map.get_waypoint(spawn_tfs[138].location)
            spawn_wps = []
            for index, spawn_tf in enumerate(spawn_tfs):
                temp_wp = self.map.get_waypoint(spawn_tf.location)
                if not temp_wp.is_junction:
                    spawn_wps.append((index, temp_wp))
            choices = random.choices(spawn_wps, k=length)
            for choice in choices:
                wps.append(choice[1])
                print(choice[0])
        return wps

    def current_state(self):
        """
        获取当前所有车辆的状态，用于迁移条件、判断结束
        :return: dict{name: CarInfo}
        """
        states = {}
        for name, agent in self.agents.items():
            vehicle = agent.vehicle
            if not agent.is_end:
                info = CarInfo()
                pos = vehicle.get_transform().location
                wp = self.map.get_waypoint(pos)
                acc = vehicle.get_acceleration()
                print(f'acc: {acc}')
                vel = vehicle.get_velocity()
                info.laneId = wp.lane_id
                info.roadId = wp.lane_id
                info.acceleration = math.sqrt(acc.x ** 2 + acc.y ** 2 + acc.z ** 2)
                if abs(info.acceleration - 1) < 0.3:
                    print(f'\033[0;31;40m acc:{info.acceleration} \033[0m')
                info.intersection = wp.is_junction
                info.junctionId = wp.get_junction().id if wp.get_junction() is not None else -1
                info.laneSectionId = wp.section_id
                info.speed = math.sqrt(vel.x ** 2 + vel.y ** 2 + vel.z ** 2)
                info.t = agent.clock
                info.model = vehicle.type_id
                info.road_s = wp.s
                info.lane_s = wp.s
                info.waypoint = wp
                # TODO: info.offset
                states[name] = info
                print(f'{name} state:{info}')
        self.curr_states = states

    def record_data(self):
        """
        :return:
        """
        for name, state in self.curr_states.items():
            loc = state.waypoint.transform.location
            self.data_writer.writerow((name, loc.x, loc.y, state.speed))

    def check_end(self):
        """
        检查本次仿真是否结束
        :return:
        """
        if self.timestamp >= self.scene.simulationTime or \
                (self.time_step == 0 and
                 datetime.now().timestamp() - self.timestamp.timestamp() >= self.scene.simulationTime):
            return True
        args = dict(self.curr_states, **self.build_guard_args())
        set_guard_args(args)
        if args['guardLibrary'] != '':
            def_path = args['guardLibrary']
            index = def_path.rfind("/")
            print(index)
            path = def_path[:index]
            module = def_path[index + 1:def_path.find('.', index)]
            print(f'path = {path}')
            print(f'module = {module}')
            try:
                sys.path.append(path)
            except IndexError:
                print('can not add module path.')
            exec(f'import {module}')
        for k, v in self.curr_states.items():
            exec(f'{k} = v')
        end_flag = False
        exec(f'end_flag = {self.scene.endTrigger}')
        if end_flag:
            return True
        for name, agent in self.agents.items():
            if not agent.is_end:
                return False
        return True

    def destroy(self):
        """
        销毁
        :return:
        """
        # if os.path.exists():
        #     os.remove()
        self.data_file.close()
        self.client.apply_batch([carla.command.DestroyActor(x) for x in self.vehicles])
        self.client.apply_batch([carla.command.DestroyActor(self.camera)])
        self.vehicles.clear()
        self.agents = {}

    def tick_timestamp(self):
        """
        全局时钟计时
        :return:
        """
        self.timestamp += self.time_step

    def build_guard_args(self) -> dict:
        """

        :return:
        """
        args = {'guardLibrary': self.scene.guardLibrary, 'map': self.map, 'clock': self.timestamp}
        return args

    def test_scene(self, img_path):
        """
        :param img_path:
        :return:
        """
        try:
            print('begin generating objs.')
            self.create_all_objs(is_test=True)
            print('finish generating objs.')
            pos = self.vehicles[0].get_transform().location
            while pos.x == 0.0 and pos.y == 0.0:
                pos = self.vehicles[0].get_transform().location
            self.set_spectator()
            qu = self.generate_synchronous_camera(img_path)
            while True:
                if not qu.empty():
                    qu.get()
                if self.pic_count >= 2:
                    break
            self.pic_count = 0
            return TestResult()
        except Exception as err:
            print(err.args)
            print(traceback.format_exc())
        finally:
            self.destroy()

    def parse_map(self):
        """
        对正在使用的地图进行解析
        :return:
        """
        path = f'./{self.map.name}.xodr'
        if not os.path.exists(path):
            self.map.save_to_disk(path)
        self.parser = MapParser()
        self.parser.parse(path)

    def set_spectator(self, vehicle_tf=None):
        """
        设置观察者，方便观察仿真
        :param vehicle_tf:
        :return:
        """
        spectator = self.world.get_spectator()
        if vehicle_tf is not None:
            spectator.set_transform(vehicle_tf)
        else:
            spectator.set_transform(self.vehicles[0].get_transform())
        print(f'set spectator: {spectator.type_id}')

    def generate_synchronous_camera(self, store_path) -> queue:
        """
        创建同步需要用到的传感器和队列
        """
        blueprint = self.bpl.find('sensor.camera.rgb')
        blueprint.set_attribute('image_size_x', '1920')
        blueprint.set_attribute('image_size_y', '1080')
        blueprint.set_attribute('fov', '110')
        # blueprint.set_attribute('sensor_tick', '0.05')
        ego_tf = self.vehicles[0].get_transform()
        forward_vector = ego_tf.get_forward_vector()
        spec_loc = ego_tf.location + carla.Location(0, 0, 5)
        spec_loc += forward_vector * (-10)
        spec_rot = ego_tf.rotation
        # spec_rot.pitch -= 90
        sensor = self.world.spawn_actor(blueprint, carla.Transform(spec_loc, spec_rot))
        sensor_queue = queue.Queue()

        def sensor_callback(data, q):
            """

            :param data:
            :param q:
            """
            if store_path.endswith('.png'):
                data.save_to_disk(store_path)
            else:
                data.save_to_disk(os.path.join(store_path, '%d.png' % self.pic_count))
            self.pic_count += 1
            q.put(data.frame)

        sensor.listen(lambda data: sensor_callback(data, sensor_queue))
        self.camera = sensor
        return sensor_queue

    def generate_mp4(self):
        """
        :return:
        """
        size = (1920, 1080)
        fourcc = cv2.VideoWriter_fourcc(*"mp4v")
        videowrite = cv2.VideoWriter(self.mp4_path, fourcc, 8, size)
        img_array = []
        for i in range(self.pic_count):
            filename = self.img_path + f'/{i}.png'
            img = cv2.imread(filename)
            if img is None:
                print(filename + " is error!")
                continue
            img_array.append(img)
        for i in range(self.pic_count):
            videowrite.write(img_array[i])

    def read_config(self):
        """
        :return:
        """
        print('read config')
        with open(os.path.abspath('./config.json'), 'r', encoding='utf-8') as file:
            json_file = json.load(file)
            self.models = json_file['models']
        print('read config finished')
