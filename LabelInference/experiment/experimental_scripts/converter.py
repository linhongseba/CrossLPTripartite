#
# Running on python3
#
import xlwt3 as xlwt
import math

def _isdigit(ch):
    if '0'<=ch and ch<='9' or ch=='.':
        return True
    else:
        return False

def _isfraction(ch):
    if '0'<=ch and ch<='9' or ch=='/':
        return True
    else:
        return False

def fil_line(_line):
    a=list(filter(_isdigit,_line))
    b=''.join(a)
    return b

def fil_accu(_line):
    a=list(filter(_isfraction,_line))
    cnt=0.0
    cnt1=0.0
    label=0
    for x in a:
        if x=='/':
            label=1
        else:
            if label==0:
                cnt=cnt*10+int(x)
            else:
                cnt1=cnt1*10+int(x)
    return cnt/cnt1

def find_xls(filename,_type):
    f = open('results/'+filename)
    obj=''
    time=''
    obj_list=[]
    if _type==2:
        while True:
            line = f.readline()
            if line.find('Recompute2')>=0:
                break;
    label=0
    while True:
        line = f.readline()
        if not line:
            break
        if line.find('ObjValue')>=0 and line.find('0.000000')<0 and _type==2:
            obj_list.append(float(line[11:]))
        if line.find('Processed in ')>=0 and line.find('total')>=0 and _type==3:
            return fil_line(line)
        if _type==2 and line.find('confidence')>=0:
            break;
        if line.find('Global')>=0:
            label=1
        if line.find('Accuracy ')>=0 and _type==1 and label==1:
            return fil_line(line[line.find('(C)'):])
    if _type==2:
        return obj_list


xls=xlwt.Workbook()

#================================================================================
sheet = xls.add_sheet('accuracy',cell_overwrite_ok=True)

graph=['graph-30','graph-37','graph_imdb','graph_paper'];
algorithm=['MRG','ARG','MRR','ARR','GRF','MRO','ARO'];

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
        filename=g+'_05_'+a+'_0_0_0.0000000000000001.txt';
        sheet.write(x,y,find_xls(filename,1))
#================================================================================
#================================================================================

graph=['graph-30','graph-37','graph_imdb','graph_paper'];
algorithm=['MRG','ARG','MRR','ARR','GRF','MRO','ARO'];


for g in graph:
    sheet = xls.add_sheet('converge-recompute2'+g,cell_overwrite_ok=True)
    sheet.write(0,0,'#iteration')
    for i in range(0,7):
        sheet.write(0,1+i,algorithm[i])

    y=0
    _maxx=0
    for a in algorithm:
        y+=1
        filename=g+'_05_'+a+'_0_0_0.0000000000000001.txt';
        obj=find_xls(filename,2)

        x=0
        for o in obj:
            x+=1
            sheet.write(x,y,o)
            _maxx=max(_maxx,x)
    x=0
    for i in range(0,_maxx):
        x+=1
        sheet.write(x,0,i)

#================================================================================
#================================================================================
sheet = xls.add_sheet('running_time',cell_overwrite_ok=True)

graph=['graph-30','graph-37','graph_imdb','graph_paper'];
algorithm=['MRG','ARG','MRR','ARR','GRF','MRO','ARO'];

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
        filename=g+'_05_'+a+'_0_0_0.0000000000000001.txt';
        sheet.write(x,y,find_xls(filename,3))

#================================================================================
xls.save('../excel/result.xls')
