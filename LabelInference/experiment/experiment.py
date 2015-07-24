import os,sys,collections,re,subprocess,time
path='../data/{0}.txt'
ratio='0.1'
nuance='1e-4'
maxIter='100'
graphs=['graph-1','graph-30','graph-37']

subprocess.call(['mkdir','results'],shell=True)
for graph in graphs:
    sys.stdout.write(graph+'\n')
    f=open('results/{0}.result.txt'.format(graph),'w')
    f.close()
    p=subprocess.Popen(['java','-jar','LabelInference.jar',path.format(graph),ratio,nuance,maxIter], stdout=subprocess.PIPE)
    while p.poll() == None:
        f=open('results/{0}.result.txt'.format(graph),'a')
        s=str(p.stdout.readline(),encoding='utf8')
        sys.stdout.write(s)
        if len(s)>3:s=s[:-1]
        f.write(s)
        time.sleep(0.1)
        f.close()
    ss=p.stdout.readlines()
    f=open('results/{0}.result.txt'.format(graph),'a')
    for s in ss:
        s=str(s,encoding='utf8')
        sys.stdout.write(s)
        if len(s)>3:s=s[:-1]
        f.write(s)
    f.close()
