import os,sys,collections,re,subprocess,time,threading
path='../data/{0}.txt'
rofs=['0.05']
rois=['0.0','0.1','0.2','0.3','0.4','0.5','0.6','0.7','0.8','0.9',]
nuance='0'
maxIter='100'
graphs=['graph-37']
inferences=['MAG']
selectors=['DEG']
thr=5

def experiment(graph,rof,inference,selector,roi):
    sys.stdout.write(graph+' '+roi+'\n')
    f=open('results/{0}.{1}.{2}.{3}.{4}.result.txt'.format(graph,inference,selector,rof[-2:],roi[-1:]),'wb')
    f.close()
    p=subprocess.Popen(['java','-jar','LabelInference.jar',path.format(graph),inference,selector,rof,roi,nuance,maxIter,'0.5'], stdout=subprocess.PIPE)
    while p.poll() == None:
        f=open('results/{0}.{1}.{2}.{3}.{4}.result.txt'.format(graph,inference,selector,rof[-2:],roi[-1:]),'ab')
        s=p.stdout.readline()
        sys.stdout.write(graph+'.'+roi[-1:]+': '+str(s,encoding='utf8'))
        if(str(s)[-4:]!='...\n'):f.write(s)
        f.close()
    ss=p.stdout.readlines()
    f=open('results/{0}.{1}.{2}.{3}.{4}.result.txt'.format(graph,inference,selector,rof[-2:],roi[-1:]),'ab')
    for s in ss:
        if(str(s)[-4:]!='...\n'):f.write(s)
        sys.stdout.write(graph+'.'+roi[-1:]+': '+str(s,encoding='utf8'))
    f.close()


threads=[]
subprocess.call(['mkdir','results'],shell=True)
for graph in graphs:
    for rof in rofs:
        for inference in inferences:
            for selector in selectors:
                for roi in rois:
                    threads+=[threading.Thread(target=experiment, args=(graph,rof,inference,selector,roi,))]
                    
for t in threads:
    t.start()
    while(len(threading.enumerate())>thr):time.sleep(1)
