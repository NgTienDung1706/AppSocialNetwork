package vn.tiendung.socialnetwork.Model;

public class ChatItem {
    private String conversationId;
    private UserProfile user;
    private LastMessage lastMessage;
    private int unreadMessages;

    public ChatItem(String conversationId, UserProfile user, LastMessage lastMessage, int unreadMessages) {
        this.conversationId = conversationId;
        this.user = user;
        this.lastMessage = lastMessage;
        this.unreadMessages = unreadMessages;
    }



    public String getConversationId() {
        return conversationId;
    }

    public UserProfile getUser() {
        return user;
    }

    public LastMessage getLastMessage() {
        return lastMessage;
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }
}

