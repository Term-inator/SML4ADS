"""
@Project : AutonomousDrivingSimulation
@Time : 2022/1/13-10:23
@Author : zch
@Description : 定义需要使用的，和OpenDrive文件标签对应的结构体
"""


class RoadLink:
    def __init__(self):
        self.pre_type = None
        self.pre_id = None
        self.pre_cont_point = None
        self.suc_type = None
        self.suc_id = None
        self.suc_cont_point = None

    def __repr__(self) -> str:
        return f'pre_type={self.pre_type};pre_id={self.pre_id};suc_type={self.suc_type};suc_id={self.suc_id}.'


class RoadType:
    def __init__(self, s, type_, max_speed, unit):
        self.s = s
        self.type_ = type_
        self.max_speed = max_speed
        self.unit = unit

    def __repr__(self) -> str:
        return super().__repr__()


class RoadLaneOffset:
    def __init__(self, s, a, b, c, d):
        self.s = s
        self.a = a
        self.b = b
        self.c = c
        self.d = d

    def __repr__(self) -> str:
        return super().__repr__()


class RoadLaneSection:
    def __init__(self, road_id, s, lanes):
        self.road_id = road_id
        self.s = s
        self.lanes = lanes

    def __repr__(self) -> str:
        return f'road_id:{self.road_id};s:{self.s};lanes:{self.lanes}'


class Lane:
    def __init__(self, road_id, lane_id, type_, level, pre_id=0, suc_id=0, w_s=0, w_a=0, w_b=0, w_c=0, w_d=0):
        self.road_id = road_id
        self.lane_id = lane_id
        self.type_ = type_
        self.level = level
        self.pre_id = pre_id
        self.suc_id = suc_id
        self.w_s = w_s
        self.w_a = w_a
        self.w_b = w_b
        self.w_c = w_c
        self.w_d = w_d

    def __repr__(self) -> str:
        return f'road_id:{self.road_id};lane_id:{self.lane_id};type:{self.type_}'

    def set_width(self, w_s=0, w_a=0, w_b=0, w_c=0, w_d=0):
        self.w_s = w_s
        self.w_a = w_a
        self.w_b = w_b
        self.w_c = w_c
        self.w_d = w_d


class RoadLanes:
    def __init__(self, lane_offset, lane_section):
        self.lane_offset = lane_offset
        self.lane_section = lane_section

    def __repr__(self) -> str:
        return super().__repr__()


class Road:
    def __init__(self, name, length, id_, junction, road_link=None, road_type=None, road_lanes=None):
        self.name = name
        self.length = length
        self.id_ = id_
        self.junction = junction
        self.road_link = road_link
        self.road_type = road_type
        self.road_lanes = road_lanes

    def __repr__(self) -> str:
        return super().__repr__()


class JunctionLaneLink:
    def __init__(self, from_, to):
        self.from_ = from_
        self.to = to

    def __repr__(self) -> str:
        return super().__repr__()


class JunctionConnection:
    def __init__(self, id_, in_road, conn_road, cont_point, lane_links=None):
        self.id_ = id_
        self.in_road = in_road
        self.conn_road = conn_road
        self.cont_point = cont_point
        self.lane_links = lane_links

    def __repr__(self) -> str:
        return super().__repr__()


class Junction:
    def __init__(self, id_, name, connections=None):
        self.id_ = id_
        self.name = name
        self.connections = connections

    def __repr__(self) -> str:
        return super().__repr__()
