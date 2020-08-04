import os

if __name__ == '__main__':
    path = 'E:/CMPE_master_project/segment_algorithm/GoogleImagesDownloader-master/data/'
    folders = os.listdir(path)
    for f in folders:
        print(f)
        os.rename(path+f,path+f.replace(' ','_').lower())
