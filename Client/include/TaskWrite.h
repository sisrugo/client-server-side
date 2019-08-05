//
// Created by shakedah on 1/9/18.
//

#ifndef SPL3_CLIENT_TASKREAD_H
#define SPL3_CLIENT_TASKREAD_H

#include <boost/thread/condition_variable.hpp>
#include <boost/thread/mutex.hpp>
#include "connectionHandler.h"
#include "BoolHold.h"

class TaskWrite {

private:
    ConnectionHandler* _connectionHandler;
    BoolHold *boolHold;

public:
    TaskWrite (ConnectionHandler* connectionHandler, BoolHold* boolHold);

    TaskWrite (const TaskWrite& toCopy);

    TaskWrite (TaskWrite&& toMove);

    TaskWrite& operator=(const TaskWrite &other);

    TaskWrite& operator=(TaskWrite &&other);

    ~TaskWrite();

    void operator()();

};


#endif //SPL3_CLIENT_TASKREAD_H
