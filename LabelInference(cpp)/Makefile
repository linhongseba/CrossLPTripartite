ifdef SystemRoot
experiment/main.exe : src/* src/*/*
	@g++ src/main.cpp -O3 --std=c++1y -pthread -march=native -o experiment/main.exe
else
experiment/main : src/* src/*/*
	@g++ src/main.cpp -O3 --std=c++1y -pthread -march=native -o experiment/main
endif