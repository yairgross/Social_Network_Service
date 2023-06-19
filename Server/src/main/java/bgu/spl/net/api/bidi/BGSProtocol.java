package bgu.spl.net.api.bidi;

import bgu.spl.net.api.bidi.msg.*;
import sun.awt.image.ImageWatched;

import java.util.Iterator;
import java.util.LinkedList;


public class BGSProtocol implements BidiMessagingProtocol<InternalMessage>{

    private ConnectionsImpl<InternalMessage> connections = null;
    private int id = -1;
    private boolean shouldTerminate = false;

    @Override
    public void start(int connectionId, Connections<InternalMessage> connections) {
        this.id = connectionId;
        this.connections = (ConnectionsImpl) connections;
    }

    @Override
    public void process(InternalMessage message) {
        int opcode = message.getOpcode();
        InternalMessage response = null;
        if (opcode == 1)
            response = register((RegisterMessage) message);
        else if (opcode == 2)
            response = login((LoginMessage) message);
        else if (opcode == 3)
            response = logout((LogoutMessage) message);
        else if (opcode == 4)
            response = follow((FollowMessage) message);
        else if (opcode == 5)
            response = post((PostMessage) message);
        else if (opcode == 6)
            response = pm((PrivateMessage) message);
        else if (opcode == 7)
            response = logStat((LogStatMessage) message);
        else if (opcode == 8)
            response = stat((StatMessage) message);
        else if (opcode == 9)
            notification((NotificationMessage) message);
        else if (opcode == 10)
            acknowledge((AckMessage) message);
        else if (opcode == 11)
            error((ErrorMessage) message);
        else
            response = block((BlockMessage) message);

        if (response != null) {
            connections.send(id, response);
        }
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    private InternalMessage register(RegisterMessage message) {
        if (!connections.isRegistered(connections.getIdByName(message.getUsername()))) {
            connections.register(id, new UserData(message));
            return new AckMessage(message);
        }
        return new ErrorMessage(message.getOpcode());
    }

    private InternalMessage login(LoginMessage message) {
        if (connections.getUserByName(message.getUsername()) != null && connections.isRegistered(connections.getIdByName(message.getUsername()))
                && !connections.isConnected(connections.getIdByName(message.getUsername()))) {
            UserData data = connections.getUserByName(message.getUsername());
            if (message.getPassword().equals(data.getPassword()) && message.getCaptcha() == '1'){
                connections.connect(id, message.getUsername());
                return new AckMessage(message);
            }
        }
        return new ErrorMessage(message.getOpcode());
    }

    private InternalMessage logout(LogoutMessage message) {
        if (connections.isConnected(id)) {
            connections.disconnect(id);
            return new AckMessage(message);
        }
        return new ErrorMessage(message.getOpcode());
    }

    private InternalMessage follow(FollowMessage message) {
        if (connections.isRegistered(id) && connections.isRegistered(connections.getIdByName(message.getTargetUsername()))) {
            if (message.isFollow()) {
                if (connections.follow(id, connections.getIdByName(message.getTargetUsername()))) {
                    return new AckMessage(message);
                }
            } else {
                if (connections.unfollow(id, connections.getIdByName(message.getTargetUsername()))) {
                    return new AckMessage(message);
                }
            }
        }
        return new ErrorMessage(message.getOpcode());
    }

    private InternalMessage post(PostMessage message){
        if (connections.isConnected(id)) {
            String senderUserName = connections.getUserByID(id).getUsername();
            Iterator<String> mentionedUsersIter = message.getMentionedUsers().iterator();
            while (mentionedUsersIter.hasNext()) {
                String currentUsername = mentionedUsersIter.next();
                int currentId = connections.getIdByName(currentUsername);
                if(connections.getUserByID(id) != null && !connections.getUserByID(id).isBlocking(connections.getUserByID(currentId))) {
                    NotificationMessage toSend = new NotificationMessage((byte)'1', senderUserName , message.getContent());
                    if(connections.isConnected(currentId))
                        connections.send(currentId, toSend);
                    else
                        connections.addToInbox(currentId, toSend);
                }
            }
            LinkedList<String> followers = connections.getFollowersOf(id);
            for (String username : followers) {
                if (!message.getMentionedUsers().contains(username)) {
                    int currentId = connections.getIdByName(username);
                    if (id != -1) {
                        NotificationMessage toSend = new NotificationMessage((byte)'1', senderUserName , message.getContent());
                        if(connections.isConnected(currentId))
                            connections.send(currentId, toSend);
                        else
                            connections.addToInbox(currentId, toSend);
                    }
                }
            }
            connections.getUserByID(id).incrementNumberOfPosts(1);
            return new AckMessage(message);
        }
        return new ErrorMessage(message.getOpcode());
    }

    private InternalMessage pm(PrivateMessage message) {
        message.filter(connections.getForbiddenWords());
        int targetId = connections.getIdByName(message.getTargetUsername());
        if (connections.isConnected(id) && connections.isRegistered(targetId)) {
            if (connections.getUserByID((id)).isFollowing(connections.getUserByID(targetId))) {
                String senderUserName = connections.getUserByID(id).getUsername();
                String contentWithTime = message.getContent() + " " + message.getTime();
                NotificationMessage toSend = new NotificationMessage((byte)'0', senderUserName, contentWithTime);
                if(connections.isConnected(targetId))
                    connections.send(targetId, toSend);
                else
                    connections.addToInbox(targetId, toSend);
                return new AckMessage(message);
            }
        }
        return new ErrorMessage(message.getOpcode());
    }

    private InternalMessage logStat(LogStatMessage message) {
        if (connections.isConnected(id)) {
            LinkedList<UserData> connectedUsers = connections.getConnectedUsers(id);
            return new AckStatMessage(message, connectedUsers);
        }
        return new ErrorMessage(message.getOpcode());
    }

    private InternalMessage stat(StatMessage message) {
        if (connections.isConnected(id)) {
            LinkedList<String> usernames = message.getUsernamesList();
            LinkedList<UserData> users = new LinkedList<>();
            for (String username : usernames) {
                UserData currentUser = connections.getUserByName(username);
                if(currentUser != null && !connections.getUserByID(id).isBlocking(currentUser))
                    users.addLast(currentUser);
            }
            return new AckStatMessage(message, users);
        }
        return new ErrorMessage(message.getOpcode());
    }

    private void notification(NotificationMessage message) {
        String notificationType = "";
        if (message.getType() == 1)
            notificationType = "PUBLIC ";
        else
            notificationType = "PM ";
        System.out.println("NOTIFICATION " + notificationType + message.getPostingUser() + " " + message.getContent());
    }

    private void acknowledge(AckMessage message) {
        System.out.println("ACK " + message.getOpcode());
    }

    private void error(ErrorMessage message) {
        System.out.println("ERROR " + message.getOpcode());
    }

    private InternalMessage block(BlockMessage message) {
        if (connections.isRegistered(connections.getIdByName(message.getTargetUsername())) && connections.isConnected(id)) {
            UserData blockedUser = connections.getUserByName(message.getTargetUsername());
            UserData self = connections.getUserByID(id);
	    if (self != null) {
                self.block(blockedUser);
                blockedUser.block(self);
                return new AckMessage(message);
	    }
        }
        return new ErrorMessage(message.getOpcode());
    }
}
