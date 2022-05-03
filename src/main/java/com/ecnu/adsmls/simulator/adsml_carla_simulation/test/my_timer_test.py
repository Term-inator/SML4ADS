"""
@Project : AutonomousDrivingSimulation
@Time : 2022/2/14-13:31
@Author : zch
@Description :
"""
from datetime import datetime

from src.utils.MyTimer import MyTimer


def t_tick():
    t += 1
    print(t)


if __name__ == "__main__":
    global t
    t = 0
    start_time = datetime.now()
    timer = MyTimer(start_time, 2, t_tick)
    timer.start()
    while True:
        if datetime.now().timestamp() - start_time.timestamp() >= 10:
            timer.cancel()
            break
