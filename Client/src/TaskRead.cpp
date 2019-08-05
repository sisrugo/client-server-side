//
// Created by shakedah on 1/8/18.
//

#include <iostream>
#include "../include/TaskRead.h"
#include <boost/thread.hpp>
#include <condition_variable>


using namespace std;

TaskRead::TaskRead (ConnectionHandler* connectionHandler, BoolHold* boolHold) : _connectionHandler(connectionHandler), boolHold(boolHold){}

//Both copy and move just copy the pointer to connectionHandler, as TaskRead
//only holds a pointer to connectionHandler given to it by the main thread
TaskRead::TaskRead(const TaskRead &toCopy) { // Copy Constructor
    this->_connectionHandler = toCopy._connectionHandler;
    this->boolHold = toCopy.boolHold;
}

TaskRead::TaskRead(TaskRead &&toMove) { //Move Constructor
    this->_connectionHandler = toMove._connectionHandler;
    this->boolHold = toMove.boolHold;
    toMove.boolHold = nullptr;
    toMove._connectionHandler = nullptr;
}

TaskRead& TaskRead::operator=(const TaskRead &other) { // Copy Operator
    if (this != &other){
        _connectionHandler = other._connectionHandler;
        this->boolHold = other.boolHold;
    }
    return *this;
}

TaskRead& TaskRead::operator=(TaskRead &&other) { // Move Operator
    if (this != &other){
        _connectionHandler = other._connectionHandler;
        other._connectionHandler = nullptr;
        this->boolHold = other.boolHold;
        other.boolHold = nullptr;
    }
    return *this;
}

TaskRead::~TaskRead(){

};

void TaskRead::operator()(){
    while (1){
        std::string answer;
        if (!_connectionHandler->getLine(answer)) { // The connection had disconnected
            break;
        }
        int len = answer.length()-1;
        answer.resize(len); // removes the '/n'
        std::cout << answer << std::endl;
        if (answer == "ACK signout succeeded") { // if answer is signout, break loop (and stop waiting for answers from server
            break;
        }
    }
}


