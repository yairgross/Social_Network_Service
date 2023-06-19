#ifndef CLIENT_INPUT_TASK_H
#define CLIENT_INPUT_TASK_H

#include "ConnectionHandler.h"

#include <iostream>
#include <mutex>
#include <thread>

class Input_Task {
private:
    std::mutex& _mutex;
    ConnectionHandler& _handler;

public:
    Input_Task(std::mutex& mutex, ConnectionHandler& handler);
    void run();
};

#endif //CLIENT_INPUT_TASK_H
