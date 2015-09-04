import os,sys,collections,re,subprocess,time,threading,datetime
path='../../data/{0}.txt'
rols=['0.05']
rois=['0.2','0.3','0.4','0.5','0.6','0.7','0.8','0.9']
maxIter='100'
graphs=['graph_paper','graph_imdb_10M']
algorithms=['MRG','MRR','MRO','ARG','ARR','ARO','GRF']
thr=4

def experiment(graph,rol,algo,roi,cfd0,cfd1,nuance):
    if cfd0==cfd1:
        fileName=graph+'_'+rol[-2:]+'_'+algo+'_'+roi[-1:]+'_'+cfd0[-1:]+'_'+nuance
    else:
        fileName=graph+'_'+rol[-2:]+'_'+algo+'_'+roi[-1:]+'_5'+'_'+nuance
    fileName='results/'+fileName+'.txt'
    if os.path.exists(fileName) and open(fileName).readlines()[-1]=='Done.\n':return
    sys.stdout.write(fileName+'\n')
    f=open(fileName,'wb')
    f.close()
    p=subprocess.Popen(['java','-jar','LabelInference.jar',path.format(graph),rol,algo,'DEG',roi,cfd0,cfd1,nuance,maxIter], stdout=subprocess.PIPE)
    while p.poll() == None:
        f=open(fileName,'ab')
        s=p.stdout.readline()
        sys.stdout.write(fileName+': '+str(s,encoding='utf8'))
        f.write(s)
        f.close()
    last=p.stdout.readlines()
    f=open(fileName,'ab')
    for s in last:
        f.write(s)
        sys.stdout.write(fileName+': '+str(s,encoding='utf8')+'\n')
    f.close()

threads=[]
subprocess.call(['mkdir','results'],shell=True)
for graph in graphs:
    for rol in rols:
        for algo in algorithms:
            threads+=[threading.Thread(target=experiment, args=(graph,rol,algo,'0.0','0.0','0.0','-1',))]
            #threads+=[threading.Thread(target=experiment, args=(graph,rol,algo,'0.0','0.0','0.0','0.001',))]
        for roi in rois:
            threads+=[threading.Thread(target=experiment, args=(graph,rol,'MRG',roi,'0.5','0.5','-1',))]
            #threads+=[threading.Thread(target=experiment, args=(graph,rol,'MRG',roi,'0.5','0.5','0.001',))]
        threads+=[threading.Thread(target=experiment, args=(graph,rol,'MRG','0.1','0.1','0.9','-1',))]
        #threads+=[threading.Thread(target=experiment, args=(graph,rol,'MRG','0.1','0.1','0.9','0.001',))]  
for t in threads:
    t.start()
    while(len(threading.enumerate())>thr):
        time.sleep(0.001)
