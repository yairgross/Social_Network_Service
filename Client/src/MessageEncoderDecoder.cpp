#include <iostream>
#include <ctime>
#include <cstring>
#include <vector>

#include "MessageEncoderDecoder.h"


MessageEncoderDecoder::MessageEncoderDecoder(): bytes(std::vector<char>()) {}

std::string MessageEncoderDecoder::decodeNextByte(char nextByte) {
    if (nextByte == ';') {
        int bytes_length = bytes.size();
        char* bytesArray = getBytesArray();
        char* opcodeBytes = new char[2];
        opcodeBytes[0] = bytesArray[0];
        opcodeBytes[1] = bytesArray[1];
        short opcode = bytesToShort(opcodeBytes);
        switch (opcode) {
            case 9 : return notification(bytesArray, bytes_length);
            case 10 : return ack(bytesArray, bytes_length);
            case 11 : return error(bytesArray);
        }
        delete bytesArray;
        delete[] opcodeBytes;
    }
    else {
        bytes.push_back(nextByte);
    }
    return "";
}

std::vector<char> MessageEncoderDecoder::encode(std::string msg) {
    std::string command = msg.substr(0, msg.find(' '));
    std::string info = msg.substr(msg.find(' ')+1);
    if (command == "REGISTER") return handleRegister(info);
    else if (command == "LOGIN") return handleLogin(info);
    else if (command == "LOGOUT") return handleLogout(info);
    else if (command == "FOLLOW") return handleFollow(info);
    else if (command == "POST") return handlePost(info);
    else if (command == "PM") return handlePM(info);
    else if (command == "LOGSTAT") return handleLogStat(info);
    else if (command == "STAT") return handleStat(info);
    else if (command == "BLOCK") return handleBlock(info);
    return std::vector<char>();
}

short MessageEncoderDecoder::bytesToShort(char* bytesArr) {
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}

void MessageEncoderDecoder::shortToBytes(short num, char* bytesArr) {
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}

char* MessageEncoderDecoder::getBytesArray() {
    char* ret = new char[(int)(bytes.size())];
    for (int i = 0; i < (int)(bytes.size()); i++) {
        ret[i] = bytes[i];
    }
    bytes.clear();
    return ret;
}

std::string MessageEncoderDecoder::notification(char *bytes, int length) {
    std::string pm_post = "PM";
    if (bytes[2] == '1')
        pm_post = "POST";
    int i = 2;
    bool foundZero = false;
    while(i < length && !foundZero){
        i++;
        foundZero = bytes[i] == '\0';
    }
    std::string posting_user = std::string(&bytes[3], i-2);
    int j = i+1;
    foundZero = false;
    while(j < length && !foundZero){
        j++;
        foundZero = bytes[j] == '\0';
    }
    std::string content = std::string(&bytes[i+1], j-1);
    std::string ret = "NOTIFICATION " + pm_post + " " + posting_user + " " + content;
    return ret;
}

std::string MessageEncoderDecoder::ack(char *bytes, int length) {
    char* msg_opcode_bytes = new char[2];
    msg_opcode_bytes[0] = bytes[2];
    msg_opcode_bytes[1] = bytes[3];
    short msg_opcode = bytesToShort(msg_opcode_bytes);
    std::string optional = "";
    std::string ret = "";
    if (msg_opcode == 4) {
        optional = std::string(&bytes[4], length-5);
        ret = "ACK " + std::to_string(msg_opcode) + " " + optional;
    }
    else if (msg_opcode == 7 || msg_opcode == 8){
        ret = extractStatInfo(bytes, length);
    }
    else {
        ret = "ACK " + std::to_string(msg_opcode);
    }
    return ret;
}

