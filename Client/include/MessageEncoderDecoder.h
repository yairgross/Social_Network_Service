#ifndef CLIENT_MESSAGEENCODERDECODER_H
#define CLIENT_MESSAGEENCODERDECODER_H

#include <cstdint>
#include <iostream>
#include <cstddef>
#include <vector>

class MessageEncoderDecoder {

private:
    std::vector<char> bytes;

    short bytesToShort(char* bytesArr);

    void shortToBytes(short num, char* bytesArr);

    char* getBytesArray();

    std::string notification(char* bytes, int length);

    std::string ack(char* bytes, int length);

    std::string error(char* bytes);

    int findNextZero(int from, char *bytes);

    std::string currentDateTime();

    std::string extractStatInfo(char *bytes, int length);

public:
    MessageEncoderDecoder();

    std::string decodeNextByte(char nextByte);

    std::vector<char> encode(std::string msg);

    std::vector<char> handleRegister(std::string info);

    std::vector<char> handleLogout(std::string info);

    std::vector<char> handleLogin(std::string info);

    std::vector<char> handleFollow(std::string info);

    std::vector<char> handlePost(std::string info);

    std::vector<char> handlePM(std::string info);

    std::vector<char> handleLogStat(std::string info);

    std::vector<char>handleStat(std::string info);

    std::vector<char> handleBlock(std::string info);

};

#endif //CLIENT_MESSAGEENCODERDECODER_H
