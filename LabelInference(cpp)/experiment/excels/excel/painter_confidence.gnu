set term pdfcairo lw 2 font "Times␣New␣Roman,8" 
set output "confidence.pdf" 
plot "./confidence_mg"  using 1:2 w lp lw 1 pt 0 title "30",\
     "./confidence_mg"  using 1:3 w lp lw 1 pt 0 title "37",\
     "./confidence_mg"  using 1:4 w lp lw 1 pt 0 title "imdb_10M",\
     "./confidence_mg"  using 1:5 w lp lw 1 pt 0 title "paper"
set output

