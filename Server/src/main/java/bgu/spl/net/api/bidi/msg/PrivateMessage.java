package bgu.spl.net.api.bidi.msg;

import bgu.spl.net.api.bidi.BGSEncoderDecoder;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PrivateMessage extends InternalMessage{

    private final String targetUsername;
    private String content;
    private final String time;

    public PrivateMessage(String targetUsername, String content, String time) {
        super(6);
        this.targetUsername = targetUsername;
        this.content = content;
        this.time = time;
    }

    public PrivateMessage(byte[] bytes) {
        super(6);
        int i = BGSEncoderDecoder.findNextZero(2, bytes);
        byte[] usernameBytes = Arrays.copyOfRange(bytes, 2, i);
        targetUsername = new String(usernameBytes, StandardCharsets.UTF_8);
        int j = BGSEncoderDecoder.findNextZero(i+1, bytes);
        byte[] contentBytes = Arrays.copyOfRange(bytes, i+1, bytes.length - 1);
        content = new String(contentBytes, StandardCharsets.UTF_8);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        time = dtf.format(now).toString();
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public void filter(String[] forbiddenWords) {
        for (String word : forbiddenWords) {
            filter(word);
        }
    }

    private void filter(String forbiddenWord) {
        if (content.contains(forbiddenWord)) {
            content = content.replaceAll(forbiddenWord, "<filtered>");
        }
    }
}
