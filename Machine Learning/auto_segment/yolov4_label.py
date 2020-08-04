import os
from collections import defaultdict
import shutil
from PIL import Image,ImageOps

if __name__ == '__main__':
    path_seg = 'E:/CMPE_master_project/segment_algorithm/segment/'
    path_img = 'E:/CMPE_master_project/segment_algorithm/images/'    
    tar = 'E:/CMPE_master_project/darknet/data/'
    if not os.path.isdir(tar):
        os.mkdir(tar)
    dest = '/content/data/'
    train = open('E:/CMPE_master_project/darknet/train.txt','w')
    val = open('E:/CMPE_master_project/darknet/test.txt','w')
    ratio = 8

    for ind,f_type in enumerate(os.listdir(path_seg)):
        count = 0
        dic = defaultdict(list)
        for img in os.listdir(path_seg+f_type):
            loc = map(int,img.split('_')[:4])
            name = '_'.join(img.split('_')[4:])
            dic[name].append(list(loc))
        for img,l in dic.items():
            # print(img)
            image = Image.open(path_img+f_type+'/'+img)
            x,y = image.size                
            if x <= 0 or y <= 0:
                print(path_img+f_type+'/'+img)
                continue
            if count % 10 >= ratio:
                val.write(dest+f_type+'_'+img+'\n')
            else:
                train.write(dest+f_type+'_'+img+'\n')           

            file = open(tar+f_type+'_'+img[:-3]+'txt','w')
            # print(f_type,x,y)
            # print(l)
            for loc in l:                
                x_c = (loc[2]+loc[0])/(2.0*x)
                y_c = (loc[3]+loc[1])/(2.0*y)
                w = (loc[2]-loc[0])/(2.0*x)
                h = (loc[3]-loc[1])/(2.0*y)
                file.write(' '.join([str(ind)]+list(map(str,[x_c,y_c,w,h])))+'\n')
            shutil.copy2(path_img+f_type+'/'+img, tar+f_type+'_'+img)
            file.close()
            if len(dic) < 1000:
                roate = image.rotate(270, expand=True)
                roate.save(tar+f_type+'_'+img[:-4]+'_r90.jpg')
                if count % 10 >= ratio:
                    val.write(dest+f_type+'_'+img[:-4]+'_r90.jpg'+'\n')
                else:
                    train.write(dest+f_type+'_'+img[:-4]+'_r90.jpg'+'\n')
                file = open(tar+f_type+'_'+img[:-4]+'_r90.txt','w')
                file.write(' '.join([str(ind)]+list(map(str,[1-y_c,x_c,h,w])))+'\n')
                file.close()
            if len(dic) < 500:
                flip = ImageOps.flip(image)
                flip.save(tar+f_type+'_'+img[:-4]+'_flip.jpg')
                if count % 10 >= ratio:
                    val.write(dest+f_type+'_'+img[:-4]+'_flip.jpg'+'\n')
                else:
                    train.write(dest+f_type+'_'+img[:-4]+'_flip.jpg'+'\n')
                file = open(tar+f_type+'_'+img[:-4]+'_flip.txt','w')
                file.write(' '.join([str(ind)]+list(map(str,[x_c,1-y_c,w,h])))+'\n')
                file.close()

                mirror = ImageOps.mirror(roate)
                mirror.save(tar+f_type+'_'+img[:-4]+'_mirror.jpg')
                if count % 10 >= ratio:
                    val.write(dest+f_type+'_'+img[:-4]+'_mirror.jpg'+'\n')
                else:
                    train.write(dest+f_type+'_'+img[:-4]+'_mirror.jpg'+'\n')
                file = open(tar+f_type+'_'+img[:-4]+'_mirror.txt','w')
                file.write(' '.join([str(ind)]+list(map(str,[y_c,x_c,h,w])))+'\n')
                file.close()            
            count+=1
    train.close()
    val.close()

