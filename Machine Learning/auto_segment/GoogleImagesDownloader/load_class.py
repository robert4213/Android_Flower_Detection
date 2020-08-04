from collections import defaultdict
def load_class(path = 'E:/CMPE_master_project/Plant_detection_Application/model_data/class_new.txt'):
    cl = defaultdict(int)
    folder = []
    with open(path,mode='r') as f:
        for line in f:
            cl[line.rstrip("\n")] = 0
            folder.append(line.rstrip("\n"))
    return cl,folder

# if __name__ == '__main__':
#     print(load_class())
