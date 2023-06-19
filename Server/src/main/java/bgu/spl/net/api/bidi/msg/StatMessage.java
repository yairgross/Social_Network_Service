package bgu.spl.net.api.bidi.msg;

import bgu.spl.net.api.bidi.BGSEncoderDecoder;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;

public class StatMessage extends InternalMessage{

    private final String usernames;
    private LinkedList<String> usersList;

    public StatMessage(String usernames) {
        super(8);
        this.usernames = usernames;
        extractUsernames();
    }

    public StatMessage(byte[] bytes) {
        super(8);
        byte[] usernamesBytes = Arrays.copyOfRange(bytes, 2, BGSEncoderDecoder.findNextZero(2, bytes));
        usernames = new String(usernamesBytes, StandardCharsets.UTF_8);
        extractUsernames();
    }

    public String getUsernames() {
        return usernames;
    }

    public LinkedList<String> getUsernamesList() {
        return usersList;
    }

    private void extractUsernames() {
        String names = usernames;
        usersList = new LinkedList<String>();
        while (names.contains("|")) {
            int index = names.indexOf('|');
            String currentUser = names.substring(0, index);
            usersList.addLast(currentUser);
            String toRemove = currentUser + '|';
            names = names.substring(toRemove.length());
        }
        if (names.length() > 0)
            usersList.addLast(names);
    }




}
