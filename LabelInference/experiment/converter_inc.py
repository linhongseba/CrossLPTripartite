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
    sheet.write(0,4,"incremental-accuracy")
    for a in range(2,3):
        for p in range(0,1):
            maxtime=0.0;
            maxaccu=0.0;
            r_time_list=[];
            r_accu_list=[];
            i_time_list=[];
            i_accu_list=[];
            x_list=[];

            for i in range(1,10):
                filename='graph-'+graph[g]+'.'+algorithm[a]+'.'+preprocessing[p]+'.05.'+str(i)+'.result.txt';
                f = open('results/'+filename)
                #print filename;
                x_list.append(str(i*10));
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
                        i_time_list.append(float(line[13:21]));
                        if float(line[13:21])>maxtime:
                            maxtime=float(line[13:21]);
                    if line.find('Accuracy ')>=0:
                        sheet.write(x,4,line[11:])
                        i_accu_list.append(float(line[11:]));
                        if float(line[11:])>maxaccu:
                            maxaccu=float(line[11:])
                        break
                f.close()
                x+=1

            figureNum=g*2+1;
            plt.figure(figureNum)
            plt.suptitle('graph-'+graph[g]+'-time')
            plt.xlabel(u'increment percentage')
            plt.ylabel(u'time consuming')
            ax = plt.subplot(1,1,1)
            lines,=ax.plot(x_list,i_time_list,label='incremental-time')
            handles, labels = ax.get_legend_handles_labels()
            ax.legend(handles[::-1], labels[::-1])
            #plt.axis([1, 9, 0, maxtime])
            plt.savefig('figures/'+'graph-'+graph[g]+'.time.05.pdf')

            figureNum=g*2+2;
            plt.figure(figureNum)
            plt.suptitle('graph-'+graph[g]+'-accu')
            plt.xlabel(u'increment percentage')
            plt.ylabel(u'accuracy')
            ax = plt.subplot(1,1,1)
            lines,=ax.plot(x_list,i_accu_list,label='incremental-accuracy')
            handles, labels = ax.get_legend_handles_labels()
            ax.legend(handles[::-1], labels[::-1])
            ax.legend().set_alpha(0.0)
            #plt.axis([1, 9, 0, maxaccu])
            plt.savefig('figures/'+'graph-'+graph[g]+'.accu.05.pdf')

#plt.show()
xls.save('result_inc.xls')
