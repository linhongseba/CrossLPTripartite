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
            sheet.write(con+2,0,'confidence=0.'+str(con))
        sheet.write(0,tr*7+1,t)
        sheet.write(1,tr*7+1,"update_only-time")
        sheet.write(1,tr*7+2,"total-time")
        sheet.write(1,tr*7+3,"incremental-accuracy")
        sheet.write(1,tr*7+4,"global-accuracy")
        sheet.write(1,tr*7+5,"recompute-accuracy")
        sheet.write(1,tr*7+6,"recompute-accuracy")
        sheet.write(1,tr*7+5,"recompute-time-update")
        sheet.write(1,tr*7+6,"recompute-time-total")
        sheet.write(1,tr*7+7,"recompute-accuracy")


        for i in range(1,2):
            filename=filename=g+'_'+t+'_MRG_'+str(i)+'_5.txt';
            f = open('results/'+filename)
            #print filename;
            for con in range(0,10):
                line = f.readline()
                while True:
                    line = f.readline()
                    if not line:
                        break
                    if line.find('Processed in ')>=0 and line.find('update')>=0:
                        sheet.write(con+2,tr*7+1,line[13:])
                    if line.find('Processed in ')>=0 and line.find('total')>=0:
                        sheet.write(con+2,tr*7+2,line[13:])
                    if line.find('accuracy ')>=0 and line.find('Incremental')>=0:
                        sheet.write(con+2,tr*7+3,line[22:])
                    if line.find('accuracy ')>=0 and line.find('Global ')>=0:
                        sheet.write(con+2,tr*7+4,line[18:])
                    if line.find('confidence')>=0:
                        break
            f.close()
            filename=g+'_'+t+'_MRG_0_0.txt';
            f = open('results/'+filename)
            while True:
                line = f.readline()
                if not line:
                    break
                if line.find('Processed in ')>=0 and line.find('update')>=0:
                    _update=line[13:]
                if line.find('Processed in ')>=0 and line.find('total')>=0:
                    _total=line[13:]
                    break;
            while True:
                line = f.readline()
                if not line:
                    break
                if line.find('accuracy ')>=0 and line.find('Global ')>=0:
                    _accuracy=line[18:]
            f.close()
            for con in range(0,10):
                sheet.write(con+2,tr*7+5,_update)
                sheet.write(con+2,tr*7+6,_total)
                sheet.write(con+2,tr*7+7,_accuracy)

#plt.show()
xls.save('result_inc_fixed_ratio_0.5.xls')
