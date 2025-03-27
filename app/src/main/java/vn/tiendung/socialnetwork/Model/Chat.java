package vn.tiendung.socialnetwork.Model;

public class Chat {
    private String id;
    private String userName;
    private String lastMessage;
    private String lastMessageTime;
    private int unreadCount;
    private boolean isOnline;
    private String avatarUrl;

    public Chat(String id, String userName, String lastMessage, String lastMessageTime, int unreadCount, boolean isOnline, String avatarUrl) {
        this.id = id;
        this.userName = userName;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.unreadCount = unreadCount;
        this.isOnline = isOnline;
        this.avatarUrl = avatarUrl;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
