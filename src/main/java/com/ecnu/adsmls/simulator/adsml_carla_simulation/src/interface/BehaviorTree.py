"""
@Project : AutonomousDrivingSimulation
@Time : 2022/2/13-11:29
@Author : zch
@Description :
"""
import json
import random
import sys
import numpy as np
import os

try:
    curr_dir = os.getcwd()
    parent_dir = curr_dir[:curr_dir.rfind(os.path.sep)]
    src_dir = parent_dir[:parent_dir.rfind(os.path.sep)]
    sys.path.append(parent_dir)
except IndexError:
    print('append path error!')

from src.carla_simulator.controller.enums import VehicleState
from src.interface.GuardFunction import *


class BehaviorNode:
    # 行为树中的行为节点
    def __init__(self):
        self.id = 0
        self.state = VehicleState.IDLE
        self.duration = 0
        self.acc = 0.0
        self.target_vel = 0.0
        self.transitions = []

    @staticmethod
    def get_idle_instance():
        node = BehaviorNode()
        node.id = -1
        node.state = VehicleState.IDLE
        return node

    def __repr__(self):
        return f'BehaviorNode[id={self.id}, state={self.state}, duration={self.duration}, acc={self.acc}, target ' \
               f'speed={self.target_vel}, len(transitions)={len(self.transitions)}];'


class BranchNode:
    # 行为树中的分支节点
    def __init__(self):
        self.id = 0
        self.transitions = []

    def __repr__(self):
        return f'BranchNode[id={self.id}, len(transitions)={len(self.transitions)}];'


class CommonTransition:
    # 普通迁移，从行为节点迁出
    def __init__(self):
        self.id = 0
        self.guards = []
        self.source_id = 0
        self.target_id = 0

    def __repr__(self):
        return f'CommonTransition[id={self.id}, source id={self.source_id}, target id={self.target_id}];'


class ProbabilityTransition:
    # 概率迁移，从分支节点迁出
    def __init__(self):
        self.id = 0
        self.source_id = 0
        self.target_id = 0
        self.weight = 0

    def __repr__(self):
        return f'ProbabilityTransition[id={self.id}, source id={self.source_id}, target id={self.target_id}, ' \
               f'weight={self.weight}]'


