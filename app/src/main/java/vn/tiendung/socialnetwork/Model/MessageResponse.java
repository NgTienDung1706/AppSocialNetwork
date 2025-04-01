package vn.tiendung.socialnetwork.Model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MessageResponse {
    @SerializedName("messages")
    private List<Message> messages;

    @SerializedName("total")
    private int total;

    @SerializedName("currentPage")
    private int currentPage;

    @SerializedName("totalPages")
    private int totalPages;

    public List<Message> getMessages() {
        return messages;
    }

    public int getTotal() {
        return total;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
