import hashlib


print 'converting xls ... '


x=2;
y=2;

f = open('tags.dat')
fo = open('tags_out.txt','w')
dict = {}
str_list=[]
num=0;
pre=[]
back=[]
last=[]
tot=0
userMAX=0;
movieMAX=0;
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
    now=line[st:en].upper()
    o=dict.get(now,0);
    if o==0:
        num+=1;
        str_list+=now+'\n'
        dict[now]=num
    userID=int(line[0:pre]);
    movieID=int(line[pre+2:st-2]);
    if userID>userMAX:
        userMAX=userID
    if movieID>movieMAX:
        movieMAX=movieID


    #print str(userID)+" "+str(movieID)
#==============================================================================================
print dict['VERY GOOD']
tagMAX=num
fo.writelines('2\n');
print userMAX+tagMAX+movieMAX
f.close();
f = open('tags.dat')
pre_edge=[0]*1000000
last=[0]*1000000
back=[0]*1000000
tot=0
while True:
    line = f.readline()
    if not line:
        break
    #print line
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
    now=line[st:en].upper()

    tagID=dict[now]-1+userMAX
    userID=int(line[0:pre])-1
    movieID=int(line[pre+2:st-2])-1+userMAX+tagMAX;

    #print pre_edge[0]
    tot+=1;pre_edge[tot]=last[userID];last[userID]=tot;back[tot]=movieID
    tot+=1;pre_edge[tot]=last[movieID];last[movieID]=tot;back[tot]=userID
    tot+=1;pre_edge[tot]=last[userID];last[userID]=tot;back[tot]=tagID
    tot+=1;pre_edge[tot]=last[tagID];last[tagID]=tot;back[tot]=userID
    tot+=1;pre_edge[tot]=last[movieID];last[movieID]=tot;back[tot]=tagID
    tot+=1;pre_edge[tot]=last[tagID];last[tagID]=tot;back[tot]=movieID
#==============================================================================================
_rating=[0]*100000
fi= open('ratings_avg.txt')
movieID=0
while True:
    line = fi.readline()
    if not line:
        break
    cnt=0;st=0;en=0;
    for i in range(0,len(line)):
        if line[i]==' ':
            cnt+=1
        if cnt==0:
            st=i
    en=len(line)
    

    movieID=int(line[0:st+1]);
    if int(line[st+2:en])==1:
        _rating[movieID-1]=1;
        print "***"+str(movieID)
    else:
        _rating[movieID-1]=2;

for i in range(0,userMAX+tagMAX+movieMAX):
    if last[i]==0:
        continue
    if i<userMAX:
        fo.write('A'+str(i))
    else:
        if i<userMAX+tagMAX:
            fo.write('B'+str(i-userMAX))
        else:
            fo.write('C'+str(i-userMAX-tagMAX))
    j=last[i]
    now_list=[]
    num=0
    while j!=0:
        now_list+=[back[j]]
        j=pre_edge[j]
    write_list=set(now_list)
    for j in write_list:
        num+=1
    fo.write(' '+str(num))
    if i>=userMAX+tagMAX:
        if _rating[i-(userMAX+tagMAX)]==1:
            fo.write(' 0 1\n')
        if _rating[i-(userMAX+tagMAX)]==2:
            fo.write(' 1 0\n')
        if _rating[i-(userMAX+tagMAX)]==0:
            fo.write(' 0 0\n')
    else:
        fo.write(' 0 0\n')
    for x in write_list:
        if x<userMAX:
            fo.write('A'+str(x))
        else:
            if x<userMAX+tagMAX:
                fo.write('B'+str(x-userMAX))
            else:
                fo.write('C'+str(x-userMAX-tagMAX))
        fo.write(' 1\n')

f.close()
fi.close()
fo.close()



