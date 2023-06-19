package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.Server;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionsImpl<T> implements Connections<T>{

    private ConcurrentHashMap<Integer, User<T>> idsToUsers = new ConcurrentHashMap<Integer, User<T>>();
    private int nextAvailableID = 0;
    private String[] forbiddenWords = {"get", "schwifty", "pickle", "Rick", "OOH", "WEE"};


    public int nextID() {
        int nextId = nextAvailableID;
        nextAvailableID++;
        return nextId;
    }

    @Override
    public boolean send(int connectionId, T msg) {
//        if (isRegistered(connectionId)) {
            idsToUsers.get(connectionId).getHandler().send(msg);
            return true;
//        }
//        return false;
    }

    @Override
    public void broadcast(T msg) {
        Iterator<User<T>> iter = idsToUsers.values().iterator();
        while (iter.hasNext())
            iter.next().getHandler().send(msg);
    }

    public void multicast(LinkedList<Integer> connectionIds, T msg) {
        for (int id : connectionIds)
            send(id, msg);
    }

    public void addToInbox(int id, T msg) {
        if (isRegistered(id))
            idsToUsers.get(id).addToInbox(msg);
    }

    public void connect(int newConnectionId, String username) {
        int id = getIdByName(username);
        if (id >= 0) {
            User<T> user = idsToUsers.get(id);
            if(id != newConnectionId)
                idsToUsers.remove(id);
            User<T> newUser = idsToUsers.get(newConnectionId);
            newUser.register();
            newUser.connect();
            newUser.setData(user.getData());
            //user.removeData();
            //user.connect();
            newUser.setInbox(user.getInbox());
            while (!newUser.getInbox().isEmpty())
                newUser.getHandler().send(newUser.removeFromInbox());
        }
    }

    @Override
    public void disconnect(int connectionId) {
        if (isRegistered(connectionId))
            idsToUsers.get(connectionId).disconnect();
    }

    public boolean isConnected(int connectionId) {
        return isRegistered(connectionId) && idsToUsers.get(connectionId).isConnected();
    }

    public void register(int connectionId, UserData data) {
        if (idsToUsers.containsKey(connectionId)) {
            User<T> user = idsToUsers.get(connectionId);
            user.setData(data);
            user.register();
        }
    }

    public void add(int connectionId, ConnectionHandler<T> handler) {
        User<T> user = new User<T>();
        user.setHandler(handler);
        idsToUsers.put(connectionId, user);
    }

    public boolean isRegistered(int connectionId) {
        if (idsToUsers.containsKey(connectionId)) {
            return idsToUsers.get(connectionId) != null && idsToUsers.get(connectionId).isRegistered();
        }
        return false;
    }

    public UserData getUserByID(int id) {
        if(isRegistered(id))
            return idsToUsers.get(id).getData();
        return null;
    }

    public UserData getUserByName(String username) {
        Iterator<Integer> iter = idsToUsers.keySet().iterator();
        while(iter.hasNext()) {
            UserData currentUser = idsToUsers.get(iter.next()).getData();
            if (currentUser != null && currentUser.getUsername().equals(username))
                return currentUser;
        }
        return null;
    }

    public int getIdByName(String username) {
        Iterator<Integer> iter = idsToUsers.keySet().iterator();
        while (iter.hasNext()) {
            int currentID = iter.next();
            if (idsToUsers.get(currentID).getData() != null && idsToUsers.get(currentID).getData().getUsername().equals(username))
                return currentID;
        }
        return -1;
    }

    public LinkedList<String> getFollowersOf(int connectionId) {
        if (isRegistered(connectionId)) {
            ConcurrentLinkedDeque<UserData> followers =  idsToUsers.get(connectionId).getData().getFollowers();
            LinkedList<String> ret = new LinkedList<>();
            for (UserData user : followers) {
                ret.addLast(user.getUsername());
            }
            return ret;
        }
        return null;
    }

    public boolean follow(int follower, int targetUser) {
        if (isRegistered(follower) && isRegistered(targetUser) && isConnected(follower)) {
            boolean flag = idsToUsers.get(follower).getData().follow(idsToUsers.get(targetUser).getData());
            return flag;
        }
        return false;
    }

    public boolean unfollow(int follower, int targetUser) {
        if (isRegistered(follower) && isRegistered(targetUser) && isConnected(follower))
            return idsToUsers.get(follower).getData().unfollow(idsToUsers.get(targetUser).getData());
        return false;
    }

    public String[] getForbiddenWords() { return forbiddenWords; }

    public LinkedList<UserData> getConnectedUsers(int id) {
        LinkedList<UserData> connectedUsers = new LinkedList<>();
        Iterator<Integer> iter = idsToUsers.keySet().iterator();
        while (iter.hasNext()) {
            int currentUserId = iter.next();
            if (isConnected(currentUserId) && !getUserByID(id).isBlocking(getUserByID(currentUserId))) {
                connectedUsers.addLast(idsToUsers.get(currentUserId).getData());
            }
        }
        return connectedUsers;
    }

    private class User<T> {

        private UserData data = null;
        private ConnectionHandler<T> handler = null;
        private ConcurrentLinkedQueue<T> inbox = new ConcurrentLinkedQueue<>();
        private boolean isRegistered = false;
        private boolean isConnected = false;

        public UserData getData() {
            return data;
        }

        public void setData(UserData data) {
            this.data = data;
        }

        public ConnectionHandler<T> getHandler() {
            return handler;
        }

        public void setHandler(ConnectionHandler<T> handler) {
            this.handler = handler;
        }

        public ConcurrentLinkedQueue<T> getInbox() {
            return inbox;
        }

        public boolean isRegistered() {
            return isRegistered;
        }

        public void register() {
            isRegistered = true;
        }

        public void unregister() {
            isRegistered = false;
        }

        public boolean isConnected() {
            return isConnected;
        }

        public void connect() {
            if (!isConnected) {
                isConnected = true;
            }
        }

        public void disconnect() {
            if (isConnected) {
                isConnected = false;
            }
        }

        public void addToInbox(T message) {
            synchronized (inbox) {
                inbox.add(message);
            }
        }

        public T removeFromInbox() {
            synchronized (inbox) {
                if (!inbox.isEmpty())
                    return inbox.remove();
                return null;
            }
        }

        public void setInbox(ConcurrentLinkedQueue<T> oldInbox){
            this.inbox = oldInbox;
        }

        public void removeData(){
            this.data = null;
        }
    }
}

