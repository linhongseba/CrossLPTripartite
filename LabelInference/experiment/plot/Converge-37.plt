reset
set size 1,1
set datafile separator "\t"
set key horizon out
set yrange[21500000:26000000]
set xrang [0:100]
set ytics("2.2" 22000000, "2.3" 23000000, "2.4" 24000000, "2.5" 25000000, "2.6" 26000000)
set label "X 10^7" at 0, 26500000 
set xlabel 'Number of iterations'
set ylabel 'Objective values'
 
set style line 1 lt 1 lc rgb "#e41a1c" lw 9 pt 3 ps 4 
set style line 2 lt 1 lc rgb "#377eb8" lw 9 pt 6 ps 4 
set style line 3 lt 1 lc rgb "#4daf4a" lw 9 pt 1 ps 4 
set style line 4 lt 1 lc rgb "#984ea3" lw 9 pt 12 ps 4 
set style line 5 lt 1 lc rgb "dark-red" lw 9 pt 4 ps 4 
set style line 6 lt 1 lc rgb "#ff7f00" lw 9 pt 2 ps 4
set style line 7 lt 1 lc rgb "#a65628" lw 9 pt 14 ps 4 

set terminal postscript eps enhanced "Helvetica" 28
set output 'Converge-37.eps'
plot 'converge-37.txt' using 1:2 with lp ls 1 title 'MRG', \
'' using 1:3 with lp ls 2 title 'ARG', \
'' using 1:4 with lp ls 3 title 'MRR', \
'' using 1:5 with lp ls 4 title 'ARR', \
'' using 1:6 with lp ls 5 title 'GRF', \
'' using 1:7 with lp ls 6 title 'MRO', \
'' using 1:8 with lp ls 7 title 'ARO'
clear
