#ifndef CLIENT_OUTPUT_TASK_H
#define CLIENT_OUTPUT_TASK_H

#include "ConnectionHandler.h"

#include <iostream>
#include <mutex>
#include <thread>

class Output_Task {
private:
    std::mutex& _mutex;
    ConnectionHandler& _handler;

public:
    Output_Task(std::mutex& mutex, ConnectionHandler& handler);
    void run();
};

#endif //CLIENT_OUTPUT_TASK_H
