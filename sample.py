import sys
sys.path.append('./yolov4')
from yolov4 import detect

if __name__ == '__main__':
    detect.initial()
    print('*'*52)
    print('*'*20 + 'start detect'+'*'*20)
    print(detect.detect('./yolov4/data/5.jpg'))
    print(detect.detect('./yolov4/data/3.jpg'))
    print(detect.detect('./yolov4/data/10.png'))