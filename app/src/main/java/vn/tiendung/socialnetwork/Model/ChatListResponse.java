package vn.tiendung.socialnetwork.Model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatListResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("chatList")
    private List<ChatItem> chatList;

    public boolean isSuccess() {
        return success;
    }

    public List<ChatItem> getChatList() {
        return chatList;
    }
} 