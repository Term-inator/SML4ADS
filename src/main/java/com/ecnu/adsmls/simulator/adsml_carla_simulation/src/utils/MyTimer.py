"""
@Project : AutonomousDrivingSimulation
@Time : 2022/2/14-11:57
@Author : zch
@Description :
"""
from threading import Timer
from datetime import datetime


class MyTimer(object):
    # 封装定时器类，每隔一定时间直行一次
    def __init__(self, start_time, interval, callback_proc, args=None, kwargs=None):
        """
        构造方法
        :param start_time: 开始时间
        :param interval: 间隔时间
        :param callback_proc: 回调函数
        :param args: tuple.传给被调用函数的参数
        :param kwargs: dict.传给被调用函数的参数
        """
        self.__timer = None
        self.__start_time = start_time
        self.__interval = interval
        self.__callback_pro = callback_proc
        self.__args = args if args is not None else []
        self.__kwargs = kwargs if kwargs is not None else {}

    def exec_callback(self, args=None, kwargs=None):
        """
        直行回调函数
        :param args:
        :param kwargs:
        :return:
        """
        self.__callback_pro(*self.__args, **self.__kwargs)
        self.__timer = Timer(self.__interval, self.exec_callback)
        self.__timer.start()

    def start(self):
        interval = self.__interval - (datetime.now().timestamp() - self.__start_time.timestamp())
        print(interval)
        self.__timer = Timer(interval, self.exec_callback)
        self.__timer.start()

    def cancel(self):
        if self.__timer is not None:
            self.__timer.cancel()
            self.__timer = None

    def reinit(self, start_time=None, interval=None, callback_proc=None, args=None, kwargs=None):
        self.__start_time = start_time if start_time is not None else datetime.now()
        self.__interval = interval if interval is not None else self.__interval
        self.__callback_pro = callback_proc if callback_proc is not None else self.__callback_pro
        self.__args = args if args is not None else self.__args
        self.__kwargs = kwargs if kwargs is not None else self.__kwargs
