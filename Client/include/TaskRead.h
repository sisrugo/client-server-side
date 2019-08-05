//
// Created by shakedah on 1/8/18.
//

#ifndef SPL3_CLIENT_TASK_H
#define SPL3_CLIENT_TASK_H

#include <boost/thread/condition_variable.hpp>
#include <boost/thread/mutex.hpp>
#include "connectionHandler.h"
#include "BoolHold.h"


class TaskRead {
private:
    ConnectionHandler* _connectionHandler;
    BoolHold* boolHold;


public:

    TaskRead (ConnectionHandler* connectionHandler, BoolHold* boolHold);

    TaskRead (const TaskRead& toCopy);

    TaskRead (TaskRead&& toMove);

    TaskRead& operator=(const TaskRead &other);

    TaskRead& operator=(TaskRead &&other);

    ~TaskRead();

    void operator()();

};


#endif //SPL3_CLIENT_TASK_H
