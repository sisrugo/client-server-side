//
// Created by shakedah on 1/9/18.
//

#include "../include/TaskWrite.h"
#include <boost/thread.hpp>
#include <condition_variable>


using namespace std;


TaskWrite::TaskWrite (ConnectionHandler* connectionHandler, BoolHold* boolHold) : _connectionHandler(connectionHandler), boolHold(boolHold){}

//Both copy and move just copy the pointer to connectionHandler, as TaskWrite
// only holds a pointer to connectionHandler given to it by the main thread
TaskWrite::TaskWrite(const TaskWrite &toCopy) { // Copy Constructor
    this->_connectionHandler = toCopy._connectionHandler;
    this->boolHold = toCopy.boolHold;

}

TaskWrite::TaskWrite(TaskWrite &&toMove) { //Move Constructor
    this->_connectionHandler = toMove._connectionHandler;
    this->boolHold = toMove.boolHold;
    toMove.boolHold = nullptr;
    toMove._connectionHandler = nullptr;
}

TaskWrite& TaskWrite::operator=(const TaskWrite &other) { // Copy Operator
    if (this != &other){
        _connectionHandler = other._connectionHandler;
        this->boolHold = other.boolHold;

    }
    return *this;
}

TaskWrite& TaskWrite::operator=(TaskWrite &&other) { // Move Operator
    if (this != &other){
        _connectionHandler = other._connectionHandler;
        other._connectionHandler = nullptr;
        this->boolHold = other.boolHold;
        other.boolHold = nullptr;
    }
    return *this;
}

TaskWrite::~TaskWrite(){}

void TaskWrite::operator()() {

    while (!std::cin.eof()) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);

        if (boolHold->getBool() || !_connectionHandler->sendLine(line)){ // if
            break;
        }
    }
}