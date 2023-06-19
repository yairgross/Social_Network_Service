package bgu.spl.net.api.bidi.msg;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class FollowMessage extends InternalMessage{

    private final byte follow;
    private final String targetUsername;

    public FollowMessage(byte follow, String targetUsername) {
        super(4);
        this.follow = follow;
        this.targetUsername = targetUsername;
    }

    public FollowMessage(byte[] bytes) {
        super(4);
        follow = bytes[2];
        byte[] usernameBytes = Arrays.copyOfRange(bytes, 3, bytes.length);
        targetUsername = new String(usernameBytes, StandardCharsets.UTF_8);
    }

    public boolean isFollow() {
        return follow == '0';
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    @Override
    public byte[] getEncodedAck() {
        byte[] username = targetUsername.getBytes(StandardCharsets.UTF_8);
        byte[] ret = new byte[username.length+2];
        for (int i = 0; i < username.length; i++) {
            ret[i] = username[i];
        }
        ret[ret.length-2] = '\0';
        ret[ret.length-1] = ';';
        return ret;
    }
}
