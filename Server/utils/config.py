# Config file
import os

global MODEL_PATH
global ANCHORS_PATH
global CLASSES_PATH
global FONT_PATH

# BASE_PATH = "/Users/xiaoting/Documents/Courses/295_Project/Android_Flower_Detection"

BASE_PATH = os.path.dirname(__file__)
PARENT_PATH = os.path.dirname(BASE_PATH)


# MODEL_PATH = "/Users/xiaoting/Documents/Courses/295_Project/Android_Flower_Detection/server/model/logs/last1.h5"
# ANCHORS_PATH = "/Users/xiaoting/Documents/Courses/295_Project/Android_Flower_Detection/server/model/model_data/yolo_anchors.txt"

# CLASSES_PATH = "/Users/xiaoting/Documents/Courses/295_Project/Android_Flower_Detection/server/model/model_data/class.txt"
# FONT_PATH = "/Users/xiaoting/Documents/Courses/295_Project/Android_Flower_Detection/server/model/font/simhei.ttf"
MODEL_PATH = PARENT_PATH + "/model/logs/last1.h5"
ANCHORS_PATH = PARENT_PATH + "/model/model_data/yolo_anchors.txt"
CLASSES_PATH = PARENT_PATH + "/model/model_data/class.txt"
FONT_PATH = PARENT_PATH + "/model/font/simhei.ttf"
