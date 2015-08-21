import os,sys,collections,re,subprocess,time,threading,datetime
path='../data/{0}.txt'
rols=['0.01','0.05','0.10']
rois=['0.1','0.2','0.3','0.4','0.5','0.6','0.7','0.8','0.9']
cfds=['0.1','0.2','0.3','0.4','0.5','0.6','0.7','0.8','0.9']
nuance='-1'
maxIter='100'
graphs=['graph-1','graph-30','graph-37','graph_imdb_10M','graph_paper']
thr=5

algorithms={
    'MRG':{'IFR':'MR','SLT':'DEG','INIT':'GRP'},
    'ARG':{'IFR':'AR','SLT':'DEG','INIT':'GRP'},
    'MRR':{'IFR':'MR','SLT':'DEG','INIT':'RND'},
    'ARR':{'IFR':'MR','SLT':'DEG','INIT':'RND'},
    'GRF':{'IFR':'LP','SLT':'DEG','INIT':'DFT'},
    #'MAD':{'IFR':'MR','SLT':'DEG','INIT':'GRP'},
    'MRO':{'IFR':'MRO','SLT':'DEG','INIT':'RND'},
    'ARO':{'IFR':'ARO','SLT':'DEG','INIT':'RND'},
    }

def experiment(graph,rol,algo,roi,cfd):
    fileName=graph+'_'+rol[-2:]+'_'+algo+'_'+roi[-1:]+'_'+cfd[-1:]
    if os.path.exists('results/'+fileName+'.txt') and open('results/'+fileName+'.txt').readlines()[-1]=='Done.\n':return
    sys.stdout.write(fileName+'\n')
    f=open('results/'+fileName+'.txt','wb')
    f.close()
    #startTime=time.clock()
    p=subprocess.Popen(['java','-jar','LabelInference.jar',path.format(graph),rol,algo,'DEG',roi,cfd,nuance,maxIter], stdout=subprocess.PIPE)
    while p.poll() == None:
        f=open('results/'+fileName+'.txt','ab')
        s=p.stdout.readline()
        sys.stdout.write(fileName+': '+str(s,encoding='utf8'))
        f.write(s)
        f.close()
    last=p.stdout.readlines()
    f=open('results/'+fileName+'.txt','ab')
    for s in last:
        f.write(s)
        sys.stdout.write(fileName+': '+str(s,encoding='utf8')+'\n')
    #timeUsed=time.clock()-startTime
    #f.write('Processed in {<3}')
    f.close()


threads=[]
subprocess.call(['mkdir','results'],shell=True)
for graph in graphs:
    for rol in rols:
        for algo in algorithms:
            threads+=[threading.Thread(target=experiment, args=(graph,rol,algo,'0.0','0.0',))]
        for roi in rois:
            threads+=[threading.Thread(target=experiment, args=(graph,rol,'MRG',roi,'0.5',))]
        for cfd in cfds:
            threads+=[threading.Thread(target=experiment, args=(graph,rol,'MRG','0.1',cfd,))]
                    
for t in threads:
    t.start()
    t.join()
    #while(len(threading.enumerate())>thr):time.sleep(1)
