reset
set size 1.2,0.5
set boxwidth 1.0 absolute
set style fill pattern border -1
set style histogram clustered gap 2
#set grid
set logscale y
set noxtics
set datafile separator "\t"
set style data histograms
set yrang [1:8500]
set xtics   ("Prop 30" 1, "Prop 37" 2, "PubMed" 3, "IMDB" 4)
#unset key
set key 4.7, 25000 horiz
#set key reverse Left left top at screen 1.12, graph 1
#set key font ",20" spacing 1
set style line 1 lt 1 lc rgb "#e41a1c" lw 5 pt 3 ps 2.5
set style line 2 lt 1 lc rgb "#377eb8" lw 5 pt 4 ps 2.5
set style line 3 lt 1 lc rgb "#4daf4a" lw 5 pt 6 ps 2.5
set style line 4 lt 1 lc rgb "#984ea3" lw 5 pt 8 ps 2.5
set style line 5 lt 1 lc rgb "#ff7f00" lw 5 pt 12 ps 2.5
set style line 6 lt 1 lc rgb "#ffff33" lw 5 pt 12 ps 2.5
set style line 7 lt 1 lc rgb "#a65628" lw 5 pt 12 ps 2.5
set style line 8 lt 1 lc rgb "#f781bf" lw 5 pt 12 ps 2.5
set style line 9 lt 1 lc rgb "#999999" lw 5 pt 12 ps 2.5
set terminal postscript eps enhanced color "Helvetica" 20
#set yrange [0:1.0]
#set ytics (0.2, 0.4, 0.6, 0.8, 1.0)  
set ylabel 'Total running time (s)' offset 2
set output 'time.eps'
plot [0.6:4.5] 'Time.txt' using 2 ti 'MRG' ls 1 fs pattern 3, '' using 3 ti 'ARG' ls 8 fs pattern 3, \
'' using 4 ti 'MRR' ls 6 fs pattern 3, '' using 5 ti 'ARR' ls 3 fs pattern 3, \
'' using 6 ti 'GRF' ls 4 fs pattern 1, '' using 7 ti 'MRO' ls 9 fs pattern 4, \
'' using 8 ti 'ARO' ls 2 fs pattern 2
clear
