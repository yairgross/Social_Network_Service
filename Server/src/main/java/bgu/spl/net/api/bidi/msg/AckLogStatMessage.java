package bgu.spl.net.api.bidi.msg;

import bgu.spl.net.api.bidi.BGSEncoderDecoder;
import bgu.spl.net.api.bidi.UserData;

public class AckLogStatMessage extends AckMessage{
    
    private UserData user;
    
    public AckLogStatMessage(InternalMessage msg, UserData user) {
        super(msg);
        this.user = user;
    }

    @Override
    public byte[] getEncodedAck() {
        byte[] ackOpcode = BGSEncoderDecoder.shortToBytes((short) 10);
        byte[] logStatOpcode = BGSEncoderDecoder.shortToBytes((short)super.getOpcode());
        byte[] age = BGSEncoderDecoder.shortToBytes(user.getAge());
        byte[] numberOfPosts = BGSEncoderDecoder.shortToBytes(user.getNumberOfPosts());
        byte[] numberOfFollowers = BGSEncoderDecoder.shortToBytes(user.getNumberOfFollowers());
        byte[] numberOfFollowings = BGSEncoderDecoder.shortToBytes(user.getNumberOfFollowings());
        byte ret[] = {
                ackOpcode[0], ackOpcode[1],
                logStatOpcode[0], logStatOpcode[1],
                age[0], age[1],
                numberOfPosts[0], numberOfPosts[1],
                numberOfFollowers[0], numberOfFollowers[1],
                numberOfFollowings[0], numberOfFollowings[1], ';'
        };
        return ret;
    }
}
