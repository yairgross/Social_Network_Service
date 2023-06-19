package bgu.spl.net.api.bidi;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.msg.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class BGSEncoderDecoder implements MessageEncoderDecoder<InternalMessage> {

    private LinkedList<Byte> bytes = new LinkedList<>();

    @Override
    public InternalMessage decodeNextByte(byte nextByte) {
        if (nextByte == ';') {
            byte[] bytesArray = getBytesArray(bytes);
            bytes.clear();
            byte[] opcodeBytes = {bytesArray[0], bytesArray[1]};
            short opcode = bytesToShort(opcodeBytes);
            switch (opcode) {
                case 1 : return new RegisterMessage(bytesArray);
                case 2 : return new LoginMessage(bytesArray);
                case 3 : return new LogoutMessage();
                case 4 : return new FollowMessage(bytesArray);
                case 5 : return new PostMessage(bytesArray);
                case 6 : return new PrivateMessage(bytesArray);
                case 7 : return new LogStatMessage();
                case 8 : return new StatMessage(bytesArray);
                case 12 : return new BlockMessage(bytesArray);
            }
        }
        bytes.addLast(nextByte);
        return null;
    }

    @Override
    public byte[] encode(InternalMessage message) {
        byte[] encoded = null;
        if (message.getOpcode() == 11)
            encoded = message.getEncodedError();
        else {
            encoded = message.getEncodedAck();
        }
        return encoded;
    }

    private byte[] getBytesArray(LinkedList<Byte> bytes) {
        byte[] ret = new byte[bytes.size()];
        int currentIndex = 0;
        for (Byte b : bytes) {
            ret[currentIndex] = b;
            currentIndex++;
        }
        return ret;
    }

    public static short bytesToShort(byte[] byteArr) {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public static byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    public static int findNextZero(int from, byte[] bytes) {
        for (int i = from; i < bytes.length; i++) {
            if (bytes[i] == '\0')
                return i;
        }
        return -1;
    }
}
