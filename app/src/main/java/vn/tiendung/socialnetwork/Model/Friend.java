package vn.tiendung.socialnetwork.Model;

/*Tạo tạm một class để chứa các thông tin về bạn bè*/
public class Friend {
    public static final int TYPE_REQUEST = 0;
    public static final int TYPE_SUGGEST = 1;
    private String name;
    private String mutualFriends;
    private String avatarResId;

    private boolean isFriend;

    private boolean isRequestSent;

    private int type;

    private String userId;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Friend(String userId,String name, String mutualFriends, String avatarResId,boolean isFriend) {
        this.userId = userId;
        this.name = name;
        this.mutualFriends = mutualFriends;
        this.avatarResId = avatarResId;
        this.isFriend = isFriend;
    }
    public Friend(String userId,String name, String avatarResId, String mutualFriends, int type) {
        this.userId = userId;
        this.name = name;
        this.mutualFriends = mutualFriends;
        this.avatarResId = avatarResId;
        this.type = type;
    }
    public Friend(String userId,String name, String avatarResId, String mutualFriends, int type,boolean isRequestSent) {
        this.userId = userId;
        this.name = name;
        this.mutualFriends = mutualFriends;
        this.avatarResId = avatarResId;
        this.type = type;
        this.isRequestSent = isRequestSent;
    }

    public String getName() {
        return name;
    }

    public String getMutualFriends() {
        return mutualFriends;
    }

    public String getAvatarResId() {
        return avatarResId;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isRequestSent() {
        return isRequestSent;
    }

    public void setRequestSent(boolean requestSent) {
        isRequestSent = requestSent;
    }
}
