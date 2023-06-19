package bgu.spl.net.api.bidi.msg;

import bgu.spl.net.api.bidi.BGSEncoderDecoder;
import bgu.spl.net.srv.ConnectionHandler;

import javax.security.auth.callback.CallbackHandler;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class LoginMessage extends InternalMessage{

    private final String username;
    private final String password;
    private final byte captcha;

    public LoginMessage(String username, String password, byte captcha) {
        super(2);
        this.username = username;
        this.password = password;
        this.captcha = captcha;
    }

    public LoginMessage(byte[] bytes) {
        super(2);
        int i = BGSEncoderDecoder.findNextZero(2, bytes);
        byte[] usernameBytes = Arrays.copyOfRange(bytes, 2, i);
        username = new String(usernameBytes, StandardCharsets.UTF_8);
        int j = BGSEncoderDecoder.findNextZero(i+1, bytes);
        byte[] passwordBytes = Arrays.copyOfRange(bytes, i+1, j);
        password = new String(passwordBytes, StandardCharsets.UTF_8);
        captcha = bytes[j+1];
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public byte getCaptcha() {
        return captcha;
    }
}
