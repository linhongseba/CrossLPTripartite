import datetime
import time
import os
import sys
import xlwt 

print 'converting xls ... '

x = 0       
y = 0       

xls=xlwt.Workbook()
sheet = xls.add_sheet('sheet1',cell_overwrite_ok=True)
sheet.write(0,1,"BCD")
sheet.write(0,4,"MA")
sheet.write(0,7,"LP")
for i in range(0,3):
    sheet.write(1,1+3*i,"Degree")
    sheet.write(1,2+3*i,"Random")
    sheet.write(1,3+3*i,"Heuristic")
sheet.write(0,1,"BCD")
sheet.write(0,4,"MA")
sheet.write(0,7,"LP")
sheet.write(0,1,"BCD")
sheet.write(0,4,"MA")
sheet.write(0,7,"LP")


#graph-1.BCD.DEG.05.result.txt
graph=['1','30','37'];
algorithm=['BCD','MA','LP'];
preprocessing=['DEG','RND','SHR'];
training=['05','10'];

x=2;
y=1;

for g in range(0,3):
    for t in range(0,2):
        sheet.write(x,0,'graph'+str(g+1)+'-'+training[t])
        for a in range(0,3):
            for p in range(0,3):
                filename='graph-'+graph[g]+'.'+algorithm[a]+'.'+preprocessing[p]+'.'+training[t]+'.result.txt';
                f = open('results/'+filename)
                #print filename;
                i=0
                while True:
                    i+=1
                    line = f.readline()
                    if not line:
                        break
                    if line.find('Accuracy ')>=0:
                        sheet.write(x,y,line[11:])
                        y+=1;
                        break
        x+=1
        y=1
xls.save('result.xls')
