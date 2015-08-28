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

        for con in range(0,10):
            sheet.write(con+2,0, float(con)/10)
        sheet.write(0,tr*7+1,t)
        sheet.write(1,tr*7+0,"confidence")
        sheet.write(1,tr*7+1,"update_only-time (sec)")
        sheet.write(1,tr*7+2,"total-time (sec)")
        sheet.write(1,tr*7+3,"incremental-accuracy")
        sheet.write(1,tr*7+4,"global-accuracy")
        sheet.write(1,tr*7+5,"recompute-accuracy")
        sheet.write(1,tr*7+6,"recompute-accuracy")
        sheet.write(1,tr*7+5,"recompute-time-update")
        sheet.write(1,tr*7+6,"recompute-time-total")
        sheet.write(1,tr*7+7,"recompute-accuracy")


        for i in range(1,2):
            filename=filename=g+'_'+t+'_MRG_'+str(i)+'_5.txt';
            f = open('../results/'+filename)
            #print filename;
            for con in range(0,10):
                label=0
                
                line = f.readline()
                while True:
                    line = f.readline()
                    if not line:
                        break
                    if line.find('Processed in ')>=0 and line.find('update')>=0:
                        sheet.write(con+2,tr*7+1,float(line[13:-16])/1000)
                    if line.find('Processed in ')>=0 and line.find('total')>=0:
                        sheet.write(con+2,tr*7+2,float(line[13:-10])/1000)
                    if line.find('Global')>=0:
                        label=1
                    if line.find('Accuracy')>=0 and label==0:
                        sheet.write(con+2,tr*7+3,float(line[-14:-6]))
                    if line.find('Accuracy')>=0 and label==1:
                        sheet.write(con+2,tr*7+4,float(line[-14:-6]))
                    if line.find('confidence')>=0:
                        break
            f.close()
            filename=g+'_'+t+'_MRG_0_0.txt';
            f = open('../results/'+filename)
            while True:
                line = f.readline()
                if not line:
                    break
                if line.find('Processed in ')>=0 and line.find('update')>=0:
                    _update=float(line[13:-16])/1000
                if line.find('Processed in ')>=0 and line.find('total')>=0:
                    _total=float(line[13:-10])/1000
                    break;
            label=0
            while True:
                line = f.readline()
                if not line:
                    break
                if line.find('Global')>=0:
                    label=1
                if line.find('Accuracy')>=0 and label==1:
                    _accuracy=float(line[-14:-6])
            f.close()
            for con in range(0,10):
                sheet.write(con+2,tr*7+5,_update)
                sheet.write(con+2,tr*7+6,_total)
                sheet.write(con+2,tr*7+7,_accuracy)

#plt.show()
xls.save('../excel/result_inc_fixed_ratio_0.5.xls')
