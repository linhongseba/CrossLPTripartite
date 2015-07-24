import os,sys,collections,re,subprocess,time

graphName='30'
numLabels=2

fTweetLabel='tweetlabel-'+graphName+'.txt'
fTweetWord='tweet-word-'+graphName+'.txt'
fUserLabel='userlabel-'+graphName+'.txt'
fUserRetweet='user-retweet-'+graphName+'.txt'
fUserUserGraph='user-user-graph-'+graphName+'.txt'
fUserWord='user-word-'+graphName+'.txt'
fSf0='Sf0.txt'
fGraph='graph-'+graphName+'.txt'

tweetLabel=open(fTweetLabel).readlines()
tweetWord=open(fTweetWord).readlines()
userLabel=open(fUserLabel).readlines()
userRetweet=open(fUserRetweet).readlines()
userUserGraph=open(fUserUserGraph).readlines()
userWord=open(fUserWord).readlines()
sf0=open(fSf0).readlines()
graph=open(fGraph,'w')


#store whole graph with adjacency list, use type+id_in_type as id
g=collections.defaultdict(dict)
for s in userLabel:
    v=s.split()
    if v[1]=='3':continue
    g['A'+v[0]].setdefault('label',['0']*numLabels)
    g['A'+v[0]]['label'][int(v[1])-1]='1'
for s in tweetLabel:
    v=s.split()
    g['B'+v[0]].setdefault('label',['0']*numLabels)
    g['B'+v[0]]['label'][int(v[1])-1]='1'
for s in sf0:
    v=s.split()
    if v[2]!='1':continue
    #g['C'+v[0]].setdefault('neighbors',{})
    g['C'+v[0]].setdefault('label',['0']*numLabels)
    g['C'+v[0]]['label'][int(v[1])-1]=v[2]
for s in userRetweet:
    e=s.split()
    g['A'+e[0]].setdefault('label',['0']*numLabels)
    g['B'+e[1]].setdefault('label',['0']*numLabels)
    g['A'+e[0]].setdefault('neighbors',{})
    g['B'+e[1]].setdefault('neighbors',{})
    g['A'+e[0]]['neighbors'].update({'B'+e[1]:e[2]})
    g['B'+e[1]]['neighbors'].update({'A'+e[0]:e[2]})
for s in tweetWord:
    e=s.split()
    g['B'+e[0]].setdefault('label',['0']*numLabels)
    g['C'+e[1]].setdefault('label',['0']*numLabels)
    g['B'+e[0]].setdefault('neighbors',{})
    g['C'+e[1]].setdefault('neighbors',{})
    g['B'+e[0]]['neighbors'].update({'C'+e[1]:e[2]})
    g['C'+e[1]]['neighbors'].update({'B'+e[0]:e[2]})
for s in userWord:
    e=s.split()
    g['A'+e[0]].setdefault('label',['0']*numLabels)
    g['C'+e[1]].setdefault('label',['0']*numLabels)
    g['A'+e[0]].setdefault('neighbors',{})
    g['C'+e[1]].setdefault('neighbors',{})
    g['A'+e[0]]['neighbors'].update({'C'+e[1]:e[2]})
    g['C'+e[1]]['neighbors'].update({'A'+e[0]:e[2]})

graph.write(str(numLabels)+'\n')
for v in g:
    if 'neighbors' not in g[v]:continue
    graph.write(v+' '+str(len(g[v]['neighbors']))+' '+' '.join(g[v]['label'])+'\n')
    [graph.write(u+' '+g[v]['neighbors'][u]+'\n') for u in g[v]['neighbors']]
    graph.write('\n')
graph.close()

