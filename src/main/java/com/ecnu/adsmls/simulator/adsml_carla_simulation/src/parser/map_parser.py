"""
@Project : AutonomousDrivingSimulation
@Time : 2022/1/13-11：30
@Author : zch
@Description : OpenDRIVE文件解析类，存储road和junction标签信息
"""
import xml.etree.ElementTree as ET
import logging
import os
import sys

try:
    curr_dir = os.getcwd()
    parent_dir = curr_dir[:curr_dir.rfind(os.path.sep)]
    src_dir = parent_dir[:parent_dir.rfind(os.path.sep)]
    sys.path.append(parent_dir)
except IndexError:
    print('append path error!')

from src.parser.structs import *


class MapParser:
    def __init__(self):
        self.__roads = []
        self.__junctions = []
        self.__lanes = []
        self.__lane_sections = None
        logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
        self.__logger = logging.getLogger(__name__)

    def parse(self, file_path):
        """
        解析OpenDrive文件
        :param file_path:文件路径
        :return: None
        """
        tree = ET.parse(file_path)
        root = tree.getroot()
        if root.tag != 'OpenDRIVE':
            raise RuntimeError('This file is not an OpenDrive file')
        self.__parse_roads(root)
        # self.__logger.info(f'This file contains {len(self.__roads)} road tags.')
        self.__parse_junctions(root)
        # self.__logger.info(f'This file contains {len(self.__junctions)} junction tags.')
        # self.__logger.info(f'This file contains {len(self.__lanes)} lane tags of type driving')
        # for lane in self.lanes:
        #     self.logger.info(f'road_id={lane.road_id}, lane_id={lane.lane_id}, lane_type={lane.type_}')

    def __parse_roads(self, root):
        """
        解析OpenDrive标签下的所有road标签
        :param root: OpenDrive标签
        :return: None
        """
        for r in root.iter('road'):
            road = Road(r.get('name'), float(r.get('length')), int(r.get('id')), int(r.get('junction')))
            # 解析road下面的link标签
            link = r.find('link')
            road_link = self.__parse_road_link(link)
            # 解析road下面的type标签
            type_ = r.find('type')
            road_type = self.__parse_road_type(type_)
            # 解析road下面的lanes标签
            lanes = r.find('lanes')
            offset = lanes.find('laneOffset')
            section = lanes.find('laneSection')
            lane_offset = self.__parse_lane_offset(offset)
            lane_section = self.__parse_lane_section(road.id_, section)
            road.road_link = road_link
            road.road_type = road_type
            road.road_lanes = RoadLanes(lane_offset, lane_section)
            self.__roads.append(road)
            # self.logger.info(road)

    def __parse_road_link(self, link):
        """
        解析road标签下的link标签
        :param link: link标签
        :return: RoadLink
        """
        if link is None:
            # self.__logger.error('link is None')
            return None
        pre = link.find('predecessor')
        suc = link.find('successor')
        road_link = RoadLink()
        if pre is not None:
            road_link.pre_type = pre.get('elementType')
            road_link.pre_id = int(pre.get('elementId'))
            if road_link.pre_type == 'road':
                road_link.pre_cont_point = pre.get('contactPoint')
        if suc is not None:
            road_link.suc_type = suc.get('elementType')
            road_link.suc_id = int(suc.get('elementId'))
            if road_link.suc_type == 'road':
                road_link.suc_cont_point = suc.get('contactPoint')
        return road_link

    def __parse_road_type(self, type_):
        """
        解析road下的type标签
        :param type_: type标签
        :return: RoadType
        """
        if type_ is None:
            return None
        speed = type_.find('speed')
        road_type = RoadType(float(type_.get('s')), type_.get('type'), int(speed.get('max')), speed.get('unit'))
        return road_type

    def __parse_lane_offset(self, offset):
        """
        解析road-lanes标签下面的laneOffset标签
        :param offset: laneOffset标签
        :return: RoadLaneOffset
        """
        return RoadLaneOffset(float(offset.get('s')), float(offset.get('a')), float(offset.get('b')),
                              float(offset.get('c')), float(offset.get('d')))

    def __parse_lane_section(self, road_id, section):
        """
        解析road-lanes标签下面的laneSection标签
        :param road_id: 该laneSection所属road的id
        :param section: laneSection标签
        :return: RoadLaneSection
        """
        left = section.find('left')
        right = section.find('right')
        lanes = []
        if left is not None:
            left_lanes = self.__parse_lanes(road_id, left.findall('lane'))
            if left_lanes is not None and len(left_lanes) > 0:
                lanes.extend(left_lanes)
        if right is not None:
            right_lanes = self.__parse_lanes(road_id, right.findall('lane'))
            if right_lanes is not None and len(right_lanes) > 0:
                lanes.extend(right_lanes)
        self.__lanes.extend(lanes)
        return RoadLaneSection(road_id, float(section.get('s')), lanes)

    def __parse_lanes(self, road_id, lanes):
        """
        解析所有lane元素
        :param road_id: 所属road的id
        :param lanes: lane列表
        :return: structs.Lane[]
        """
        results = []
        for l in lanes:
            if l.get('type') == 'driving':
                lane = Lane(road_id, int(l.get('id')), l.get('type'), l.get('level'))
                link = l.find('link')
                width = l.find('width')
                if link is not None:
                    pre = link.find('predecessor')
                    suc = link.find('successor')
                    if pre is not None:
                        lane.pre_id = int(pre.get('id'))
                    if suc is not None:
                        lane.suc_id = int(suc.get('id'))
                if width is not None:
                    lane.set_width(float(width.get('sOffset')), float(width.get('a')), float(width.get('b')),
                                   float(width.get('c')), float(width.get('d')))
                results.append(lane)
        return results

    def __parse_junctions(self, root):
        """
        解析OpenDrive标签下面所有的junction标签
        :param root: OpenDrive标签
        :return: None
        """
        for j in root.iter('junction'):
            junction = Junction(int(j.get('id')), j.get('name'))
            connections = []
            for c in j.iter('connection'):
                connections.append(self.__parse_junction_connection(c))
            junction.connections = connections
            self.__junctions.append(junction)
            # self.logger.info(junction)

    def __parse_junction_connection(self, connection):
        """
        解析junction标签下面的connection标签
        :param connection: connection标签
        :return: JunctionConnection
        """
        conn = JunctionConnection(int(connection.get('id')), int(connection.get('incomingRoad')),
                                  int(connection.get('connectingRoad')), connection.get('contactPoint'))
        lane_links = []
        for l in connection.iter('laneLink'):
            lane_links.append(JunctionLaneLink(int(l.get('from')), int(l.get('to'))))
            conn.lane_links = lane_links
        return conn

    def get_roads(self):
        return self.__roads

    def get_junctions(self):
        return self.__junctions

    def get_lanes(self):
        return self.__lanes

    def get_all_lane_sections(self):
        """
        获取所有road下面的laneSection信息
        :return: structs.RoadLaneSection[]
        """
        if self.__lane_sections is not None:
            return self.__lane_sections
        lane_sections = []
        for r in self.__roads:
            section = r.road_lanes.lane_section
            if section is not None:
                lane_sections.append(section)
        self.__logger.info(f'This map contains {len(lane_sections)} laneSections.')
        self.__lane_sections = lane_sections
        return lane_sections

    def get_junction_by_id(self, id_):
        """
        根据junction的id获取junction
        :param id_: int. junction的id
        :return: structs.Junction. 对应OpenDRIVE文件中的junction标签
        """
        for junction in self.__junctions:
            if junction.id_ == id_:
                return junction
        return None

    def get_road_by_id(self, id_):
        """
        根据road的id获取road
        :param id_: int. road的id
        :return: structs.Road. 对应OpenDRIVE文件中的road标签
        """
        for road in self.__roads:
            if road.id_ == id_:
                return road
        return None

    def get_lane(self, road_id, lane_id):
        """
        通过road id和lane id获取lane
        :param road_id: lane所属的road的id
        :param lane_id: lane的id
        :return: structs.Lane
        """
        for lane in self.__lanes:
            if lane.lane_id == lane_id and lane.road_id == road_id:
                return lane
        return None

    def clear(self):
        """
        清空，可用于下一次解析
        :return: None
        """
        self.__roads.clear()
        self.__junctions.clear()
        self.__lanes.clear()
        self.__lane_sections = None
