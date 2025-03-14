package vn.tiendung.socialnetwork.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserModel implements Serializable {
    @SerializedName("_id")
    private String _id;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("password") // Không gửi password trong response
    private String password;

    public UserModel(String _id, String username, String email) {
        this._id = _id;
        this.username = username;
        this.email = email;
    }

    public UserModel(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getId() {
        return _id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setId(String id) {
        this._id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
