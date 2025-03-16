package vn.tiendung.socialnetwork.Model;

public class Post {
    private int avatar;
    private String name;
    private String content;

    public Post(int avatar, String name, String content) {
        this.avatar = avatar;
        this.name = name;
        this.content = content;
    }

    public int getAvatar() {
        return avatar;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }
}

