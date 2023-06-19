package bgu.spl.net.api.bidi.msg;

import bgu.spl.net.api.bidi.BGSEncoderDecoder;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class RegisterMessage extends InternalMessage{

    private final String username;
    private final String password;
    private final String birthday;

    public RegisterMessage(String username, String password, String birthday) {
        super(1);
        this.username = username;
        this.password = password;
        this.birthday = birthday;
    }

    public RegisterMessage(byte[] bytes) {
        super(1);
        int i = BGSEncoderDecoder.findNextZero(2, bytes);
        byte[] usernameBytes = Arrays.copyOfRange(bytes, 2, i);
        username = new String(usernameBytes, StandardCharsets.UTF_8);
        int j = BGSEncoderDecoder.findNextZero(i+1, bytes);
        byte[] passwordBytes = Arrays.copyOfRange(bytes, i+1, j);
        password = new String(passwordBytes, StandardCharsets.UTF_8);
        byte[] birthdayBytes = Arrays.copyOfRange(bytes, j+1, bytes.length);
        birthday = new String(birthdayBytes, StandardCharsets.UTF_8);
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getBirthday() {
        return birthday;
    }

}
