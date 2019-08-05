CC = g++
CFLAGS  = -g -Wall -std=c++11
LFLAGS  = -L/usr/lib -lboost_thread -lboost_system

# All Targets
all: all

# Tool invocations
all: bin/BbClient.o bin/connectionHandler.o bin/TaskRead.o bin/TaskWrite.o bin/BoolHold.o
	$(CC) -o bin/BBClient bin/BbClient.o bin/connectionHandler.o bin/TaskRead.o bin/TaskWrite.o bin/BoolHold.o $(LFLAGS)
	@echo 'Finished building'

# Depends on the source and header files
bin/connectionHandler.o: src/connectionHandler.cpp include/connectionHandler.h
	$(CC) $(CFLAGS) -c -Iinclude -o bin/connectionHandler.o src/connectionHandler.cpp

# Depends on the source and header files
bin/BoolHold.o: src/BoolHold.cpp include/BoolHold.h
	$(CC) $(CFLAGS) -c -Iinclude -o bin/BoolHold.o src/BoolHold.cpp

# Depends on the source and header files
bin/TaskRead.o: src/TaskRead.cpp include/TaskRead.h
	$(CC) $(CFLAGS) -c -Iinclude -o bin/TaskRead.o src/TaskRead.cpp

# Depends on the source and header files
bin/TaskWrite.o: src/TaskWrite.cpp include/TaskWrite.h
	$(CC) $(CFLAGS) -c -Iinclude -o bin/TaskWrite.o src/TaskWrite.cpp

# Depends on the source and header files
bin/BbClient.o: src/BbClient.cpp include/TaskRead.h include/TaskWrite.h include/connectionHandler.h include/BoolHold.h
	$(CC) $(CFLAGS) -c -Iinclude -o bin/BbClient.o src/BbClient.cpp

#Clean the build directory
clean:
	rm -f bin/*