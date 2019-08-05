#include <stdlib.h>
#include "../include/connectionHandler.h"
#include "../include/TaskRead.h"
#include <boost/thread.hpp>
#include <chrono>
#include <thread>
#include "../include/TaskWrite.h"
#include "../include/BoolHold.h"

using namespace std;
/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/


int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    BoolHold* boolHold = new BoolHold();
    ConnectionHandler* connectionHandler = new ConnectionHandler(host, port);

    if (!connectionHandler->connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }



    TaskWrite taskToWrite (connectionHandler,boolHold );
    TaskRead taskToRead (connectionHandler, boolHold);
    boost::thread th1 (taskToWrite);
    boost::thread th2 (taskToRead);


    th2.join();
    boolHold->setBool(true);
    cout << "Ready to exit. Press enter" << endl;
    th1.join();

    delete connectionHandler;
    delete boolHold;
    return 0;
}
