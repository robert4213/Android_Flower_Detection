from server.model.nets.yolo3 import yolo_body
from keras.layers import Input
from server.model.yolo import YOLO
from PIL import Image
import numpy as np
from datetime import datetime

def predict_species(image_path):
    yolo = YOLO()   ### session keep open
    image = Image.open(image_path)
    boxes = yolo.detect_image_boxes(image)
    print("YOLO Detect Result: {}".format(boxes))
    yolo.close_session()
    return boxes
    # return [['rose', [55, 175, 438, 497], 0.97702414]]


# if __name__ == '__main__':
#     yolo = YOLO()
#     # x = 10
#     # photo = []
#     # with open('2007_test.txt') as f:
#     #     file = f.readlines()
#     #     # print(file[0])
#     # for line in file:
#     #     photo.append(line.split()[0])
#     # np.random.seed(int(datetime.timestamp(datetime.now())))
#     # np.random.shuffle(photo)
#     # np.random.seed(None)
#     # for i in range(x):
#     if True:
#         # img = input('Input image filename:')
#         img ='/Users/xiaoting/Documents/Courses/295_Project/Plant_Identification/rose3.jpeg'
#         # print(photo[i])
#         try:
#             image = Image.open(img)
#             print("Image opened")
#         except:
#             print('Open Error! Try again!')
#             # continue
#         else:
#             # [[type,[top,left,bottom,right],score]
#             boxes = yolo.detect_image_boxes(image)
#             print("Return Boxes:{}".format(boxes))
#             r_image = yolo.detect_image(image)
#             r_image.show()
#     yolo.close_session()
