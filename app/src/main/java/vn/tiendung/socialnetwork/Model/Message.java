package vn.tiendung.socialnetwork.Model;

import com.google.gson.annotations.SerializedName;

public class Message {
    @SerializedName("_id")
    private String id;

    @SerializedName("conversation_id")
    private String conversationId;

    @SerializedName("sender_id")
    private Sender sender;

    @SerializedName("content")
    private String content;

    @SerializedName("message_type")
    private String messageType;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("reply_to")
    private String replyTo;

    @SerializedName("status")
    private String status;

    @SerializedName("is_deleted")
    private boolean isDeleted;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    public static class Sender {
        @SerializedName("_id")
        private String id;

        @SerializedName("name")
        private String name;
        @SerializedName("avatar")
        private String avatar;
        @SerializedName("username")
        private String username;

        public String getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }

    public String getId() {
        return id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public Sender getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public String getStatus() {
        return status;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
