import os,sys,collections,re,subprocess,time

graphName='1'

fTweetLabel='tweetlabel-'+graphName+'.txt'
fTweetWord='tweet-word-'+graphName+'.txt'
fUserLabel='userlabel-'+graphName+'.txt'
fUserRetweet='user-retweet-'+graphName+'.txt'
fUserUserGraph='user-user-graph-'+graphName+'.txt'
fUserWord='user-word-'+graphName+'.txt'
#fSf0='Sf0.txt'
fTempGraph='.tempGraph'
fTestGraph='testGraph.g'
fTrainGraph5='trainGraph5.g'
fTrainGraph10='trainGraph10.g'

tweetLabel=open(fTweetLabel).readlines()
tweetWord=open(fTweetWord).readlines()
userLabel=open(fUserLabel).readlines()
userRetweet=open(fUserRetweet).readlines()
userUserGraph=open(fUserUserGraph).readlines()
userWord=open(fUserWord).readlines()
#sf0=open(fSf0).readlines()
tempGraph=open(fTempGraph,'w')
testGraph=open(fTestGraph,'w')
trainGraph5=open(fTrainGraph5,'w')
trainGraph10=open(fTrainGraph10,'w')

#store whole graph with adjacency list, use type+id_in_type as id
graph=collections.defaultdict(dict)
for s in userLabel:
    v=s.split()
    graph['A'+v[0]].update({'label':v[1]})
for s in tweetLabel:
    v=s.split()
    graph['B'+v[0]].update({'label':v[1]})
#for s in sf0:
#    v=s.split()
#    if v[2]==1:
#        graph['C'+v[0]].update({'label':v[1]})
for s in userRetweet:
    e=s.split()
    graph['A'+e[0]].setdefault('neighbors',{})
    graph['B'+e[1]].setdefault('neighbors',{})
    graph['A'+e[0]]['neighbors'].update({'B'+e[1]:e[2]})
    graph['B'+e[1]]['neighbors'].update({'A'+e[0]:e[2]})

for s in tweetWord:
    e=s.split()
    graph['B'+e[0]].setdefault('neighbors',{})
    graph['C'+e[1]].setdefault('neighbors',{})
    graph['B'+e[0]]['neighbors'].update({'C'+e[1]:e[2]})
    graph['C'+e[1]]['neighbors'].update({'B'+e[0]:e[2]})

for s in userWord:
    e=s.split()
    graph['A'+e[0]].setdefault('neighbors',{})
    graph['C'+e[1]].setdefault('neighbors',{})
    graph['A'+e[0]]['neighbors'].update({'C'+e[1]:e[2]})
    graph['C'+e[1]]['neighbors'].update({'A'+e[0]:e[2]})


#give a global integer id to each vertex, in order to suit SNA
vNum=0
graphB=collections.defaultdict(dict)
for v in graph:
    graph[v].setdefault('label','')
    graphB[str(vNum)].update({'label':graph[v]['label'],'id':str(vNum)})
    graphB[str(vNum)].setdefault('neighbors',{})
    graph[v].update({'id':str(vNum)})
    vNum+=1
for v in graph:
    graphB[graph[v]['id']].update({'name':v[1:],'type':v[:1]})
    graphB[graph[v]['id']].update({'neighbors':{graph[u]['id']:graph[v]['neighbors'][u] for u in graph[v]['neighbors']}})
    
#write a temp file as SNA formated, run SNA and get result back
tempGraph.write('{0}\n'.format(vNum))
for v in graphB:
    tempGraph.write('{0},{1}'.format(v,len(graphB[v]['neighbors'])))
    [tempGraph.write(':{0}'.format(u)) for u in graphB[v]['neighbors']]
    tempGraph.write('\n')
tempGraph.close()
subprocess.call(['SNA','.tempGraph','nei'],shell=True)
result=[s.split() for s in open('.tempGraph.statistics.txt').readlines()[1:]]

#make test set and train set
testGraph.write(str(vNum)+'\n')
for v in result:
    testGraph.write(v[0]+' '+v[1]+' '+(graphB[v[0]]['label'] if 'label' in graphB[v[0]] else '0')+' '+graphB[v[0]]['type']+'\n')
    [testGraph.write(' '+u+' '+graphB[v[0]]['neighbors'][u]+'\n') for u in graphB[v[0]]['neighbors']]
    testGraph.write('\n')
sortedResult=[(-float(v[2]),v[0]) for v in result if 'label' in graphB[v[0]]]
sortedResult.sort()
for v in result:
    trainGraph5.write(v[0]+' '+v[1]+' '+(graphB[v[0]]['label'] if (-float(v[2]),v[0]) in sortedResult[:int(len(sortedResult)/20)] else '0')+' '+graphB[v[0]]['type'])
    [trainGraph5.write(' '+u+' '+graphB[v[0]]['neighbors'][u]+'\n') for u in graphB[v[0]]['neighbors']]
    trainGraph5.write('\n')
for v in result:
    trainGraph10.write(v[0]+' '+v[1]+' '+(graphB[v[0]]['label'] if (-float(v[2]),v[0]) in sortedResult[:int(len(sortedResult)/10)] else '0')+' '+graphB[v[0]]['type'])
    [trainGraph10.write(' '+u+' '+graphB[v[0]]['neighbors'][u]+'\n') for u in graphB[v[0]]['neighbors']]
    trainGraph10.write('\n')
testGraph.close()
trainGraph5.close()
trainGraph10.close()
