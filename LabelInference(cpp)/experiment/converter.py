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
    return float(b)

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

def fil_B(_line):
    st=_line.find('0.',0)
    _list=[]
    while st>=0:
        en=_line.find('0.',st+1)
        if en>=0:
            _list.append(fil_line(_line[st:en]))
        else:
            _list.append(fil_line(_line[st:]))
        st=en
    return _list

def find_xls(filename,_type):
    f = open('results/'+filename)
    obj=''
    time=''
    obj_list=[]
    #if computing_part=='Recomputing':
    #    while True:
    #        line = f.readline()
    #        if line.find('Recomputing')>=0:
    #            break
    while True:
        line = f.readline()
        if not line:
            break
        if line.find('ObjValue')>=0 and _type==2:
            obj_list.append(fil_line(line))
        if line.find('Processed in ')>=0 and _type==3:
            return fil_line(line[:line.find('sec')])
        if line.find('accuracy')>=0 and _type==1:
            return fil_accu(line)
    if _type==2:
        return obj_list
#type1 means to extract the accuracy
#type2 means to extract the obj
#type3 means to extract the processing time

xls=xlwt.Workbook()

#================================================================================
sheet = xls.add_sheet('accuracy',cell_overwrite_ok=True)

graph=['graph-30','graph-37'];
algorithm=['MR','MO','MG','AR','AO','AG','LP'];
nuance=['-1','1e-15'];
#training_increase=['-10.5','1e-150.5','1e-15-1','-1-1'];

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
        accu_list=[]
        for t in nuance:
            filename=g+'_0.05_'+a+'_0.0_'+t+'_-1.txt';
            accu_list.append(find_xls(filename,1));

        sheet.write(x,y,sum(accu_list)/2.0)
#================================================================================
#================================================================================

graph=['graph-30','graph-37'];
algorithm=['MR','MO','MG','AR','AO','AG','LP'];
nuance=['-1','1e-15'];

maxx=0
for g in graph:
    sheet = xls.add_sheet('converge'+g,cell_overwrite_ok=True)
    sheet.write(0,0,'#iteration')
    for i in range(0,7):
        sheet.write(0,1+i,algorithm[i])

    y=0
    for a in algorithm:
        y+=1
        #obj=[]
        filename=g+'_0.05_'+a+'_0.0_-1_-1.txt';
        obj=find_xls(filename,2)

        x=0
        for o in obj:
            x+=1
            sheet.write(x,y,o)
        maxx=max(x,maxx)

for x in range(0,maxx):
    sheet.write(x+1,0,x)
#================================================================================
#================================================================================
sheet = xls.add_sheet('running_time',cell_overwrite_ok=True)

#graph=['graph-1','graph-30','graph-37','graph_imdb_10M','graph_paper'];
graph=['graph-30','graph-37'];
algorithm=['MR','MO','MG','AR','AO','AG','LP'];
nuance=['-1','1e-15'];

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
        filename=g+'_0.05_'+a+'_0.0_-1_-1.txt';
        sheet.write(x,y,find_xls(filename,3))

#================================================================================
xls.save('../excel/result.xls')
