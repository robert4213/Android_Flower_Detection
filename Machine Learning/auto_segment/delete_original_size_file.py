import os

path = 'E:\\CMPE_master_project\\images'

if __name__ == '__main__':
    types = os.listdir(path)
    types = map(lambda x:os.path.join(path,x),types)
    
    for t in types:
        try:
            for img in os.listdir(t):
                if 'o.jpg' in img.split('_'):
                    print(os.path.join(t,img))
                    os.remove(os.path.join(t,img))
        except Exception as e:
            print(e)
            continue

