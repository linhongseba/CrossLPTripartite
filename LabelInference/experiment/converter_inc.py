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

#graph-1.BCD.DEG.05.result.txt
graph=['1','30','37'];
algorithm=['GDG','GDR','MAG','MAR'];
preprocessing=['DEG','RND','SHR'];


for g in range(0,3):
    x=1;
    sheet = xls.add_sheet('graph%s'%graph[g],cell_overwrite_ok=True)
    for i in range(1,10):
        sheet.write(i,0,"%d"% i+"0%")
    sheet.write(0,0,"new-data-percentage")
    sheet.write(0,1,"recompute-time")
    sheet.write(0,2,"incremental-time")
    sheet.write(0,3,"recompute-accuracy")
    sheet.write(0,4,"incremental accuracy")
    for a in range(2,3):
        figureNum=g*4+a+1;
        plt.figure(figureNum)
        plt.suptitle('graph-'+graph[g]+'.'+algorithm[a])
        for p in range(0,1):
            for i in range(1,10):
                filename='graph-'+graph[g]+'.'+algorithm[a]+'.'+preprocessing[p]+'.05.'+str(i)+'.result.txt';
                f = open('results/'+filename)
                #print filename;
                i=0
                while True:
                    line = f.readline()
                    if not line:
                        break
                    if line.find('Processed in ')>=0:
                        sheet.write(x,1,line[13:])
                    if line.find('Accuracy ')>=0:
                        sheet.write(x,3,line[11:])
                        break
                while True:
                    line = f.readline()
                    if line.find('Increment')>=0:
                        break
                while True:
                    line = f.readline()
                    if not line:
                        break
                    if line.find('Processed in ')>=0:
                        sheet.write(x,2,line[13:])
                    if line.find('Accuracy ')>=0:
                        sheet.write(x,4,line[11:])
                        break
                x+=1

#plt.show()
xls.save('result.xls')
