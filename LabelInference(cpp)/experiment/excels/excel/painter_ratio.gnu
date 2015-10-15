set term pdfcairo lw 2 font "Times␣New␣Roman,8" 
set output "ratio.pdf" 
plot "./ratio_mg"  using 1:2 w lp lw 1 pt 0 title "30",\
     "./ratio_mg"  using 1:3 w lp lw 1 pt 0 title "37",\
     "./ratio_mg"  using 1:4 w lp lw 1 pt 0 title "imdb_10M",\
     "./ratio_mg"  using 1:5 w lp lw 1 pt 0 title "paper"
set output

