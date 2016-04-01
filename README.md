# Learning algorithms on K-partite Graphs

##Table of Content
-[Clustering on K-partite Graph](#clustering)

-[Graph Summary on K-partite Graph](#summary)

-[Label Propagation on K-partite Graph](#Label Propagation)

##Clustering
###Related Publications

Linhong Zhu, Aram Galstyan, James Cheng, and Kristina Lerman. Tripartite Graph Clustering for Dynamic Sentiment Analysis on Social Media. ACM SIGMOD 2014: 1531-1542.

Linhong Zhu, Aram Galstyan, James Cheng, Kristina Lerman:
Tripartite Graph Clustering for Dynamic Sentiment Analysis on Social Media. CoRR abs/1402.6010 (2014)

###Code location
The matlab implementation for clustering on Tripartite graph is located within folder Triclustering
###Usage:
[Su,Sp,Sf,Hu,Hp,errsu, errsp, errsr, accyu,accyp, MIu, MIp] = tricluster(Xu,Xr,Xp,Gu,Sf0,alpha,beta,tlabel, ulabel)
where:

  %n: number of tweets
  
  %m: number of users
  
  %d: number of features
  
  %r: number of clusters
  
  %Input:
  
  %Xu: user-feature matrix m X d
  
  %Xr: user-retweet matrix m X n
  
  %Xp: tweet-feature matrix n X d
  
  %Gu: user-user graph, n X n
  
  %Sf0: feature-sentiment lexicon information
  
  %tlabel: ground truth for tweet-cluster
  
  %ulabel: groupd truth for user-cluster
  
  %hyperparameter: alpha and beta
  
  %output: 
  
  %Su: user-cluster matrix m X r
  
  %Sp: tweet-cluster matrix n X r
  
  %Sf: feature-cluster matrix d X r
  
  %errsu: user-level approximation error
  
  %errsp: tweet-level approximation error
  
  %errsr: total approximation error
  
  %accyu: user-level accuracy of each iteration
  
  %accyp :tweet-level accuracy of each iteration
  
  %MIu: user-level MI of each iteration
  
  %MIp: tweet-level MI of each iteration

###summary
On-going work

###Label Propagation

