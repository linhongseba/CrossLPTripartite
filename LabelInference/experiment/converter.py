import datetime
import time
import os
import sys
import xlwt 
import numpy
import matplotlib.pyplot as plt

print 'converting xls ... '

x = 0       
y = 0       

xls=xlwt.Workbook()
sheet = xls.add_sheet('sheet1',cell_overwrite_ok=True)
sheet.write(0,2,"BCD")
sheet.write(0,5,"MA")
sheet.write(0,8,"LP")
for i in range(0,3):
    sheet.write(1,2+3*i,"Degree")
    sheet.write(1,3+3*i,"Random")
    sheet.write(1,4+3*i,"Heuristic")


#graph-1.BCD.DEG.05.result.txt
graph=['1','30','37'];
algorithm=['BCD','MA','LP'];
preprocessing=['DEG','RND','SHR'];
training=['05','10'];

x=2;
y=2;

for g in range(0,3):
    for t in range(0,2):
        sheet.write(x,0,'graph'+str(g+1)+'-'+training[t])
        sheet.write(x,1,'Accuracy')
        sheet.write(x+1,1,'Objective')
        sheet.write(x+2,1,'Time comsuming')
        for a in range(0,3):
            figureNum=g*2*3+t*3+a+1;
            plt.figure(figureNum)
            plt.suptitle('graph-'+graph[g]+'.'+training[t]+'.'+algorithm[a])
            maxobj=0.0;
            minobj=10000000.0;
            for p in range(0,3):
                filename='graph-'+graph[g]+'.'+algorithm[a]+'.'+preprocessing[p]+'.'+training[t]+'.result.txt';
                f = open('results/'+filename)
                #print filename;
                i=0
                obj=''
                time=''
                x_list=[]
                y_list=[]
                while True:
                    i+=1
                    line = f.readline()
                    if not line:
                        break
                    if line.find('ObjValue')>=0:
                        obj=line[11:]
                        x_list.append(float(i));
                        y_list.append(float(obj));
                        if float(obj)>maxobj:
                            maxobj=float(obj)
                        if float(obj)<minobj:
                            minobj=float(obj)
                    if line.find('Processed in ')>=0:
                        time=line[16:]
                    if line.find('Accuracy ')>=0:
                        sheet.write(x,y,line[11:])
                        sheet.write(x+1,y,obj)
                        sheet.write(x+2,y,time)
                        y+=1;
                        break
                lines=plt.plot(x_list, y_list)
                if p==0:
                    plt.setp(lines, color='r')
                if p==1:
                    plt.setp(lines, color='g')
                if p==2:
                    plt.setp(lines, color='b')
                plt.axis([0, i, minobj, maxobj])
                plt.savefig('figures/'+'graph-'+graph[g]+'.'+training[t]+'.'+algorithm[a]+'.pdf')  
        x+=3
        y=2
plt.show()
xls.save('result.xls')
