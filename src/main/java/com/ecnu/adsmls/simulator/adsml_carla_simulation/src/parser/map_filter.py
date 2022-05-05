"""
@Project : AutonomousDrivingSimulation
@Time : 2022/1/16-10:23
@Author : zch
@Description : 地图过滤工具类，提供方法筛选符合条件的信息
"""
import logging
import random
import numpy as np
import os
import sys

try:
    curr_dir = os.getcwd()
    parent_dir = curr_dir[:curr_dir.rfind(os.path.sep)]
    src_dir = parent_dir[:parent_dir.rfind(os.path.sep)]
    sys.path.append(parent_dir)
except IndexError:
    print('append path error!')

from src.parser.map_parser import MapParser


class MapFilter:
    def __init__(self, parser):
        logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
        self.logger = logging.getLogger(__name__)
        if parser is None:
            raise RuntimeError('参数为空。')
        if not isinstance(parser, MapParser):
            raise RuntimeError('参数错误。')
        self.parser = parser

    def lane_filter(self, has_left_lane, has_right_lane):
        """
        对所给laneSection数组，按照条件进行过滤
        :param has_left_lane: bool.是否有左车道
        :param has_right_lane: bool.是否有右车道
        :return: structs.Lane[].符合条件的lane数组
        """
        lanes = []
        lane_sections = self.parser.get_all_lane_sections()
        for s in lane_sections:
            # 获取所有可以行驶的车道
            left_lanes = s.left_lanes
            right_lanes = s.right_lanes
            # 有左车道
            if has_left_lane and not has_right_lane:
                start_left = 0
                start_right = 1
                end_left = len(left_lanes) - 1
                end_right = len(right_lanes)
            # 有右车道
            elif not has_left_lane and has_right_lane:
                start_left = 1
                start_right = 0
                end_left = len(left_lanes)
                end_right = len(right_lanes) - 1
            # 中间车道
            elif has_right_lane and has_left_lane:
                start_left = 1
                start_right = 1
                end_left = len(left_lanes) - 1
                end_right = len(right_lanes) - 1
            # 所有车道
            else:
                start_left = 0
                start_right = 0
                end_left = len(left_lanes)
                end_right = len(right_lanes)
            # self.logger.info(f'start_left:{start_left},start_right:{start_right},end_left:{end_left},end_right:{end_right}')
            # 取出所有符合条件的车道
            for i in range(start_left, end_left):
                # self.logger.info(left_lanes[i])
                lanes.append(left_lanes[i])
            for i in range(start_right, end_right):
                # self.logger.info(right_lanes[i])
                lanes.append(right_lanes[i])
        return lanes

    @staticmethod
    def choice_lane_random(lower_bound, upper_bound):
        """
        :param lower_bound:
        :param upper_bound:
        :return:
        """
        return random.uniform(lower_bound, upper_bound)

    @staticmethod
    def choice_lane_norm(lower_bound, upper_bound) -> float:
        """

        :param lower_bound:
        :param upper_bound:
        :return:
        """
        num = random.gauss(mu=(lower_bound+upper_bound)/2, sigma=1)
        while num < lower_bound or num > upper_bound:
            num = random.gauss(mu=(lower_bound+upper_bound)/2, sigma=1)
        return num

    @staticmethod
    def choice_lane_poisson(lower_bound, upper_bound):
        """

        :param lower_bound:
        :param upper_bound:
        :return:
        """
        num = np.random.poisson(size=1)[0]
        while num < lower_bound or num > upper_bound:
            num = np.random.poisson(size=1)[0]
        return num


if __name__ == "__main__":
    print(np.random.poisson(size=1).shape)
