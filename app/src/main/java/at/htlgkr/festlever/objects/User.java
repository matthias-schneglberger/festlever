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

    List<Event> provideEvents = new ArrayList<>();
    List<Event> joinedEvents = new ArrayList<>();

    public User() {}

    public User(String username, String password, String email) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(String username, String email, String password, List<String> friends, List<String> friendRequests, List<Event> provideEvents, List<Event> joinedEvents) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.friends = friends;
        this.friendRequests = friendRequests;
        this.provideEvents = provideEvents;
        this.joinedEvents = joinedEvents;
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

    public List<Event> getProvideEvents() {
        return provideEvents;
    }

    public void setProvideEvents(List<Event> provideEvents) {
        this.provideEvents = provideEvents;
    }

    public List<Event> getJoinedEvents() {
        return joinedEvents;
    }

    public void setJoinedEvents(List<Event> joinedEvents) {
        this.joinedEvents = joinedEvents;
    }
}
