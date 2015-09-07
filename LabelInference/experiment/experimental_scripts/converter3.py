import xlwt
import math

def find_xls(filename,_type):
    f = open('./results/'+filename)
    obj=''
    time=''
    obj_list=[]
    label=0
    while True:
        line = f.readline()
        if not line:
            break
        if line.find('ObjValue')>=0 and line.find('0.000000')<0 and _type==2:
            obj_list.append(float(line[11:]))
        if line.find('Processed in ')>=0 and line.find('total')>=0 and _type==3:
            return float(line[13:-10])/1000
        if line.find('Global')>=0:
            label=1
    if _type==2:
        return obj_list


xls=xlwt.Workbook()

graph=['graph-1','graph-30','graph-37','graph_imdb','graph_paper'];
algorithm=['MRG','ARG','MRR','ARR','GRF','MRO','ARO'];
training=['05'];
iteration=range(0,11)+[20,30,40,50,60,70,80,90,100]

for g in graph:
    sheet = xls.add_sheet('converge'+g,cell_overwrite_ok=True)
    sheet.write(0,0,'#iteration')
    for i in range(0,7):
        sheet.write(0,1+i,algorithm[i])
    x=0
    for i in iteration:
        x+=1
        sheet.write(x,0,i)
    
    y=0
    for a in algorithm:
        y+=1
        #obj=[]
        filename=g+'_05_'+a+'_0_0.txt';
        obj=find_xls(filename,2)
    
        x=0
        for o in obj:
            x+=1
            sheet.write(x,y,o)
#================================================================================
#================================================================================
sheet = xls.add_sheet('running_time',cell_overwrite_ok=True)

graph=['graph-1','graph-30','graph-37','graph_imdb','graph_paper'];
algorithm=['MRG','ARG','MRR','ARR','GRF','MRO','ARO'];
training=['05',];

sheet.write(0,0,'running time (sec)')
for i in range(0,7):
    sheet.write(0,1+i,algorithm[i])

x=0
for g in graph:
    x+=1
    sheet.write(x,0,g)
    y=0
    for a in algorithm:
        y+=1
        tot=0;
        filename=g+'_05_'+a+'_0_0.txt';
        sheet.write(x,y,find_xls(filename,3))

xls.save('../excel/result0906.xls')
