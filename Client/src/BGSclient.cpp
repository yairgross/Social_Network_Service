#include "ConnectionHandler.h"
#include "Input_Task.h"
#include "Output_Task.h"

#include <stdlib.h>
#include <iostream>
#include <mutex>
#include <thread>


int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    std::mutex mutex;
    Input_Task in_task = Input_Task(mutex, connectionHandler);
    Output_Task out_task = Output_Task(mutex, connectionHandler);
    std::thread in_thread(&Input_Task::run, &in_task);
    std::thread out_thread(&Output_Task::run, &out_task);
    out_thread.join();
    in_thread.detach();
    std::cout << "Client terminated" << std::endl;
    return 0;
}

