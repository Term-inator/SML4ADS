"""
@Project : AutonomousDrivingSimulation 
@File : args_parse_test.py
@Author : zheng_chenghang
@Date : 2022/5/1 18:30 
"""
import argparse
import os


def parse_args():
    """

    :return:
    """
    curr_folder = os.getcwd()
    project_path = curr_folder[:curr_folder.rfind(os.path.sep)+1]
    scenario_img_path = project_path + 'store/scenario/img/'
    mp4_path = project_path + 'store/scenario/mp4/default.mp4'
    scene_path = project_path + 'store/scene/'
    parser = argparse.ArgumentParser()
    parser.add_argument('path', help='ADSML文件路径', type=str)
    parser.add_argument('-scene', help='静态场景展示得图片数量', type=int)
    parser.add_argument('-scenario_img_path', help='仿真过程存储图片的路径', type=str)
    parser.add_argument('-mp4_path', help='仿真过程完成后生成的视频文件路径', type=str)
    parser.add_argument('-scene_img_path', help='静态场景展示图片的存储路径', type=str, default=scene_path)
    parser.add_argument('-recorder', help='使用recorder的文件存储路径', type=str, default='')
    parser.add_argument('-ip', help='仿真器ip', type=str, default='127.0.0.1')
    parser.add_argument('-port', help='仿真器端口', type=int, default=2000)
    parser.add_argument('-csv_path', help='记录时空轨道数据，给出文件路径', type=str, default='')
    return parser.parse_args()


if __name__ == '__main__':
    args = vars(parse_args())
    print(args)
