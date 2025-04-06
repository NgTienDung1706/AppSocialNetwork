package vn.tiendung.socialnetwork.Fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

import vn.tiendung.socialnetwork.R;

public class ReactionPopupWindow {

    public interface ReactionListener {
        void onReactionSelected(String reactionName);
    }

    private final PopupWindow popupWindow;
    private final String[] emojiNames = {"Like", "Love", "Haha", "Wow", "Sad", "Angry"};
    private final int[] emojiIds = {
            R.id.emoji_like, R.id.emoji_love, R.id.emoji_haha,
            R.id.emoji_heart, R.id.emoji_sad, R.id.emoji_angry
    };

    private final List<ImageView> emojiViews = new ArrayList<>();

    public ReactionPopupWindow(Context context, ReactionListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.reaction_popup, null);
        popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        for (int i = 0; i < emojiIds.length; i++) {
            final int index = i;
            ImageView emoji = view.findViewById(emojiIds[i]);
            emojiViews.add(emoji);

            emoji.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        scaleOnlyThis(emoji); // phóng cái này, thu nhỏ cái khác
                        return true;

                    case MotionEvent.ACTION_UP:
                        resetAllEmojiSize();
                        popupWindow.dismiss();
                        listener.onReactionSelected(emojiNames[index]);
                        return true;

                    case MotionEvent.ACTION_CANCEL:
                        resetAllEmojiSize();
                        return true;
                }
                return false;
            });
        }
    }

    private void scaleOnlyThis(ImageView selected) {
        for (ImageView emoji : emojiViews) {
            if (emoji == selected) {
                emoji.animate().scaleX(1.5f).scaleY(1.5f).setDuration(150).start();
            } else {
                emoji.animate().scaleX(0.8f).scaleY(0.8f).setDuration(150).start();
            }
        }
    }

    private void resetAllEmojiSize() {
        for (ImageView emoji : emojiViews) {
            emoji.animate().scaleX(1f).scaleY(1f).setDuration(150).start();
        }
    }

    public void show(View anchor) {
        popupWindow.showAsDropDown(anchor, 0, -anchor.getHeight() - 200);
    }
}


