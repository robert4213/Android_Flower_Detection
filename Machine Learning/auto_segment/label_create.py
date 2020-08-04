import os
from collections import defaultdict
from PIL import Image,ImageDraw

def add_record(path):
    rec = defaultdict(list)
    # print(path)
    for f in os.listdir(path):
        print(f)
        try:
            data = f.split('_')
            p = '_'.join(data[4:])
            rec[p].append(data[:4])
            # draw box
            # p_90 = p[:-4] + '_90.jpg'
            # if p_90 not in rec:
            #     img = Image.open((path+p).replace('segment','images'))
            #     # im_d = ImageDraw.Draw(img)
            #     # im_d.rectangle(((int(data[0]), int(data[1])), (int(data[2]), int(data[3]))),width=10)
            #     # img.show()
            #     out = img.rotate(90, expand=True)
            #     w,h = out.size
            #     # out_d = ImageDraw.Draw(img)
            #     # out_d.rectangle([w-int(data[3]), int(data[0]), w-int(data[1]), int(data[2])],width=10)
            #     # out.show()
            #     # print((path+p_90).replace('segment','images'))
            #     try:
            #         out.save((path+p_90).replace('segment','images'))
            #     except Exception as e:
            #         print(e)
            #         print('type problem',(path+p).replace('segment','images'))
            #         out = out.convert('RGB')
            #         out.save((path+p_90).replace('segment','images'))
            #         img = img.convert('RGB')
            #         img.save((path+p).replace('segment','images'))

            # rec[p_90].append(map(str,[w-int(data[3]), int(data[0]), w-int(data[1]), int(data[2])]))
        except Exception as e:
            print(e)
            print('file problem',path,f)
            continue
    return rec

if __name__ == "__main__":
    path = './segment/'
    p_2 = './images/'
    target = '/content/images/'
    train = open(p_2+'train_nr.txt','w')
    valid = open(p_2+'val_nr.txt','w')
    cl = open(p_2 +'class_all.txt','w')
    ratio = 8
    for ind,t in enumerate(os.listdir(path)):
        try:            
            rec = add_record(path+t+'/')
            count = 0
            for img,arr in rec.items():
                s = target+t+'/'+img
                for val in arr:
                    val = list(val)
                    val.append(str(ind))
                    s += ' '+','.join(val)
                s += '\n'
                if count%10 >= ratio:
                    valid.write(s)
                else:
                    train.write(s)
                count += 1
            print(t,count)
            cl.write(t.replace('_',' ')+'\n')
        except Exception as e:
            print('error',e)


