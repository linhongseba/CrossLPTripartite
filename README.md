# Semi-supervised learning algorithms on K-partite Graphs

##Table of Content
- [Publication](##publication)
- [Code Location](##code-location)
- [Installation] (##intallation)
- [Usage] (##usage)
- [Input and Output] (##input-and-output)
- [Example Pipeline] (##example-pipeline)
- [Development Manual] ()


## Publication
  
## Code location
We have two versions of implementations, one is implemented in Java, and another is implemented in C++ with parallel in pthread.

Java version:  [LabelInference](https://github.com/linhongseba/CrossLPTripartite/tree/master/LabelInference)

C++ version:   [LabelInference(CPP)](https://github.com/linhongseba/CrossLPTripartite/tree/master/LabelInference%28cpp%29)

## Installation
### C++
    - Make sure that a g++ compiler that supports c++11 was intalled in your local machine
    - Make sure that pthread library was intalled in your local machine
    - Enter into LabelInference(cpp) folder, and type make to compile the file
    
### Java
    - Compile with IDE Netbeans, see detailed instruction from
    
    (https://netbeans.org/kb/73/java/project-setup.html?print=yes#existing-java-sources)



## Usage

To run the Java code,

path.format(graph),rol,algo,'DEG',roi,cfd0,cfd1,nuance,maxIter

Java -jar LabelInference.jar [graph-file-folder] [percentage-of-training-labels] [algorithm-name] [algorithm-to-select-seeds] [number of new data] [confidence parameter 1] [confidence parameter 2] [abort parameter] [maxnimum number of iterations] [value of parameter beta]

To run the c++ code,

Similar to the above

## Example Pipeline
 
 Please refer to the script LabelInference/experiment/experimental_scripts/experiment.py for more examples
 
 
