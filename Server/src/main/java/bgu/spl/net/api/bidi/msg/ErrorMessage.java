package bgu.spl.net.api.bidi.msg;

import bgu.spl.net.api.bidi.BGSEncoderDecoder;

public class ErrorMessage extends InternalMessage {

    private final short msgOpcode;

    public ErrorMessage(short msgOpcode) {
        super(11);
        this.msgOpcode = msgOpcode;
    }

    public int getMsgOpcode() {
        return msgOpcode;
    }

    @Override
    public byte[] getEncodedError() {
        byte[] errorOpcode = BGSEncoderDecoder.shortToBytes((short) 11);
        byte[] msgOpCode = BGSEncoderDecoder.shortToBytes(msgOpcode);
        byte[] ret = {
                errorOpcode[0], errorOpcode[1],
                msgOpCode[0], msgOpCode[1], ';'
        };
        return ret;
    }
}
