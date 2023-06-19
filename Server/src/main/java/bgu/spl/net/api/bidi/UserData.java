package bgu.spl.net.api.bidi;

import bgu.spl.net.api.bidi.msg.RegisterMessage;


import java.time.DateTimeException;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.time.LocalDateTime;

public class UserData {

    private String username = "";
    private String password = "";
    private final short age;
    private short numberOfPosts = 0;
    private ConcurrentLinkedDeque<UserData> followings = new ConcurrentLinkedDeque<>();
    private ConcurrentLinkedDeque<UserData> followers = new ConcurrentLinkedDeque<>();
    private ConcurrentLinkedDeque<UserData> blocked = new ConcurrentLinkedDeque<>();

    public UserData(String username, String password, short age) {
        this.username = username;
        this.password = password;
        this.age = age;
    }

    public UserData(RegisterMessage message) {
        this.username = message.getUsername();
        this.password = message.getPassword();
        this.age = setAge(message.getBirthday());
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public short getAge() {
        return age;
    }

    public short getNumberOfPosts() {
        return numberOfPosts;
    }

    public ConcurrentLinkedDeque<UserData> getFollowings() {
        return followings;
    }

    public short getNumberOfFollowings() {
        return (short)(followings.size());
    }

    public ConcurrentLinkedDeque<UserData> getFollowers() {
        return followers;
    }

    public short getNumberOfFollowers() {
        return (short)(followers.size());
    }

    public ConcurrentLinkedDeque<UserData> getBlocked() {
        return blocked;
    }

    public boolean isFollowing(UserData user) {
        return followings.contains(user);
    }

    public boolean isBlocking(UserData user) {
        return blocked.contains(user);
    }

    public void incrementNumberOfPosts(int numberOfPosts) {
        this.numberOfPosts += numberOfPosts;
    }

    public boolean follow(UserData user) {
        if (!isFollowing(user) && !user.isBlocking(this) && !this.getUsername().equals(user.getUsername())) {
            followings.addLast(user);
            user.addFollower(this);
            return true;
        }
        return false;
    }

    public boolean unfollow(UserData user) {
        if (isFollowing(user)){
           followings.remove(user);
           user.removeFollower(this);
           return true;
        }
        return false;
    }

    public boolean addFollower(UserData user) {
        if (!followers.contains(user) && !isBlocking(user)) {
            followers.addLast(user);
            return true;
        }
        return false;
    }

    public boolean removeFollower(UserData user){
        if(followers.contains(user)){
            followers.remove(user);
            return true;
        }
        return false;
    }

    public void block(UserData user) {
        if (!isBlocking(user)) {
            blocked.addLast(user);
            if(user.isFollowing(this)) {
                removeFollower(user);
                user.unfollow(this);
            }
        }
    }

    private short setAge(String birthday) {
        int dayOfBirth = Integer.parseInt(birthday.substring(0, 2));
        int monthOfBirth = Integer.parseInt(birthday.substring(3,5));
        int yearOfBirth = Integer.parseInt(birthday.substring(birthday.length()-4));
        LocalDateTime dateOfBirth = LocalDateTime.of(yearOfBirth, monthOfBirth, dayOfBirth, 1, 1);
        LocalDateTime now = LocalDateTime.now();
        if (!now.isAfter(dateOfBirth)) {
            throw new DateTimeException("Invalid date of birth");
        }
        int yearsOld = now.getYear() - yearOfBirth - 1;
        if (now.getMonthValue() - monthOfBirth >= 0 && now.getDayOfMonth() - dayOfBirth >= 0) {
            yearsOld++;
        }
        return (short)yearsOld;
    }

}