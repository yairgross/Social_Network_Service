# Defining makefile variables
CFLAGS = -g -Wall -Weffc++ -c -std=c++11 -Iinclude -o
BFLAGS = -lboost_system -lboost_thread -pthread

SRCFILES = ./src/BGSclient.cpp ./src/ConnectionHandler.cpp ./src/MessageEncoderDecoder.cpp ./src/Input_Task.cpp ./src/Output_Task.cpp
OBJFILES = ./bin/BGSclient.o ./bin/ConnectionHandler.o ./bin/MessageEncoderDecoder.o ./bin/Input_Task.o ./bin/Output_Task.o

# Default target, executed directly after the "make" command
all: ./bin/BGSclient

# Links the project's .o files into BGSclient.exe
./bin/BGSclient: ./bin/*.o
	@echo "Linking object files"
	g++ -Wall -Weffc++ -o ./bin/BGSclient $(OBJFILES) $(BFLAGS)
	@echo "Finished linking"

# Compiles all the .cpp files in the "src" directory
./bin/*.o: $(SRCFILES)
	@echo "Compiling source code"
	g++ $(CFLAGS) ./bin/BGSclient.o ./src/BGSclient.cpp
	g++ $(CFLAGS) ./bin/ConnectionHandler.o ./src/ConnectionHandler.cpp
	g++ $(CFLAGS) ./bin/MessageEncoderDecoder.o ./src/MessageEncoderDecoder.cpp
	g++ $(CFLAGS) ./bin/Input_Task.o ./src/Input_Task.cpp
	g++ $(CFLAGS) ./bin/Output_Task.o ./src/Output_Task.cpp
	@echo "Finished compiling"

# Cleans all the files in the "bin" directory
clean:
	rm -rf ./bin/*