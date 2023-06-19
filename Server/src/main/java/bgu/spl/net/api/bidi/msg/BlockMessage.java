package bgu.spl.net.api.bidi.msg;

import bgu.spl.net.api.bidi.BGSEncoderDecoder;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BlockMessage extends InternalMessage {

    private final String targetUsername;

    public BlockMessage(String targetUsername) {
        super(12);
        this.targetUsername = targetUsername;
    }

    public BlockMessage(byte[] bytes) {
        super(12);
        byte[] usernameBytes = Arrays.copyOfRange(bytes, 2, BGSEncoderDecoder.findNextZero(2, bytes));
        targetUsername = new String(usernameBytes, StandardCharsets.UTF_8);
    }

    public String getTargetUsername() {
        return targetUsername;
    }

}