class BehaviorTree:
    # 行为树
    def __init__(self):
        self.elements = {}  # 行为树中所有的元素，包括行为节点、分支节点和两种迁移
        self.root = None  # 行为树的根节点
        self.vehicle_id = 0  # 该行为树所属的车辆id
        self.root_id = 0  # 行为树根节点元素的id
        self.current_id = 0  # 遍历记录，当前遍历到的元素id
        self.current = None  # 遍历记录，当前遍历到的元素引用
        self.is_end = False  # 遍历是否已经结束

    def build_tree_from_json(self, json_dict=None, path=''):
        """
        读取json文件，解析获取行为树
        :param json_dict: 描述行为树的字典对象
        :param path: 文件的路径
        :return:
        """
        if json_dict is not None:
            data = json_dict
        elif path != '':
            file = open(path, 'r', encoding='utf-8')
            data = json.load(file)
            file.close()
        else:
            raise RuntimeError('无法找到行为树文件')
        for k, v in data.items():
            # print(f'key: {k} \nvalue: {v}\nvalue type: {type(v)}')
            if k == 'behaviors':
                self.__parse_json_behaviors(v)
            elif k == 'branchPoints':
                self.__parse_json_branch_points(v)
            elif k == 'commonTransitions':
                self.__parse_json_common_transitions(v)
            elif k == 'probabilityTransitions':
                self.__parse_json_probability_transitions(v)
        # for k, v in self.elements.items():
        #     print(f'key:{k}, value:{v}')
        self.current_id = self.root_id = data['rootId']
        self.current = self.root = self.elements[data['rootId']]

    def __parse_json_behaviors(self, values):
        """
        解析json文件中的behaviors对象
        :param values: list. 每一个元素代表一个行为节点
        :return:
        """
        for v in values:
            behavior = BehaviorNode()
            behavior.id = v['id']
            if v['name'].upper() == 'DECELERATE':
                behavior.state = VehicleState.ACCELERATE
            else:
                behavior.state = VehicleState[v['name'].upper()]
            params = v['params']
            if 'duration' in params.keys():
                if params['duration'] != '':
                    behavior.duration = int(params['duration'])
                else:
                    behavior.duration = -1
            else:
                behavior.duration = -1
            if 'acceleration' in params.keys():
                behavior.acc = float(params['acceleration'])
            if 'target speed' in params.keys():
                behavior.target_vel = float(params['target speed'])
            if v['name'].upper() == 'DECELERATE':
                behavior.acc *= -1
            self.elements[behavior.id] = behavior

    def __parse_json_branch_points(self, values):
        """
        解析json文件中的branchPoints对象
        :param values: list. 每一个元素代表一个分支节点
        :return:
        """
        for v in values:
            branch = BranchNode()
            branch.id = int(v['id'])
            self.elements[branch.id] = branch

    def __parse_json_common_transitions(self, values):
        """
        解析json文件中的commonTransitions对象
        :param values: list. 每一个元素代表一个普通迁移
        :return:
        """
        for v in values:
            transition = CommonTransition()
            transition.id = int(v['id'])
            transition.target_id = int(v['targetId'])
            transition.source_id = int(v['sourceId'])
            if 'guard' in v.keys():
                transition.guards = v['guard']
            else:
                transition.guards = ''
            self.elements[transition.id] = transition
            source_node = self.elements[transition.source_id]
            source_node.transitions.append((transition.target_id, transition.guards))

    def __parse_json_probability_transitions(self, values):
        """
        解析json文件中的probabilityTransitions对象
        :param values: list. 每一个元素代表一个概率迁移
        :return:
        """
        for v in values:
            transition = ProbabilityTransition()
            transition.id = int(v['id'])
            transition.target_id = int(v['targetId'])
            transition.source_id = int(v['sourceId'])
            transition.weight = int(v['weight'])
            self.elements[transition.id] = transition
            source_node = self.elements[transition.source_id]
            source_node.transitions.append((transition.target_id, transition.weight))

    def traver_tree(self, _args, is_force):
        """
        遍历行为树，返回下一个行为节点
        :param is_force: 是否强制迁移出当前节点
        :param _args: 包含所有迁移时guard条件需要用到的变量的字典
        :return: BehaviorNode. 下一个行为节点
        """
        set_guard_args(_args)
        if len(self.current.transitions) == 0:
            self.is_end = True
            node = BehaviorNode()
            node.state = VehicleState.KEEP
            node.duration = 5
            node.target_vel = self.current.target_vel
            return node
        if not is_force and isinstance(self.current, BehaviorNode):
            transitions = self.current.transitions
            for k, v in _args.items():
                exec(f'{k} = v')
            # 导入用户自定义guard文件
            if _args['guardLibrary'] != '':
                def_path = _args['guardLibrary']
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
            # 依次检测每一条迁移路径的guard条件
            flag_dict = {}
            for index1, transition in enumerate(transitions):
                all_guard = transition[1].replace("&&", "and")
                all_guard = all_guard.replace("||", "or")
                name = f'flag{index1}'
                exec_str = f"flag_dict['{name}'] = " + all_guard
                if all_guard == '':
                    exec_str += 'True'
                print(f'\033[0;31;40m exec_str: {exec_str} \033[0m')
                exec(exec_str)
                if flag_dict[name]:
                    self.current_id = transition[0]
                    self.current = self.elements[self.current_id]
                    while isinstance(self.current, BranchNode):
                        self.__do_probability_trans()
                    return self.current
            return None
        elif is_force:
            transition = random.choice(self.current.transitions)
            self.current_id = transition[0]
            self.current = self.elements[self.current_id]
            while isinstance(self.current, BranchNode):
                self.__do_probability_trans()
            return self.current
        else:
            raise RuntimeError('当前节点不是行为节点')

    def __handle_guard_str(self, guard: str) -> str:
        for fun in GUARD_FUNCTIONS:
            if fun in guard:
                print(f'guard before handling: {guard}')
                guard = guard.replace(" ", "")
                sub = guard[len(fun) + 1:-1]
                params = sub.split(',')
                str_params = []
                for par in params:
                    str_params.append(f"'{par}'")
                str_guard = "(args"
                for str_par in str_params:
                    str_guard += ',' + str_par
                str_guard += ")"
                print(f'guard after handling: {str_guard}')
                return str_guard
        return guard

    def get_root_node(self):
        return self.root

    def __do_probability_trans(self):
        all_weight = 0
        for trans in self.current.transitions:
            all_weight += trans[1]
        ids = []
        probabilities = []
        for trans in self.current.transitions:
            ids.append(trans[0])
            probabilities.append(trans[1] / all_weight)
        self.current_id = np.random.choice(ids, size=1, p=probabilities)[0]
        self.current = self.elements[self.current_id]


if __name__ == "__main__":
    tree = BehaviorTree()
    tree.build_tree_from_json()
    t = 2
    exec('flag = t > 1;return flag')
    # print(flag)
