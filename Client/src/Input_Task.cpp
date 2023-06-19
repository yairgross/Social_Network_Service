#include "Input_Task.h"

Input_Task::Input_Task(std::mutex &mutex, ConnectionHandler &handler)
    : _mutex(mutex), _handler(handler) {}

void Input_Task::run() {
    while (!_handler.shouldTerminate()) {
        std::string input = "";
        std::getline(std::cin, input, '\n');
        _handler.sendToServer(input);
    }
}

