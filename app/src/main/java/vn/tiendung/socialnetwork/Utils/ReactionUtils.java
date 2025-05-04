package vn.tiendung.socialnetwork.Utils;

import vn.tiendung.socialnetwork.R;

public class ReactionUtils {
    public static int getEmojiResId(String reaction) {
        if (reaction == null) return R.drawable.ic_comment_outline_heart;
        switch (reaction) {
            case "Thích": return R.drawable.ic_reaction_like;
            case "Thương": return R.drawable.ic_reaction_love;
            case "Haha": return R.drawable.ic_reaction_haha;
            case "Tim": return R.drawable.ic_reaction_heart;
            case "Wow": return R.drawable.ic_reaction_wow;
            case "Buồn": return R.drawable.ic_reaction_sad;
            case "Giận": return R.drawable.ic_reaction_angry;
            default: return R.drawable.ic_comment_outline_heart;
        }
    }

}
