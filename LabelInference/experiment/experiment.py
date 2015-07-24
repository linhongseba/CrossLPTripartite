import os,sys,collections,re,subprocess,time
graphName='../data/graph-1.txt'
ratio='0.1'
nuance='1e-4'
maxIter='100'
subprocess.call(['java','-jar','LabelInference.jar',graphName,ratio,nuance,maxIter],shell=False)
