package vn.tiendung.socialnetwork.Model;

import java.util.List;

public class Comment {
    private String id;
    private String parent; // reply other comment
    private String postId;
    private String userId;
    private String userName;
    private String avatarUrl;
    private String content;
    private String createdAt; // ISO 8601 string: "2025-04-07T14:00:00Z"
    private List<String> likes; // Danh sách userId đã like
    private boolean isDeleted;
    private boolean myLike;

    public boolean isMyLike() {
        return myLike;
    }

    public void setMyLike(boolean myLike) {
        this.myLike = myLike;
    }

    public Comment() {}

    public Comment(String id, String postId, String userId, String userName,
                   String avatarUrl, String content, String createdAt,
                   List<String> likes, boolean isDeleted, boolean myLike) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
        this.userName = userName;
        this.avatarUrl = avatarUrl;
        this.content = content;
        this.createdAt = createdAt;
        this.likes = likes;
        this.isDeleted = isDeleted;
        this.myLike = myLike;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
    public String getId() { return id; }
    public String getPostId() { return postId; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getAvatarUrl() { return avatarUrl; }
    public String getContent() { return content; }
    public String getCreatedAt() { return createdAt; }
    public List<String> getLikes() { return likes; }
    public boolean isDeleted() { return isDeleted; }

    public void setId(String id) { this.id = id; }
    public void setPostId(String postId) { this.postId = postId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public void setContent(String content) { this.content = content; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setLikes(List<String> likes) { this.likes = likes; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public boolean isLikedBy(String currentUserId) {
        return likes != null && likes.contains(currentUserId);
    }
}
