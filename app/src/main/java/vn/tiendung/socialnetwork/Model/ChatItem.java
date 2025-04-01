package vn.tiendung.socialnetwork.Model;

import com.google.gson.annotations.SerializedName;

public class ChatItem {
    @SerializedName("conversationId")
    private String conversationId;

    @SerializedName("user")
    private User user;

    @SerializedName("lastMessage")
    private LastMessage lastMessage;

    @SerializedName("unreadMessages")
    private int unreadMessages;

    public static class User {
        @SerializedName("id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("avatar")
        private String avatar;

        @SerializedName("isOnline")
        private boolean isOnline;

        @SerializedName("lastSeen")
        private String lastSeen;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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
    }

    public static class LastMessage {
        @SerializedName("content")
        private String content;

        @SerializedName("sender_id")
        private String senderId;

        @SerializedName("timestamp")
        private String timestamp;

        @SerializedName("message_type")
        private String messageType;

        @SerializedName("Time")
        private String time;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getSenderId() {
            return senderId;
        }

        public void setSenderId(String senderId) {
            this.senderId = senderId;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getMessageType() {
            return messageType;
        }

        public void setMessageType(String messageType) {
            this.messageType = messageType;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }

    // Getters and Setters
    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LastMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(LastMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }
}

