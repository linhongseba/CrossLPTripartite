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

sheet = xls.add_sheet('stat',cell_overwrite_ok=True)

sheet.write(0,0,'dataname')
sheet.write(0,1,'#nodes')
sheet.write(0,2,'#edges')
sheet.write(0,3,'#label classes')
sheet.write(0,4,'#labeled nodes')
sheet.write(0,5,'type ratio(a:b:c)')
sheet.write(0,6,'max label ratio')

sheet.write(1,3,'2')
sheet.write(2,3,'3')
sheet.write(3,3,'3')
sheet.write(4,3,'2')
sheet.write(5,3,'19')

x=0
for g in graph:
    x+=1
    sheet.write(x,0,g)

    filename=g+'_01_MRG_0_0.txt';
    f = open('../results/'+filename)
    label=0
    total=[]
    labeled=[]
    ratio=[0,0,0]
    while True:
        line = f.readline()
        if not line:
            break
        if line.find('Global')>=0:
            label=1
        if line.find('Totle')>=0 and label==1:
            total+=[float(filter(lambda x:x.isdigit(),line[:line.find('(A)')]))]
            total+=[float(filter(lambda x:x.isdigit(),line[line.find('(A)'):line.find('(B)')]))]
            total+=[float(filter(lambda x:x.isdigit(),line[line.find('(B)'):line.find('(C)')]))]
        if line.find('Labeled')>=0 and label==1:
            labeled+=[float(filter(lambda x:x.isdigit(),line[:line.find('(A)')]))]
            labeled+=[float(filter(lambda x:x.isdigit(),line[line.find('(A)'):line.find('(B)')]))]
            labeled+=[float(filter(lambda x:x.isdigit(),line[line.find('(B)'):line.find('(C)')]))]
    sheet.write(x,1,sum(total))
    sheet.write(x,4,sum(labeled))
    ratio[0]=labeled[0]/total[0]
    ratio[1]=labeled[1]/total[1]
    ratio[2]=labeled[2]/total[2]
    sheet.write(x,5,str(ratio[0])+':'+str(ratio[1])+':'+str(ratio[2]))
    sheet.write(x,6,max(ratio))

    f.close()
    filename=g+'.txt';
    f = open('../../data/'+filename)
    _edge=0
    while True:
        line = f.readline()
        if not line:
            break
        if line.count(' ')==1:
            _edge+=1
    sheet.write(x,2,_edge/2.0)
    f.close()


#plt.show()
xls.save('../excel/datastat.xls')
