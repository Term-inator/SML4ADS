import argparse

parser = argparse.ArgumentParser()
parser.add_argument('--file', type=str, help='file path.')
group = parser.add_mutually_exclusive_group()
group.add_argument('--scene', type=int, help='image num under static scenes')
group.add_argument('--scenario', action='store_true', help="dynamic scenes")
# parser.add_argument('--car', type=int, help='car config')

args = parser.parse_args()

print(args.file)
print(args.scene)
print(args.scenario)
# car_config_num = args.car
# car_config_order = ['x', 'y', 'v', 'deviation', 'road']
# car_config = {}
# for i in range(len(car_config_order) - 1, -1, -1):
#     car_config[car_config_order[i]] = car_config_num & 1
#     car_config_num >>= 1
#
# print(car_config)
