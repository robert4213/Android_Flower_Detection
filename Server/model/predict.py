from PIL import Image
from server.model.yolo import YOLO

def predict_species(image_path):
    yolo = YOLO()   ### session keep open
    image = Image.open(image_path)
    boxes = yolo.detect_image_boxes(image)
    print("YOLO Detect Result: {}".format(boxes))
    yolo.close_session()
    return boxes
    # return [['rose', [55, 175, 438, 497], 0.97702414]]
