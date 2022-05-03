"""
@Project : AutonomousDrivingSimulation 
@File : import_test.py
@Author : zheng_chenghang
@Date : 2022/4/21 14:56 
"""
import sys

file = 'D:/Python/custom.py'


def import_test():
    index = file.rfind("/")
    print(index)
    path = file[:index]
    module = file[index + 1:file.find('.', index)]
    print(f'path = {path}')
    print(f'module = {module}')
    try:
        sys.path.append(path)
    except IndexError:
        print('can not add module path.')
    exec(f'import {module}')
    exec_str = f'custom.hello_test()'
    exec(exec_str)


def exec_test():
    with open(file) as f:
        exec(f.read())
    exec('hello_test()')


if __name__ == "__main__":
    # exec_test()
    import_test()

