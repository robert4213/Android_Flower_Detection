from PIL import Image
from server.model.yolo import YOLO

# from server.model.nets.yolo3 import yolo_body
# from keras.layers import Input
# from server.model.yolo import YOLO
# from PIL import Image
# import numpy as np

def predict_species(image_path):
    yolo = YOLO()   ### session keep open
    image = Image.open(image_path)
    boxes = yolo.detect_image_boxes(image)
    print("YOLO Detect Result: {}".format(boxes))
    yolo.close_session()
    return boxes
    # return [['rose', [55, 175, 438, 497], 0.97702414]]
