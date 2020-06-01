package at.htlgkr.festlever.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String username;
    private String email;
    private String password;

    List<String> friends = new ArrayList<>();
    List<String> friendRequests = new ArrayList<>();

    List<String> provideEvents = new ArrayList<>();
    List<String> joinedEvents = new ArrayList<>();
    List<String> eventRequests = new ArrayList<>();

    public User() {}

    public User(String username, String password, String email) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(String username, String email, String password, List<String> friends, List<String> friendRequests, List<String> provideEvents, List<String> joinedEvents, List<String> eventRequests) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.friends = friends;
        this.friendRequests = friendRequests;
        this.provideEvents = provideEvents;
        this.joinedEvents = joinedEvents;
        this.eventRequests = eventRequests;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public List<String> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(List<String> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public List<String> getProvideEvents() {
        return provideEvents;
    }

    public void setProvideEvents(List<String> provideEvents) {
        this.provideEvents = provideEvents;
    }

    public List<String> getJoinedEvents() {
        return joinedEvents;
    }

    public void setJoinedEvents(List<String> joinedEvents) {
        this.joinedEvents = joinedEvents;
    }

    public List<String> getEventRequests() {
        return eventRequests;
    }

    public void setEventRequests(List<String> eventRequests) {
        this.eventRequests = eventRequests;
    }
}
