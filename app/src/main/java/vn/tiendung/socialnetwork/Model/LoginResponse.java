package vn.tiendung.socialnetwork.Model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("user")
    private UserModel user;

    public String getMessage() {
        return message;
    }

    public UserModel getUser() {
        return user;
    }
}
