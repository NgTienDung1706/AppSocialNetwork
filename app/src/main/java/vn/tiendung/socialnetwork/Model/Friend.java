package vn.tiendung.socialnetwork.Model;

/*Tạo tạm một class để chứa các thông tin về bạn bè*/
public class Friend {
    private String name;
    private int mutualFriends;
    private int avatarResId;

    public Friend(String name, int mutualFriends, int avatarResId) {
        this.name = name;
        this.mutualFriends = mutualFriends;
        this.avatarResId = avatarResId;
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
