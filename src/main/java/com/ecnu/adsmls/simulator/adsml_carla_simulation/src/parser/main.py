"""
@Project : AutonomousDrivingSimulation
@Time : 2022/1/13-15:22
@Author : zch
@Description : 测试文件
"""
from map_parser import MapParser
from map_filter import MapFilter
import logging


def test_parser_and_filter():
    logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
    logger = logging.getLogger(__name__)

    map_parser = MapParser()
    map_filter = MapFilter()
    for i in range(1, 8):
        path = f'../../maps/Town0{i}.xodr'
        logger.info(f'parsing file Town0{i}.xodr')
        map_parser.parse(path)
        logger.info('parsing file finished')
        lane_sections = map_parser.get_all_lane_sections()
        logger.info('begin filter test')
        lanes01 = map_filter.lane_filter(lane_sections, True, False)
        logger.info(f'one finished, {len(lanes01)} lanes')
        lanes02 = map_filter.lane_filter(lane_sections, False, True)
        logger.info(f'two finished, {len(lanes02)} lanes')
        lanes03 = map_filter.lane_filter(lane_sections, True, True)
        logger.info(f'three finished, {len(lanes03)} lanes')
        lanes04 = map_filter.lane_filter(lane_sections, False, False)
        logger.info(f'four finished, {len(lanes04)} lanes')
        logger.info('all finished')
        map_parser.clear()


def test_parser_and_filter_normal():
    map_parser = MapParser()
    map_filter = MapFilter()
    map_parser.parse('../../maps/map.xodr')
    lane_sections = map_parser.get_all_lane_sections()
    lanes = map_filter.lane_filter(lane_sections, True, False)
    MapFilter.choice_lane_norm(lanes)
