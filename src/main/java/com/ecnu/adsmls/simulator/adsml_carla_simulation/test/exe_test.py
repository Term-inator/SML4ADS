"""
@Project : AutonomousDrivingSimulation 
@File : exe_test.py
@Author : zheng_chenghang
@Date : 2022/4/8 15:28 
"""

class Car:
    def __init__(self):
        self.id = 0
        self.model = "aaa"
        self.speed = 10


if __name__ == "__main__":
    di = {}
    di['car1'] = 1
    car2 = Car()
    car2.model = 'bbb'
    di['car2'] = car2
    car = None
    for k, v in di.items():
        exec(f'car = v')
        print(car)
    car2 = None
