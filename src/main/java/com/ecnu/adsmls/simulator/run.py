import argparse

parser = argparse.ArgumentParser()
parser.add_argument('--file', type=str, help='file path.')

args = parser.parse_args()

print(args.file)