import datetime
import time
import os
import sys
import xlwt 
import numpy
import math
import matplotlib.pyplot as plt

print 'converting xls ... '

x = 0       
y = 0       

xls=xlwt.Workbook()
sheet = xls.add_sheet('sheet1',cell_overwrite_ok=True)
sheet.write(0,2,"GDG")
sheet.write(0,5,"GDR")
sheet.write(0,8,"MAG")
sheet.write(0,11,"MAR")
for i in range(0,4):
    sheet.write(1,2+3*i,"Degree")
    sheet.write(1,3+3*i,"Random")
    sheet.write(1,4+3*i,"Heuristic")


#graph-1.BCD.DEG.05.result.txt
graph=['1','30','37'];
algorithm=['GDG','GDR','MAG','MAR'];
preprocessing=['DEG','RND','SHR'];

x=2;
y=2;

for g in range(0,3):
    sheet.write(x,0,'graph'+graph[g])
    sheet.write(x,1,'Accuracy')
    sheet.write(x+1,1,'Objective')
    sheet.write(x+2,1,'Time comsuming')
    for a in range(0,4):
        figureNum=g*4+a+1;
        plt.figure(figureNum)
        plt.suptitle('graph-'+graph[g]+'.'+algorithm[a])
        plt.xlabel(u'iteration times')
        plt.ylabel(u'lg(objective)')
        maxobj=0.0;
        minobj=10000000.0;
        for p in range(0,3):
            filename='graph-'+graph[g]+'.'+algorithm[a]+'.'+preprocessing[p]+'.05.result.txt';
            f = open('results/'+filename)
            #print filename;
            i=0
            obj=''
            time=''
            x_list=[]
            y_list=[]
            while True:
                line = f.readline()
                if not line:
                    break
                if line.find('ObjValue')>=0:
                    i+=1
                    obj=line[11:]
                    obj_now=math.log(float(obj),10);
                    x_list.append(obj_now);
                    y_list.append(obj_now);
                    if obj_now>maxobj:
                        maxobj=obj_now
                    if obj_now<minobj:
                        minobj=obj_now
                if line.find('Processed in ')>=0:
                    time=line[16:]
                if line.find('Accuracy ')>=0:
                    sheet.write(x,y,line[11:])
                    sheet.write(x+1,y,obj)
                    sheet.write(x+2,y,time)
                    y+=1;
                    break
            ax = plt.subplot(1,1,1)
            lines,=ax.plot(y_list,label=preprocessing[p])


            handles, labels = ax.get_legend_handles_labels()
            ax.legend(handles[::-1], labels[::-1])
            plt.axis([0, i, minobj, maxobj])
            plt.savefig('figures/'+'graph-'+graph[g]+'.'+algorithm[a]+'.05.pdf')  
    x+=3
    y=2
#plt.show()
xls.save('result.xls')