std::string MessageEncoderDecoder::extractStatInfo(char *bytes, int length) {
    std::string ret = "";
    int number_of_users = length / 12;
    for (int i = 0; i < number_of_users; i++) {
        ret += "ACK ";
        char* opcode_bytes = new char[2];
        opcode_bytes[0] = bytes[12*i+2];
        opcode_bytes[1] = bytes[12*i+3];
        short msg_opcode = bytesToShort(opcode_bytes);
        ret += std::to_string(msg_opcode) + " ";
        char* age_bytes = new char[2];
        age_bytes[0] = bytes[12*i+4];
        age_bytes[1] = bytes[12*i+5];
        short age = bytesToShort(age_bytes);
        ret += std::to_string(age) + " ";
        char* num_posts_bytes = new char[2];
        num_posts_bytes[0] = bytes[12*i+6];
        num_posts_bytes[1] = bytes[12*i+7];
        short num_posts = bytesToShort(num_posts_bytes);
        ret += std::to_string(num_posts) + " ";
        char* num_followers_bytes = new char[2];
        num_followers_bytes[0] = bytes[12*i+8];
        num_followers_bytes[1] = bytes[12*i+9];
        short num_followers = bytesToShort(num_followers_bytes);
        ret += std::to_string(num_followers) + " ";
        char* num_followings_bytes = new char[2];
        num_followings_bytes[0] = bytes[12*i+10];
        num_followings_bytes[1] = bytes[12*i+11];
        short num_followings = bytesToShort(num_followings_bytes);
        ret += std::to_string(num_followings) + " ";
        if (i < number_of_users-1)
            ret += "\n";
    }
    return ret;
}

std::string MessageEncoderDecoder::error(char *bytes) {
    char* msg_opcode_bytes = new char[2];
    msg_opcode_bytes[0] = bytes[2];
    msg_opcode_bytes[1] = bytes[3];
    short msg_opcode = bytesToShort(msg_opcode_bytes);
    std::string ret = "ERROR " + std::to_string(msg_opcode);
    return ret;
}

int MessageEncoderDecoder::findNextZero(int from, char *bytes) {
    for (int i = from; i < (int)(sizeof(bytes)); i++) {
        if (bytes[i] == '\0')
            return i;
    }
    return -1;
}

std::vector<char> MessageEncoderDecoder::handleRegister(std::string info) {
    char* opcode = new char[2];
    shortToBytes((short) 1, opcode);
    std::string username_str = info.substr(0, info.find(' '));
    char* username_bytes = new char[(int)(username_str.size())];
    std::copy(username_str.begin(), username_str.end(), username_bytes);
    info = info.substr(info.find(' ')+1);
    std::string password_str = info.substr(0, info.find(' '));
    char* password_bytes = new char[(int)(password_str.size())];
    std::copy(password_str.begin(), password_str.end(), password_bytes);
    info = info.substr(info.find(' ')+1);
    std::string birthday_str = info;
    char* birthday_bytes = new char[(int)(birthday_str.size())];
    std::copy(birthday_str.begin(), birthday_str.end(), birthday_bytes);
    std::vector<char> ret = std::vector<char>();
    ret.push_back(opcode[0]);
    ret.push_back(opcode[1]);
    for (int i = 0; i < (int)(username_str.size()); i++) {
        ret.push_back(username_bytes[i]);
    }
    ret.push_back('\0');
    for (int i = 0; i < (int)(password_str.size()); i++) {
        ret.push_back(password_bytes[i]);
    }
    ret.push_back('\0');
    for (int i = 0; i < (int)(birthday_str.size()); i++) {
        ret.push_back(birthday_bytes[i]);
    }
    ret.push_back(';');
    return ret;
}

std::vector<char> MessageEncoderDecoder::handleLogin(std::string info) {
    char* opcode = new char[2];
    shortToBytes((short) 2, opcode);
    std::string username_str = info.substr(0, info.find(' '));
    char* username_bytes = new char[(int)(username_str.size())];
    std::copy(username_str.begin(), username_str.end(), username_bytes);
    info = info.substr(info.find(' ')+1);
    std::string password_str = info.substr(0, info.find(' '));
    char* password_bytes = new char[(int)(password_str.size())];
    std::copy(password_str.begin(), password_str.end(), password_bytes);
    info = info.substr(info.find(' ')+1);
    char captcha = info[0];
    std::vector<char> ret = std::vector<char>();
    ret.push_back(opcode[0]);
    ret.push_back(opcode[1]);
    for (int i = 0; i < (int)(username_str.size()); i++) {
        ret.push_back(username_bytes[i]);
    }
    ret.push_back('\0');
    for (int i = 0; i < (int)(password_str.size()); i++) {
        ret.push_back(password_bytes[i]);
    }
    ret.push_back('\0');
    ret.push_back(captcha);
    ret.push_back(';');
    return ret;
}

std::vector<char> MessageEncoderDecoder::handleLogout(std::string info) {
    char* opcode = new char[2];
    shortToBytes(3, opcode);
    std::vector<char> ret = std::vector<char>();
    ret.push_back(opcode[0]);
    ret.push_back(opcode[1]);
    ret.push_back(';');
    return ret;
}

