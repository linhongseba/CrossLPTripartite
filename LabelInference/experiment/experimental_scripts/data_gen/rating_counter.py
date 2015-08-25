f = open('ratings.dat')
fo = open('ratings_avg.txt','w')
_sum=[0]*100000
tot=[0]*100000
movieMAX=0
cnout=0
while True:
    line = f.readline()
    if not line:
        break
    cnt=0;st=0;en=0;
    for i in range(0,len(line)):
        if cnt==0:
            pre=i
        if cnt==1:
            st=i+2
        if cnt==2:
            en=i
        if cnt==3:
            break;
        if line[i]==':' and line[i+1]==':':
            cnt+=1
    now=float(line[st:en])
    userID=int(line[0:pre]);
    movieID=int(line[pre+2:st-2]);
    _sum[movieID]+=now;
    tot[movieID]+=1.0
    if movieID>movieMAX:
        movieMAX=movieID
    cnout+=1
    if cnout%100000==0:
        print cnout
movieMAX+=1
for i in range(0,movieMAX):
    if tot[i]==0:
        continue
    if _sum[i]/tot[i]>3.5:
        fo.write(str(i)+' 1\n')
    else:
        fo.write(str(i)+' 0\n')
f.close()
fo.close()
