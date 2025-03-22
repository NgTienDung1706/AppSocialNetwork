package vn.tiendung.socialnetwork.Model;

/*Tạo tạm một class để chứa các thông tin về bạn bè*/
public class Friend {
    public static final int TYPE_REQUEST = 1;
    public static final int TYPE_SUGGEST = 2;
    private String name;
    private int mutualFriends;
    private int avatarResId;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Friend(String name, int mutualFriends, int avatarResId) {
        this.name = name;
        this.mutualFriends = mutualFriends;
        this.avatarResId = avatarResId;
    }
    public Friend(String name, int avatarResId, int mutualFriends, int type) {
        this.name = name;
        this.mutualFriends = mutualFriends;
        this.avatarResId = avatarResId;
        this.type = type;
    }
    public String getName() {
        return name;
    }

    public int getMutualFriends() {
        return mutualFriends;
    }

    public int getAvatarResId() {
        return avatarResId;
    }
}
