reset
set grid
set size 1,1
set datafile separator "\t"
#set key horizon out
set yrang[-0.04:0.06]
set xrang [0:1]
set xtics ("0%%" 0, "20%%" 0.2, "40%%" 0.4, "60%%" 0.6, "80%%" 0.8, "100%%" 1)
set xlabel 'Confidence parameter'
set ylabel 'Accuracy loss'
set style line 1 lt 1 lc rgb "#e41a1c" lw 9 pt 3 ps 4 
set style line 2 lt 1 lc rgb "#377eb8" lw 9 pt 6 ps 4 
set style line 3 lt 1 lc rgb "#4daf4a" lw 9 pt 1 ps 4 
set style line 4 lt 1 lc rgb "#984ea3" lw 9 pt 12 ps 4 
set style line 5 lt 1 lc rgb "dark-red" lw 9 pt 4 ps 4 
set style line 6 lt 1 lc rgb "#ff7f00" lw 9 pt 2 ps 4
set style line 7 lt 1 lc rgb "#a65628" lw 9 pt 14 ps 4 

set terminal postscript eps enhanced "Helvetica" 28
set output 'confidence-acc.eps'
plot 'confidence-30.txt' using 1:($8-$5) with lp ls 1 title 'Prop 30', \
'confidence-37.txt' using 1:($8-$5) with lp ls 2 title 'Prop 37', \
'confidence-paper.txt' using 1:($8-$5) with lp ls 3 title 'PubMed', \
'confidence-IMDB.txt' using 1:($8-$5) with lp ls 4 title 'IMDB'
clear
