import os,sys,collections,re,subprocess,time,threading,datetime
path='../data/{0}.txt'
rols=['0.05']
rois=['0.1','0.2','0.3','0.4','0.5','0.6','0.7','0.8','0.9']
cfds=['0.1','0.2','0.3','0.4','0.5','0.6','0.7','0.8','0.9']
maxIter='100'
graphs=['graph-30','graph_imdb_10M','graph-37','graph_paper']
algorithms=['MG']

def experiment(graph,rol,algo,roi,nuance,cfd):
    fileName='results/'+graph+'_'+rol+'_'+algo+'_'+roi+'_'+nuance+'_'+cfd+'.txt'
    if os.path.exists(fileName):
        rls=open(fileName).readlines()
        if len(rls)>0 and rls[-1]=='Done.\n':return
    print(fileName)
    p=subprocess.call('main '+path.format(graph)+' '+rol+' '+algo+' '+roi+' '+nuance+' '+maxIter+' '+cfd+' '+'> '+fileName,shell=True)

subprocess.call('mkdir results',shell=True)
for graph in graphs:
    for rol in rols:
        for algo in algorithms:
            experiment(graph,rol,algo,'0.0','-1','-1')
            experiment(graph,rol,algo,'0.0','1e-15','-1')
        #for roi in rois:
            #experiment(graph,rol,'MG',roi,'-1','-1')
            #experiment(graph,rol,'MG',roi,'1e-15','-1')
            #experiment(graph,rol,'MG',roi,'-1','0.5')
            #experiment(graph,rol,'MG',roi,'1e-15','0.5')
        #for cfd in cfds:
            #experiment(graph,rol,'MG','0.1','-1',cfd)
            #experiment(graph,rol,'MG','0.1','1e-15',cfd) 

