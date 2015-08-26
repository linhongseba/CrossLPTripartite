import os,sys,collections,re,subprocess,time,threading,datetime
path='../../data/{0}.txt'
rols=['0.01','0.05','0.10']
rois=['0.2','0.3','0.4','0.5','0.6','0.7','0.8','0.9']
cfds=['0.1','0.2','0.3','0.4','0.5','0.6','0.7','0.8','0.9']
nuance='-1'
maxIter='100'
graphs=['graph-1','graph_paper','graph_imdb_10M']
algorithms=['MRG','MRR','MRO','ARG','ARR','ARO','GRF']
thr=4

def experiment(graph,rol,algo,roi,cfd0,cfd1):
    if cfd0==cfd1:
        fileName=graph+'_'+rol[-2:]+'_'+algo+'_'+roi[-1:]+'_'+cfd0[-1:]
    else:
        fileName=graph+'_'+rol[-2:]+'_'+algo+'_'+roi[-1:]+'_5'
    if os.path.exists('../results/'+fileName+'.txt') and open('../results/'+fileName+'.txt').readlines()[-1]=='Done.\n':return
    sys.stdout.write(fileName+'\n')
    f=open('../results/'+fileName+'.txt','wb')
    f.close()
    p=subprocess.Popen(['java','-jar','LabelInference.jar',path.format(graph),rol,algo,'DEG',roi,cfd0,cfd1,nuance,maxIter], stdout=subprocess.PIPE)
    while p.poll() == None:
        f=open('../results/'+fileName+'.txt','ab')
        s=p.stdout.readline()
        sys.stdout.write(fileName+': '+str(s,encoding='utf8'))
        f.write(s)
        f.close()
    last=p.stdout.readlines()
    f=open('../results/'+fileName+'.txt','ab')
    for s in last:
        f.write(s)
        sys.stdout.write(fileName+': '+str(s,encoding='utf8')+'\n')
    f.close()


threads=[]
subprocess.call(['mkdir','\\..\\results'],shell=True)
for graph in graphs:
    for rol in rols:
        for algo in algorithms:
            threads+=[threading.Thread(target=experiment, args=(graph,rol,algo,'0.0','0.0','0.0',))]
        for roi in rois:
            threads+=[threading.Thread(target=experiment, args=(graph,rol,'MRG',roi,'0.5','0.5',))]
        threads+=[threading.Thread(target=experiment, args=(graph,rol,'MRG','0.1','0.1','0.9',))]      
for t in threads:
    t.start()
    x=0.001
    while(len(threading.enumerate())>thr):
        time.sleep(x)
