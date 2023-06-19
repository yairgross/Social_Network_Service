#include "ConnectionHandler.h"
#include "MessageEncoderDecoder.h"
#include <vector>

using boost::asio::ip::tcp;
using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;


ConnectionHandler::ConnectionHandler(string host, short port): host_(host), port_(port), io_service_(), socket_(io_service_),
                    enc_dec_(MessageEncoderDecoder()), _shouldTerminate(false){}


ConnectionHandler::~ConnectionHandler() {
    close();
}


bool ConnectionHandler::connect() {

    std::cout << "Starting connect to "
              << host_ << ":" << port_ << std::endl;
    try {
        tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
        boost::system::error_code error;
        socket_.connect(endpoint, error);
        if (error)
            throw boost::system::system_error(error);
    }
    catch (std::exception& e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    std::cout << "finished connecting to the server" << std::endl;
    return true;
}


bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp ) {
            tmp += socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);
        }
        if(error)
            throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}


bool ConnectionHandler::sendBytes(std::vector<char> vec, int bytesToWrite) {
    char bytes[vec.size()];
        for (int i = 0; i < (int)vec.size(); i++){
            bytes[i] = vec[i];
        }
    int tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
            tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
        if(error)
            throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

// Close down the connection properly.
void ConnectionHandler::close() {
    try{
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: consnection already closed" << std::endl;
    }
}

void ConnectionHandler::sendToServer(std::string msg) {
    std::vector<char> encoded_msg = enc_dec_.encode(msg);
    int len = encoded_msg.size();
    sendBytes(encoded_msg, len);
}

std::string ConnectionHandler::getResponse() {
    std::string msg = "";
    while (msg == "") {
        char* next_byte = new char[1];
        getBytes(next_byte, 1);
        msg = enc_dec_.decodeNextByte(next_byte[0]);
    }
    return msg;
}

bool ConnectionHandler::shouldTerminate() {return _shouldTerminate;}

void ConnectionHandler::terminate() {_shouldTerminate = true;}