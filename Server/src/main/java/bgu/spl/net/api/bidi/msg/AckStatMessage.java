package bgu.spl.net.api.bidi.msg;

import bgu.spl.net.api.bidi.BGSEncoderDecoder;
import bgu.spl.net.api.bidi.UserData;

import java.util.LinkedList;

public class AckStatMessage extends AckMessage {

    LinkedList<UserData> users;

    public AckStatMessage(InternalMessage msg, LinkedList<UserData> users) {
        super(msg);
        this.users = users;
    }

    @Override
    public byte[] getEncodedAck() {
        byte[] ret = new byte[users.size() * 12 + 1];
        for(int i = 0; i < users.size(); i++){
            UserData currentUser = users.get(i);
//            AckLogStatMessage tempMessage = new AckLogStatMessage(msg, currentUser);
//            byte[] tempEncoded = tempMessage.getEncodedAck();
            byte[] tempEncoded = getUserStat(currentUser);
            for (int j = 0; j < 12; j++) {
                ret[i*12 + j] = tempEncoded[j];
            }
        }
        ret[ret.length-1] = ';';
        return ret;
    }

    private byte[] getUserStat(UserData user) {
        byte[] ackOpcode = BGSEncoderDecoder.shortToBytes((short) 10);
        byte[] msgStatOpcode = BGSEncoderDecoder.shortToBytes(msg.getOpcode());
        byte[] age = BGSEncoderDecoder.shortToBytes(user.getAge());
        byte[] numberOfPosts = BGSEncoderDecoder.shortToBytes(user.getNumberOfPosts());
        byte[] numberOfFollowers = BGSEncoderDecoder.shortToBytes(user.getNumberOfFollowers());
        byte[] numberOfFollowings = BGSEncoderDecoder.shortToBytes(user.getNumberOfFollowings());
        byte ret[] = {
                ackOpcode[0], ackOpcode[1],
                msgStatOpcode[0], msgStatOpcode[1],
                age[0], age[1],
                numberOfPosts[0], numberOfPosts[1],
                numberOfFollowers[0], numberOfFollowers[1],
                numberOfFollowings[0], numberOfFollowings[1]
        };
        return ret;
    }
}
