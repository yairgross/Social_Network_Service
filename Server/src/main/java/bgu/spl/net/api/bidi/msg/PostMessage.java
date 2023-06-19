package bgu.spl.net.api.bidi.msg;

import bgu.spl.net.api.bidi.BGSEncoderDecoder;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;

public class PostMessage extends InternalMessage{

    private String content;
    private LinkedList<String> mentionedUsers;

    public PostMessage(String content) {
        super(5);
        this.content = content;
        extractMentionedUsers();
    }

    public PostMessage(byte[] bytes) {
        super(5);
        byte[] contentBytes = Arrays.copyOfRange(bytes, 2, bytes.length - 1);
        content = new String(contentBytes, StandardCharsets.UTF_8);
        extractMentionedUsers();
    }

    public String getContent() {
        return content;
    }

    public LinkedList<String> getMentionedUsers() {
        return mentionedUsers;
    }

    private void extractMentionedUsers() {
        mentionedUsers = new LinkedList<String>();
        if (content.contains("@")) {
            int index = content.indexOf('@');
            while (index >= 0 && index < content.length()-1) {
                String user = "";
                if (content.indexOf(' ',index+1) >= 0)
                    user = content.substring(index+1, content.indexOf(' ', index+1));
                else
                    user = content.substring(index+1);
                mentionedUsers.addLast(user);
                index = content.indexOf('@',index+1);
            }
        }
    }

}
