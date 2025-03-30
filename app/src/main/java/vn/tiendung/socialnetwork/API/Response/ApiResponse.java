package vn.tiendung.socialnetwork.API.Response;

public class ApiResponse<T> {
    private boolean success;
    private T chatList;

    public boolean isSuccess() {
        return success;
    }

    public T getChatList() {
        return chatList;
    }
}
