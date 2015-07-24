import os,sys,collections,re,subprocess,time
path='../data/{0}.txt'
ratio='0.1'
nuance='1e-4'
maxIter='100'
graphs=['graph-1','graph-30','graph-37']

subprocess.call(['mkdir','results'],shell=True)
for graph in graphs:
    sys.stdout.write(graph+'\n')
    f=open('results/{0}.result.txt'.format(graph),'wb')
    f.close()
    p=subprocess.Popen(['java','-jar','LabelInference.jar',path.format(graph),ratio,nuance,maxIter], stdout=subprocess.PIPE)
    while p.poll() == None:
        f=open('results/{0}.result.txt'.format(graph),'ab')
        s=p.stdout.readline()
        sys.stdout.write(str(s,encoding='utf8'))
        f.write(s)
        time.sleep(0.1)
        f.close()
    ss=p.stdout.readlines()
    f=open('results/{0}.result.txt'.format(graph),'ab')
    for s in ss:
        f.write(s)
        sys.stdout.write(str(s,encoding='utf8'))
    f.close()
