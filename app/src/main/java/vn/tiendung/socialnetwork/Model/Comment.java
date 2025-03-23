package vn.tiendung.socialnetwork.Model;

public class Comment {
    private int id;
    /* Sau này sẽ sửa lại để lấy thông tin qua userId*/
    private String userName;
    private String content;
    private int likes;
    private int parent;

    public Comment(int id, String userName, String content, int likes, int parent) {
        this.id = id;
        this.userName = userName;
        this.content = content;
        this.likes = likes;
        this.parent = parent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }
}
