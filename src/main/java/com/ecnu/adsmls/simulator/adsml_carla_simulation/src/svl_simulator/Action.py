"""
@Project : AutonomousDrivingSimulation 
@File : Action.py
@Author : zheng_chenghang
@Date : 2022/5/15 14:54 
"""


class Action:
    """
    Action 类
    """
    def __init__(self, client, vehicle):
        self.client = client
        self.vehicle = vehicle

    def next_wp(self, state):
        """

        :param state:
        :return:
        """

