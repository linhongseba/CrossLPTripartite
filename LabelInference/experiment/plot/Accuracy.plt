reset
set size 1.2,0.5
set boxwidth 1.0 absolute
set style fill pattern border -1
set style histogram errorbars lw 5 gap 2
#set grid
set noxtics
set datafile separator "\t"
set style data histograms
set xtics   ("Prop 30" 0, "Prop 37" 1, "PubMed" 2, "IMDB" 3)
#unset key
set key 3.8, 1.1 horiz
#set key reverse Left left top at screen 1.12, graph 1
#set key font ",20" spacing 1
set style line 1 lt 1 lc rgb "#e41a1c" lw 4 pt 3 ps 2.5
set style line 2 lt 1 lc rgb "#377eb8" lw 4 pt 4 ps 2.5
set style line 3 lt 1 lc rgb "#4daf4a" lw 4 pt 6 ps 2.5
set style line 4 lt 1 lc rgb "#984ea3" lw 4 pt 8 ps 2.5
set style line 5 lt 1 lc rgb "#ff7f00" lw 4 pt 12 ps 2.5
set style line 6 lt 1 lc rgb "#ffff33" lw 4 pt 12 ps 2.5
set style line 7 lt 1 lc rgb "#a65628" lw 4 pt 12 ps 2.5
set style line 8 lt 1 lc rgb "#f781bf" lw 4 pt 12 ps 2.5
set style line 9 lt 1 lc rgb "#999999" lw 4 pt 12 ps 2.5
set terminal postscript eps enhanced color "Helvetica" 20
set yrange [0:1.0]
set ytics (0.2, 0.4, 0.6, 0.8)  
set ylabel 'Accuracy' offset 2
set output 'Accuracy.eps'
plot [-0.4:3.6] 'Accuracy.txt' using 2:3:4 ti 'MRG' ls 1 fs pattern 3, '' using 5:6:7 ti 'ARG' ls 8 fs pattern 3, \
'' using 8:9:10 ti 'MRR' ls 6 fs pattern 3, '' using 11:12:13 ti 'ARR' ls 3 fs pattern 3, \
'' using 14:15:16 ti 'GRF' ls 4 fs pattern 1, '' using 17:18:19 ti 'MRO' ls 9 fs pattern 4, \
'' using 20:21:22 ti 'ARO' ls 2 fs pattern 2
clear
