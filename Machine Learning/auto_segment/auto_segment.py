import	numpy  as  np
import	cv2
from	matplotlib  import pyplot  as	plt
import os
import  sys
from google.colab.patches import cv2_imshow
from multiprocessing import Pool

import resource, sys
resource.setrlimit(resource.RLIMIT_STACK, (2**31,-1))
sys.setrecursionlimit(10**9)


def check_flower(img, thresh, height, width):
    """get flowers' boundary

    Args:
        img (list): 2d image array
        thresh (int): pixel value threshold
        height (int): image height
        width (int): image width

    Returns:
        list: boundary information, element: [x_min,y_min,x_max,y_max,flower points/total points,boundary area]
    """
    visted = set()
    area = []
    for y, row in enumerate(img):
        for x, val in enumerate(row):
            if (x,y) in visted:
                continue
            if val >= thresh:
                points = set()
                bounds = [width,height,0,0]
                image_bfs(img,thresh,x,y,height, width, points, bounds)
                visted |= points
                if (bounds[2]-bounds[0])*(bounds[3]-bounds[1]) > height*width*0.10:
                    area.append((*bounds, len(points),len(points)*1./((bounds[2]-bounds[0])*(bounds[3]-bounds[1])),(bounds[2]-bounds[0])*(bounds[3]-bounds[1])))
    return area

def image_bfs(img, thresh, x, y, height, width, points, bound):
    """BFS find object boundary

    Args:
        img (list): 2d list for image
        thresh (int): pixel value threshold
        x (int): x coordinate
        y (int): y coordinate
        height (int): image height
        width (int): image width
        points (set): checked points set
        bound (list): boundary coordinates [x_min,y_min,x_max,y_max]
    """
    if (x,y) in points or img[y][x] < thresh:        
        return
    points.add((x,y))
    bound[0] = min(x,bound[0])
    bound[1] = min(y,bound[1])
    bound[2] = max(x,bound[2])
    bound[3] = max(y,bound[3])
    if x > 0 and (x-1, y) not in points:
        image_bfs(img, thresh, x-1, y, height, width, points, bound)
    if y > 0 and (x, y-1) not in points:
        image_bfs(img, thresh, x, y-1, height, width, points, bound)
    if x < width-1 and (x+1, y) not in points:
        image_bfs(img, thresh, x+1, y, height, width, points, bound)
    if y < height-1 and (x, y+1) not in points:
        image_bfs(img, thresh, x, y+1, height, width, points, bound)

def area_check(area,i1,i2):
    """Check whether overlap happened between segment, drop the worse one

    Args:
        area (list): list contains all segment info  
        i1 (int): index of segment
        i2 (int): index of segment

    Returns:
        int: dropped segment index 
    """
    x_l = max(area[i1][0],area[i2][0])
    y_t = max(area[i1][1],area[i2][1])
    x_r = min(area[i1][2],area[i2][2])
    y_b = min(area[i1][3],area[i2][3])
    if y_t > y_b or x_l > x_r:
        return None
    # print('area check',(y_b-y_t)*(x_r-x_l),area[i1][4],area[i2][4])
    if (y_b-y_t)*(x_r-x_l) <= 0.6*min(area[i1][6],area[i2][6]):
        return None
    elif (y_b-y_t)*(x_r-x_l) >= min(area[i1][6],area[i2][6]):
        return i1 if area[i1][4]<area[i2][4] else i2
    else:
        return i1 if area[i1][5]<area[i2][5] else i2

def image_segment(path, target_path,thresh = 128):
    """Segment Images

    Args:
        path (String): image path
        target_path (string): segment store path
        thresh (int, optional): pixel value threshold. Defaults to 128.
    """
    # print('{:.2f} MB'.format(os.stat(path).st_size/(2**20)))

    if os.stat(path).st_size > 1024*1.5*1024:
        # print('too large')
        return
    img = cv2.imread(path, cv2.IMREAD_COLOR)
    # print('shape',img.shape)
    height, width, _ = img.shape
    if height>=1500 or width >= 1500:
        # print('size too large')
        return 
    # print(os.getpid(),path)
    res = []
    # cv2_imshow(img)
    for i in range(3):
        res.extend(check_flower(img[:,:,i].tolist(),thresh,height, width))
    # print(res)
    passed = set()
    for ind,crop in enumerate(res):
        if ind in passed:
            continue
        for i in range(ind+1,len(res)):
            # print(area_check(res,ind,i))
            passed.add(area_check(res,ind,i))
            if ind in passed:
                continue
        # cv2_imshow(img[crop[1]:crop[3],crop[0]:crop[2]])
        file_name = target_path + '{}'.format('_'.join(map(str,crop[:4]))) + '_' + path.split('/')[-1]
        # print(file_name)
        cv2.imwrite(file_name,img[crop[1]:crop[3],crop[0]:crop[2]])
    # print(passed)

def seg_folder(folder,target,checked):
    """load flowers from folder

    Args:
        folder (string): image source folder path
        target (string): segment save path
        checked (set): avoid images segment again
    """
    print('start',os.getpid(),folder)
    count = 0
    for img in os.listdir(folder):
        try:
            if img in checked:
                continue
            image_segment(os.path.join(folder,img),target)
            count += 1
        except Exception as e:
            print(os.getpid(),e)
            continue
    print('finished',os.getpid(),folder,count)

    path = '/content/images/'

if __name__ == '__main__':
    types = os.listdir(path)
    types.sort()
    dist = '/content/drive/My Drive/master_project/crop_3/'
    if not os.path.isdir(dist):
        os.mkdir(dist)
    print(types)
    p = Pool()
    for flower in types:
        if not os.path.isdir(dist+flower):
            os.mkdir(dist+flower)
        l = os.listdir(dist+flower+'/')
        l = map(lambda x: '_'.join(x.split('_')[4:]),l)
        l = set(l)
        try:
            res = p.apply_async(seg_folder,(path+flower+'/',dist+flower+'/',l))
            # seg_folder(path+flower+'/',dist+flower+'/',l)
            
        except Exception as e:
            print(e)
    p.close()
    p.join()
    print('all finished')