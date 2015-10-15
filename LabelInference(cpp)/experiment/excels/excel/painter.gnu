set term pdfcairo lw 2 font "Times␣New␣Roman,8" 
set output "convergegraph-30.pdf" 
plot "./convergegraph-30.txt"  using 1:2 w lp lw 1 pt 0 title "MG"
set output

set output "convergegraph-37.pdf" 
plot "./convergegraph-37.txt"  using 1:2 w lp lw 1 pt 0 title "MG"
set output

set output "convergegraph_imdb_10M.pdf" 
plot "./convergegraph_imdb_10M.txt"  using 1:2 w lp lw 1 pt 0 title "MG"
set output

set output "convergegraph_paper.pdf" 
plot "./convergegraph_paper.txt"  using 1:2 w lp lw 1 pt 0 title "MG"
set output

