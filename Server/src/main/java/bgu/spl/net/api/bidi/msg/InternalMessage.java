package bgu.spl.net.api.bidi.msg;
import bgu.spl.net.api.bidi.*;

/**
 * A marker class representing a message between the {@link BGSProtocol} and the {@link BGSEncoderDecoder}
 */
public class InternalMessage {

    protected final short opcode;

    public InternalMessage(int opcode) {
        this.opcode = (short) opcode;
    }

    public short getOpcode() {
        return opcode;
    }

    public byte[] getEncodedAck() {return new byte[0];}

    public byte[] getEncodedError() {return new byte[0];}

}
