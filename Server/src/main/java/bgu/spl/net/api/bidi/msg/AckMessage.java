package bgu.spl.net.api.bidi.msg;

import bgu.spl.net.api.bidi.BGSEncoderDecoder;

import java.util.LinkedList;

public class AckMessage extends InternalMessage {

    protected InternalMessage msg;

    public AckMessage(InternalMessage msg) {
        super(10);
        this.msg = msg;
    }

    @Override
    public byte[] getEncodedAck() {
        byte[] opcode = BGSEncoderDecoder.shortToBytes(getOpcode());
        byte[] msgOpcode = BGSEncoderDecoder.shortToBytes(msg.getOpcode());
        byte[] optional = msg.getEncodedAck();
        byte[] ret = new byte[5 + optional.length];
        ret[0] = opcode[0];
        ret[1] = opcode[1];
        ret[2] = msgOpcode[0];
        ret[3] = msgOpcode[1];
        int i = 4;
        if(optional.length > 0) {
            for (byte b : optional) {
                ret[i] = b;
                i++;
            }
        }
        ret[i] = ';';
        return ret;
    }

    public InternalMessage getAssociatedMessage() {return msg;}
}
