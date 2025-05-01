package vn.tiendung.socialnetwork.Model;

import java.util.List;
import java.util.Map;

import vn.tiendung.socialnetwork.R;

public class Post {
    private String _id;
    private Content content;
    private User userid;
    private String createdAt;
    private boolean isStory;
    private String location;
    private Map<String, List<String>> reactions; // like, love, haha...

    private String myReaction; // "Thích", "Haha", "Buồn", v.v...

    public String getMyReaction() {
        return myReaction;
    }

    public void setMyReaction(String myReaction) {
        this.myReaction = myReaction;
    }
    public String getId() { return _id; }
    public Content getContent() { return content; }
    public User getUser() { return userid; }
    public String getCreatedAt() { return createdAt; }
    public boolean isStory() { return isStory; }
    public String getLocation() { return location; }
    public Map<String, List<String>> getReactions() { return reactions; }

    public int getMyReactionsIcon()
    {
        if (myReaction.equals("Thích")) {
            return R.drawable.ic_reaction_like;
        }
        else if (myReaction.equals("Haha")) {
            return R.drawable.ic_reaction_haha;
        }
        else if (myReaction.equals("Buồn")) {
            return R.drawable.ic_reaction_sad;
        }
        else if (myReaction.equals("Thương")) {
            return R.drawable.ic_reaction_love;
        }
        else if (myReaction.equals("Wow")) {
            return R.drawable.ic_reaction_wow;
        }
        else if (myReaction.equals("Giận")) {
            return R.drawable.ic_reaction_angry;
        }
        else if (myReaction.equals("Tim")) {
            return R.drawable.ic_reaction_heart;
        }
        return R.drawable.ic_like;
    }

    public int getTotalReactionsCount() {
        int totalReactions = 0;

        // Kiểm tra nếu reactions không phải là null
        if (getReactions() != null) {
            // Duyệt qua từng loại cảm xúc trong map reactions
            for (List<String> reactionList : getReactions().values()) {
                // Cộng tổng số lượng cảm xúc (số lượng người thả cảm xúc)
                totalReactions += reactionList.size();
            }
        }

        return totalReactions;
    }


    public static class Content {
        private String caption;
        private List<String> hashtags;
        private List<String> pictures;

        public String getCaption() { return caption; }
        public List<String> getHashtags() { return hashtags; }
        public List<String> getPictures() { return pictures; }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public void setHashtags(List<String> hashtags) {
            this.hashtags = hashtags;
        }

        public void setPictures(List<String> pictures) {
            this.pictures = pictures;
        }
    }

    public static class User {
        private String _id;
        private String name;
        private String avatar;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }

    public Post() {
    }
}

