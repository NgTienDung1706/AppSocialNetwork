package vn.tiendung.socialnetwork.Model;

import java.util.List;

public class UserProfile {
    private String id;

    private String username;
    private String name;
    private String avatar;
    private int friendsCount;
    private int postsCount;
    private String bio;

    private boolean isOnline;
    private String lastSeen;
    private List<String> favoriteTags;

    public UserProfile(String id, String username, String name, String avatar, int friendsCount, int postsCount, String bio, List<String> favoriteTags) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.avatar = avatar;
        this.friendsCount = friendsCount;
        this.postsCount = postsCount;
        this.bio = bio;
        this.favoriteTags = favoriteTags;
    }
    public UserProfile(String id, String username, String name, String avatar, int friendsCount, int postsCount, String bio, List<String> favoriteTags, boolean isOnline, String lastSeen) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.avatar = avatar;
        this.friendsCount = friendsCount;
        this.postsCount = postsCount;
        this.bio = bio;
        this.favoriteTags = favoriteTags;
        this.isOnline = isOnline;
        this.lastSeen = lastSeen;
    }

    public UserProfile(String name, String bio, String avatar) {
        this.name = name;
        this.bio = bio;
        this.avatar = avatar;
    }

    public String getId() { return id; }

    public String getUsername() {
        return username;
    }
    public String getFullname() { return name; }
    public String getAvatar() { return avatar; }
    public int getFriendsCount() { return friendsCount; }
    public int getPostsCount() { return postsCount; }
    public String getBio() { return bio; }
    public List<String> getFavoriteTags() { return favoriteTags; }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullname(String fullname) {
        this.name = fullname;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

    public void setPostsCount(int postsCount) {
        this.postsCount = postsCount;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setFavoriteTags(List<String> favoriteTags) {
        this.favoriteTags = favoriteTags;
    }
}
