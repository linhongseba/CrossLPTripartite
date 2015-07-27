import os,sys,collections,re,subprocess,time
path='../data/{0}.txt'
ratios=['0.05','0.10']
nuance='0'
maxIter='110'
graphs=['graph-1','graph-30','graph-37']

subprocess.call(['mkdir','results'],shell=True)
for graph in graphs:
    for ratio in ratios:
        sys.stdout.write(graph+' '+ratio+'\n')
        f=open('results/{0}.{1}.result.txt'.format(graph,ratio[-2:]),'wb')
        f.close()
        p=subprocess.Popen(['java','-jar','LabelInference.jar',path.format(graph),ratio,nuance,maxIter], stdout=subprocess.PIPE)
        while p.poll() == None:
            f=open('results/{0}.{1}.result.txt'.format(graph,ratio[-2:]),'ab')
            s=p.stdout.readline()
            sys.stdout.write(str(s,encoding='utf8'))
            if(str(s,encoding='utf8')[-4:]!='...\n'):f.write(s)
            f.close()
        ss=p.stdout.readlines()
        f=open('results/{0}.{1}.result.txt'.format(graph,ratio[-2:]),'ab')
        for s in ss:
            if(str(s,encoding='utf8')[-4:]!='...\n'):f.write(s)
            sys.stdout.write(str(s,encoding='utf8'))
        f.close()
