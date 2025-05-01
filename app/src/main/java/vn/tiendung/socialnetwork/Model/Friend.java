package vn.tiendung.socialnetwork.Model;

/*Tạo tạm một class để chứa các thông tin về bạn bè*/
public class Friend {
    public static final int TYPE_REQUEST = 1;
    public static final int TYPE_SUGGEST = 2;
    private String name;
    private String mutualFriends;
    private String avatarResId;

    private boolean isFriend;

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Friend(String name, String mutualFriends, String avatarResId,boolean isFriend) {
        this.name = name;
        this.mutualFriends = mutualFriends;
        this.avatarResId = avatarResId;
        this.isFriend = isFriend;
    }
    public Friend(String name, String avatarResId, String mutualFriends, int type) {
        this.name = name;
        this.mutualFriends = mutualFriends;
        this.avatarResId = avatarResId;
        this.type = type;
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
}
