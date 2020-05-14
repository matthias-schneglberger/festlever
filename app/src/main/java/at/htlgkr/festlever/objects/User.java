package at.htlgkr.festlever.objects;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String username;
    private String email;
    private String password;

    List<User> friends;
    List<User> friendRequests;

    List<Event> provideEvents;
    List<Event> joinedEvents;

    public User() {}

    public User(String username, String email, String password, List<User> friends, List<User> friendRequests, List<Event> provideEvents, List<Event> joinedEvents) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.friends = friends;
        this.friendRequests = friendRequests;
        this.provideEvents = provideEvents;
        this.joinedEvents = joinedEvents;
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.email = email;
        this.password = password;
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

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public List<User> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(List<User> friendRequests) {
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
