package vn.tiendung.socialnetwork.Model;

import java.util.List;

public class UserProfile {
    private String id;

    private String username;
    private String fullname;
    private String avatar;
    private int friendsCount;
    private int postsCount;
    private String bio;
    private List<String> favoriteTags;

    public UserProfile(String id, String username, String fullname, String avatar, int friendsCount, int postsCount, String bio, List<String> favoriteTags) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.avatar = avatar;
        this.friendsCount = friendsCount;
        this.postsCount = postsCount;
        this.bio = bio;
        this.favoriteTags = favoriteTags;
    }

    public UserProfile(String fullname, String bio, String avatar) {
        this.fullname = fullname;
        this.bio = bio;
        this.avatar = avatar;
    }

    public String getId() { return id; }

    public String getUsername() {
        return username;
    }
    public String getFullname() { return fullname; }
    public String getAvatar() { return avatar; }
    public int getFriendsCount() { return friendsCount; }
    public int getPostsCount() { return postsCount; }
    public String getBio() { return bio; }
    public List<String> getFavoriteTags() { return favoriteTags; }

}
