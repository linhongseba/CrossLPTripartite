set term pdfcairo lw 2 font "Times␣New␣Roman,8" 
set output "convergegraph-30.pdf" 
plot "./convergegraph-30.txt"  using 1:2 w lp lw 1 pt 1 title "MR",\
"./convergegraph-30.txt"  using 1:3 w lp lw 1 pt 1 title "MO",\
"./convergegraph-30.txt"  using 1:4 w lp lw 1 pt 1 title "MG",\
"./convergegraph-30.txt"  using 1:5 w lp lw 1 pt 1 title "AR",\
"./convergegraph-30.txt"  using 1:6 w lp lw 1 pt 1 title "AO",\
"./convergegraph-30.txt"  using 1:7 w lp lw 1 pt 1 title "AG",\
"./convergegraph-30.txt"  using 1:8 w lp lw 1 pt 1 title "LP"
set output

set output "convergegraph_imdb_10M.pdf" 
plot "./convergegraph_imdb_10M.txt"  using 1:2 w lp lw 1 pt 1 title "MR",\
"./convergegraph_imdb_10M.txt"  using 1:3 w lp lw 1 pt 1 title "MO",\
"./convergegraph_imdb_10M.txt"  using 1:4 w lp lw 1 pt 1 title "MG",\
"./convergegraph_imdb_10M.txt"  using 1:5 w lp lw 1 pt 1 title "AR",\
"./convergegraph_imdb_10M.txt"  using 1:6 w lp lw 1 pt 1 title "AO",\
"./convergegraph_imdb_10M.txt"  using 1:7 w lp lw 1 pt 1 title "AG",\
"./convergegraph_imdb_10M.txt"  using 1:8 w lp lw 1 pt 1 title "LP"
set output
