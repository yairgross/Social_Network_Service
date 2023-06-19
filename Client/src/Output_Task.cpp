#include "Output_Task.h"

Output_Task::Output_Task(std::mutex &mutex, ConnectionHandler &handler)
    : _mutex(mutex), _handler(handler) {}

void Output_Task::run() {
    while (!_handler.shouldTerminate()) {
        std::string response = "";
        response = _handler.getResponse();
        if (response != "") {
            if(response[(int)(response.size()) -1] == '%')
                response = response.substr(0, (int)(response.size()) - 1);
            std::cout << response << std::endl;
            if ((int)response.find("ACK 3") >= 0){
                _handler.close();
                _handler.terminate();
            }
        }
    }
}



