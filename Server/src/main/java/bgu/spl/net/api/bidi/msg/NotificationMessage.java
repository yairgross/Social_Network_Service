package bgu.spl.net.api.bidi.msg;

import bgu.spl.net.api.bidi.BGSEncoderDecoder;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class NotificationMessage extends InternalMessage {

    private final byte type;
    private String postingUser;
    private String content;

    public NotificationMessage(byte type, String postingUser, String content) {
        super(9);
        this.type = type;
        this.postingUser = postingUser;
        this.content = content;
    }

    public byte getType() {
        return type;
    }

    public String getPostingUser() {
        return postingUser;
    }

    public String getContent() {
        return content;
    }

    @Override
    public byte[] getEncodedAck() {
        byte[] opcode = BGSEncoderDecoder.shortToBytes((short) 9);
        byte[] posting = getPostingUser().getBytes(StandardCharsets.UTF_8);
        byte[] content = getContent().getBytes(StandardCharsets.UTF_8);
        byte[] ret = new byte[6 + posting.length + content.length];
        ret[0] = opcode[0];
        ret[1] = opcode[1];
        ret[2] = type;
        for (int i = 0; i < posting.length; i++) {
            ret[i + 3] = posting[i];
        }
        ret[3+posting.length] = '\0';
        for (int i = 0; i < content.length; i++) {
            ret[4 + posting.length + i] = content[i];
        }
        ret[ret.length-2] = '\0';
        ret[ret.length-1] = ';';
        return ret;
    }

}
