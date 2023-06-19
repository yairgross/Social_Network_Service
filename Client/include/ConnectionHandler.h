#ifndef CONNECTION_HANDLER__
#define CONNECTION_HANDLER__

#include "MessageEncoderDecoder.h"

#include <string>
#include <iostream>
#include <boost/asio.hpp>
#include <vector>


using boost::asio::ip::tcp;


class ConnectionHandler {

private:

    const std::string host_;
    const short port_;
    boost::asio::io_service io_service_;   // Provides core I/O functionality
    tcp::socket socket_;
    MessageEncoderDecoder enc_dec_;
    bool _shouldTerminate;


public:

    ConnectionHandler(std::string host, short port);

    virtual ~ConnectionHandler();


    // Connect to the remote machine
    bool connect();


    // Read a fixed number of bytes from the server - blocking.
    // Returns false in case the connection is closed before bytesToRead bytes can be read.
    bool getBytes(char bytes[], unsigned int bytesToRead);


    // Send a fixed number of bytes from the client - blocking.
    // Returns false in case the connection is closed before all the data is sent.
    bool sendBytes(std::vector<char>, int bytesToWrite);


    // Close down the connection properly.
    void close();


    // sends a message to the server
    void sendToServer(std::string msg);


    // gets a message drom the server
    std::string getResponse();


    bool shouldTerminate();

    void terminate();

}; //class ConnectionHandler


#endif