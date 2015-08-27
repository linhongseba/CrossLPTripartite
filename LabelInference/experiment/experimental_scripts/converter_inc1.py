import datetime
import time
import os
import sys
import xlwt


xls=xlwt.Workbook()

#graph-1.BCD.DEG.05.result.txt
graph=['graph-1','graph-30','graph-37','graph_imdb_10M','graph_paper'];
algorithm=['MRG','ARG','MRR','ARR','GRF','MRO','ARO'];
training=['01','05','10'];



for g in graph:
    sheet = xls.add_sheet(g,cell_overwrite_ok=True)
    tr=-1
    for t in training:
        tr+=1
        sheet.write(0,tr*7+1,t)
        for i in range(1,10):
            sheet.write(i+1,0,"%d"% i+"0%")
        sheet.write(1,0,"new-data-percentage")
        sheet.write(1,tr*7+1,"update_only-time")
        sheet.write(1,tr*7+2,"total-time")
        sheet.write(1,tr*7+3,"incremental-accuracy")
        sheet.write(1,tr*7+4,"global-accuracy")
        sheet.write(1,tr*7+5,"recompute-time-update")
        sheet.write(1,tr*7+6,"recompute-time-total")
        sheet.write(1,tr*7+7,"recompute-accuracy(global)")

        for i in range(1,2):
            filename=filename=g+'_'+t+'_MRG_'+str(i)+'_5.txt';
            f = open('../results/'+filename)
            #print filename;
            for con in range(0,6):
                line = f.readline()
                label=0
                while True:
                    line = f.readline()
                    if not line:
                        break
                    if line.find('Processed in ')>=0 and line.find('update')>=0:
                        sheet.write(i+1,tr*7+1,line[13:])
                    if line.find('Processed in ')>=0 and line.find('total')>=0:
                        sheet.write(i+1,tr*7+2,line[13:])
                    if line.find('Global')>=0:
                        label=1
                    if line.find('Accuracy')>=0 and label==0:
                        sheet.write(i+1,tr*7+3,line[-14:-6])
                    if line.find('Accuracy')>=0 and label==1:
                        sheet.write(i+1,tr*7+4,line[-14:-6])
                    if line.find('confidence')>=0:
                        break
            f.close()
            filename=filename=g+'_'+t+'_MRG_0_0.txt';
            f = open('../results/'+filename)
            while True:
                line = f.readline()
                if not line:
                    break
                if line.find('Processed in ')>=0 and line.find('update')>=0:
                    sheet.write(i+1,tr*7+5,line[13:])
                if line.find('Processed in ')>=0 and line.find('total')>=0:
                    sheet.write(i+1,tr*7+6,line[13:])
                    break;
            label=0
            while True:
                line = f.readline()
                if not line:
                    break
                if line.find('Global')>=0:
                    label=1
                if line.find('Accuracy')>=0 and label==1:
                    sheet.write(i+1,tr*7+7,line[-14:-6])
            f.close()

        for i in range(2,10):
            filename=filename=g+'_'+t+'_MRG_'+str(i)+'_5.txt';
            f = open('../results/'+filename)
            #print filename;
            label=0
            while True:
                line = f.readline()
                if not line:
                    break
                if line.find('Processed in ')>=0 and line.find('update')>=0:
                    sheet.write(i+1,tr*7+1,line[13:])
                if line.find('Processed in ')>=0 and line.find('total')>=0:
                    sheet.write(i+1,tr*7+2,line[13:])
                if line.find('Global')>=0:
                    label=1
                if line.find('Accuracy')>=0 and label==0:
                    sheet.write(i+1,tr*7+3,line[-14:-6])
                if line.find('Accuracy')>=0 and label==1:
                    sheet.write(i+1,tr*7+4,line[-14:-6])
            f.close()

            filename=filename=g+'_'+t+'_MRG_0_0.txt';
            f = open('../results/'+filename)
            while True:
                line = f.readline()
                if not line:
                    break
                if line.find('Processed in ')>=0 and line.find('update')>=0:
                    sheet.write(i+1,tr*7+5,line[13:])
                if line.find('Processed in ')>=0 and line.find('total')>=0:
                    sheet.write(i+1,tr*7+6,line[13:])
                    break;
            label=0
            while True:
                line = f.readline()
                if not line:
                    break
                if line.find('Global')>=0:
                    label=1
                if line.find('Accuracy')>=0 and label==1:
                    sheet.write(i+1,tr*7+7,line[-14:-6])
            f.close()

#plt.show()
xls.save('../excel/result_inc_fixed_confidence_0.5.xls')