std::vector<char> MessageEncoderDecoder::handleFollow(std::string info) {
    char* opcode = new char[2];
    shortToBytes((short) 4, opcode);
    char follow = info[0];
    info = info.substr(info.find(' ')+1);
    std::string username_str = info;
    char* username_bytes = new char[(int)(username_str.size())];
    std::copy(username_str.begin(), username_str.end(), username_bytes);
    std::vector<char> ret = std::vector<char>();
    ret.push_back(opcode[0]);
    ret.push_back(opcode[1]);
    ret.push_back(follow);
    for (int i = 0; i < (int)(username_str.size()); i++) {
        ret.push_back(username_bytes[i]);
    }
    ret.push_back(';');
    return ret;
}

std::vector<char> MessageEncoderDecoder::handlePost(std::string info) {
    char* opcode = new char[2];
    shortToBytes((short) 5, opcode);
    std::string content_str = info;
    char* content_bytes = new char[(int)(content_str.size())];
    std::copy(content_str.begin(), content_str.end(), content_bytes);
    std::vector<char> ret = std::vector<char>();
    ret.push_back(opcode[0]);
    ret.push_back(opcode[1]);
    for (int i = 0; i <(int)(content_str.size()); i++) {
        ret.push_back(content_bytes[i]);
    }
    ret.push_back('\0');
    ret.push_back(';');
    return ret;
}

std::vector<char> MessageEncoderDecoder::handlePM(std::string info) {
    char* opcode = new char[2];
    shortToBytes((short) 6, opcode);
    std::string username_str = info.substr(0, info.find(' '));
    char* username_bytes = new char[(int)(username_str.size())];
    std::copy(username_str.begin(), username_str.end(), username_bytes);
    info = info.substr(info.find(' ')+1);
    std::string content_str = info;
    std::vector<char> ret = std::vector<char>();
    ret.push_back(opcode[0]);
    ret.push_back(opcode[1]);
    for (int i = 0; i < (int)(username_str.size()); i++) {
        ret.push_back(username_bytes[i]);
    }
    ret.push_back('\0');
    for (int i = 0; i < (int)(content_str.size()); i++) {
        ret.push_back(content_str[i]);
    }
    ret.push_back('\0');
    ret.push_back(';');
    return ret;
}

std::string MessageEncoderDecoder::currentDateTime() {
    time_t now = time(0);
    struct tm tstruct;
    char buf[17];
    tstruct = *localtime(&now);
    strftime(buf, (int)(sizeof(buf)), "%d-%m-%Y %H:%M", &tstruct);
    return buf;
}


std::vector<char> MessageEncoderDecoder::handleLogStat(std::string info) {
    char* opcode = new char[2];
    shortToBytes((short) 7, opcode);
    char* users_list_str = new char[(int)(info.size())];
    std::copy(info.begin(), info.end(), users_list_str);
    std::vector<char> ret = std::vector<char>();
    ret.push_back(opcode[0]);
    ret.push_back(opcode[1]);
    for (int i = 0; i < (int)(info.size()); i++) {
        ret.push_back(users_list_str[i]);
    }
    ret.push_back('\0');
    ret.push_back(';');
    return ret;
}

std::vector<char> MessageEncoderDecoder::handleStat(std::string info) {
    char* opcode = new char[2];
    shortToBytes((short) 8, opcode);
    char* users_list_str = new char[(int)(info.size())];
    std::copy(info.begin(), info.end(), users_list_str);
    std::vector<char> ret = std::vector<char>();
    ret.push_back(opcode[0]);
    ret.push_back(opcode[1]);
    for (int i = 0; i < (int)(info.size()); i++) {
        ret.push_back(users_list_str[i]);
    }
    ret.push_back('\0');
    ret.push_back(';');
    return ret;
}

std::vector<char> MessageEncoderDecoder::handleBlock(std::string info) {
    char* opcode = new char[2];
    shortToBytes((short) 12, opcode);
    char* username_bytes = new char[(int)(info.size())];
    std::copy(info.begin(), info.end(), username_bytes);
    std::vector<char> ret = std::vector<char>();
    ret.push_back(opcode[0]);
    ret.push_back(opcode[1]);
    for (int i = 0; i < (int)(info.size()); i++) {
        ret.push_back(username_bytes[i]);
    }
    ret.push_back('\0');
    ret.push_back(';');
    return ret;
}