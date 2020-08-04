import tensorflow as tf
physical_devices = tf.config.experimental.list_physical_devices('GPU')
if len(physical_devices) > 0:
    tf.config.experimental.set_memory_growth(physical_devices[0], True)
from absl import app, flags, logging
from absl.flags import FLAGS

from core.yolov4 import filter_boxes
from core.config import cfg
import core.utils as utils
from tensorflow.python.saved_model import tag_constants
from PIL import Image
import cv2
import numpy as np
from tensorflow.compat.v1 import ConfigProto
from tensorflow.compat.v1 import InteractiveSession
import time

# flags.DEFINE_string('framework', 'tf', '(tf, tflite, trt')
# flags.DEFINE_string('weights', './checkpoints/yolov4-416',
#                     'path to weights file')
# flags.DEFINE_integer('size', 416, 'resize images to')
# flags.DEFINE_boolean('tiny', False, 'yolo or yolo-tiny')
# flags.DEFINE_string('model', 'yolov4', 'yolov3 or yolov4')
# flags.DEFINE_string('image', './data/3.jpg', 'path to input image')
# flags.DEFINE_string('output', 'result.png', 'path to output image')
# flags.DEFINE_float('iou', 0.45, 'iou threshold')
# flags.DEFINE_float('score', 0.25, 'score threshold')
# # flags.DEFINE_string('export_dir', './data', 'export directory')

weights = './yolov4/checkpoints/yolov4-416'
iou = 0.45
score = 0.25
input_size = 416
image_path = './yolov4/data/3.jpg'
output = 'result.png'
classes_list=utils.read_class_names(cfg.YOLO.CLASSES)
num_classes = len(classes_list)
count = 0

def initial():
    global saved_model_loaded,infer
    config = ConfigProto()
    config.gpu_options.allow_growth = True
    session = InteractiveSession(config=config)
    STRIDES, ANCHORS, NUM_CLASS, XYSCALE = utils.load_config()    

    # original_image = cv2.imread(image_path)
    # original_image = cv2.cvtColor(original_image, cv2.COLOR_BGR2RGB)

    # image_data = utils.image_preprocess(np.copy(original_image), [input_size, input_size])
    # image_data = cv2.resize(original_image, (input_size, input_size))
    # image_data = image_data / 255.
    # image_data = image_data[np.newaxis, ...].astype(np.float32)

    # images_data = []
    # for i in range(1):
    #     images_data.append(image_data)
    # images_data = np.asarray(images_data).astype(np.float32)

    saved_model_loaded = tf.saved_model.load(export_dir=weights, tags=[tag_constants.SERVING])
    infer = saved_model_loaded.signatures['serving_default']
    # batch_data = tf.constant(images_data)
    # pred_bbox = infer(batch_data)
    # for key, value in pred_bbox.items():
    #     boxes = value[:, :, 0:4]
    #     pred_conf = value[:, :, 4:]
    # boxes, scores, classes, valid_detections = tf.image.combined_non_max_suppression(
    #     boxes=tf.reshape(boxes, (tf.shape(boxes)[0], -1, 1, 4)),
    #     scores=tf.reshape(
    #         pred_conf, (tf.shape(pred_conf)[0], -1, tf.shape(pred_conf)[-1])),
    #     max_output_size_per_class=50,
    #     max_total_size=50,
    #     iou_threshold=iou,
    #     score_threshold=score
    # )
    # pred_bbox = [boxes.numpy(), scores.numpy(), classes.numpy(), valid_detections.numpy()]
    # print(pred_bbox)
    # image = utils.draw_bbox(original_image, pred_bbox)
    # # image = utils.draw_bbox(image_data*255, pred_bbox)
    # image = Image.fromarray(image.astype(np.uint8))
    # # image.show()
    # image = cv2.cvtColor(np.array(image), cv2.COLOR_BGR2RGB)
    # cv2.imwrite(output, image)

def detect(image_path):
    global saved_model_loaded,infer,count
    original_image = cv2.imread(image_path)
    image_h, image_w, _ = original_image.shape
    original_image = cv2.cvtColor(original_image, cv2.COLOR_BGR2RGB)
    image_data = cv2.resize(original_image, (input_size, input_size))
    image_data = image_data / 255.
    images_data = [image_data]
    images_data = np.asarray(images_data).astype(np.float32)
    batch_data = tf.constant(images_data)
    pred_bbox = infer(batch_data)
    for key, value in pred_bbox.items():
        boxes = value[:, :, 0:4]
        pred_conf = value[:, :, 4:]
    boxes, scores, classes, valid_detections = tf.image.combined_non_max_suppression(
        boxes=tf.reshape(boxes, (tf.shape(boxes)[0], -1, 1, 4)),
        scores=tf.reshape(
            pred_conf, (tf.shape(pred_conf)[0], -1, tf.shape(pred_conf)[-1])),
        max_output_size_per_class=50,
        max_total_size=50,
        iou_threshold=iou,
        score_threshold=score
    )
    out_boxes, out_scores, out_classes, num_boxes = [boxes.numpy(), scores.numpy(), classes.numpy(), valid_detections.numpy()]
    # out_boxes, out_scores, out_classes, num_boxes = pred_bbox
    res = []
    for i in range(num_boxes[0]):
        if int(out_classes[0][i]) < 0 or int(out_classes[0][i]) > num_classes: continue
        coor = out_boxes[0][i]
        coor[0] = str(int(coor[0] * image_h))
        coor[2] = str(int(coor[2] * image_h))
        coor[1] = str(int(coor[1] * image_w))
        coor[3] = str(int(coor[3] * image_w))
        coor = ['%.02f'%x for x in coor]
        class_ind = int(out_classes[0][i])
        res.append([classes_list[class_ind],[coor[1], coor[0],coor[3], coor[2]],'%.02f'%out_scores[0][i]])
        # image = cv2.imread(image_path) 
        # image = cv2.rectangle(image, (coor[1], coor[0]), (coor[3], coor[2]), (255, 0, 0) , 2) 
        
        # # Displaying the image  
        # cv2.imwrite(str(count)+'.jpg', image)
        # count += 1
    return res


if __name__ == '__main__':
    initial()
    start = time.time()
    print(detect('./data/3.jpg'))
    print('cost',time.time()-start)
    start = time.time()
    print(detect('./data/4.jpg'))
    print('cost',time.time()-start)
    start = time.time()
    print(detect('./data/5.jpg'))
    print('cost',time.time()-start)
    detect('./data/5.jpg')
    
