package vn.tiendung.socialnetwork.Model;

public class Message {
    private String senderId;
    private String content;
    private String imageUrl;
    private String timestamp;

    public Message(String senderId, String content, String imageUrl, String timestamp) {
        this.senderId = senderId;
        this.content = content;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getContent() {
        return content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
