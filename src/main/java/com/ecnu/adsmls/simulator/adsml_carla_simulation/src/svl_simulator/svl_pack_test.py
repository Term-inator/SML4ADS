"""
@Project : AutonomousDrivingSimulation 
@File : svl_pack_test.py
@Author : zheng_chenghang
@Date : 2022/5/7 19:03 
"""
import math
import lgsvl
from VehicleController import VehicleController

dt = 0.1
max_throt = 0.75
max_brake = 0.3
max_steer = 0.8
args_lateral_dict = {'k_p': 0.8, 'k_i': 0.05, 'k_d': 0.1, 'dt': dt}
args_longitudinal_dict = {'k_p': 1.0, 'k_i': 0.05, 'k_d': 0, 'dt': dt}


def next_point(step=3.0):
    """

    :return:
    """
    currentTransform = ego.transform
    currentPoint = sim.map_point_on_lane(currentTransform.position)
    forward = lgsvl.utils.transform_to_forward(currentPoint)
    currentPoint.position += forward * step
    target_point = sim.map_point_on_lane(currentPoint.position)
    return target_point


sim = lgsvl.Simulator(address="localhost", port=8181)
print(sim)
if sim.current_scene != "06773677-1ce3-492f-9fe2-b3147e126e27":
    sim.load(scene="06773677-1ce3-492f-9fe2-b3147e126e27")
else:
    sim.reset()
print("map load finished")
sim.reset()
print('start add agent')
spawn_point = sim.get_spawn()[1]
ego: lgsvl.EgoVehicle = sim.add_agent(name="a17d7aa2-5b63-4f48-8e5f-01b6c8f3a7d3", agent_type=lgsvl.AgentType.EGO)
controller = VehicleController(ego, args_lateral_dict, args_longitudinal_dict)
# ego: lgsvl.NpcVehicle = sim.add_agent(name="SUV", agent_type=lgsvl.AgentType.NPC)
state = lgsvl.AgentState()
state.transform = lgsvl.Transform(spawn_point.position, spawn_point.rotation)
forward_vec = lgsvl.utils.transform_to_forward(state.transform)
right_vec = lgsvl.utils.transform_to_right(state.transform)
state.velocity = forward_vec * 10
ego.state = state
print("add agent finished")
for i in range(20):
    target = next_point()
    control = controller.run_step(10, target)
    ego.apply_control(control, True)
    print('throttle: {0}, brake: {1}, steer: {2}'.format(control.throttle, control.braking, control.steering))
    sim.run(time_limit=1.0)
    print('speed: {0}'.format(ego.state.speed))

