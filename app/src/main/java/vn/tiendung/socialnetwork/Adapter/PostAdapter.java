package vn.tiendung.socialnetwork.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.tiendung.socialnetwork.Model.Post;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.UI.PostDetailActivity;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<Post> posts;
    private Context context;

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        // Khởi tạo reactionLayout
        LinearLayout reactionLayout = view.findViewById(R.id.reactionLayout);
        PopupWindow popupWindow = new PopupWindow(view);

        // Tạo View cho PopupWindow
        View popupView = LayoutInflater.from(view.getContext()).inflate(R.layout.emotion_popup, null);
        popupWindow.setContentView(popupView);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);

        // Danh sách các icon cảm xúc
        ImageView[] reactionIcons = {
                popupView.findViewById(R.id.img_like),
                popupView.findViewById(R.id.img_love),
                popupView.findViewById(R.id.img_heart),
                popupView.findViewById(R.id.img_haha),
                popupView.findViewById(R.id.img_huhu),
                popupView.findViewById(R.id.img_angry),
        };

        // Lắng nghe sự kiện nhấn giữ
        reactionLayout.setOnTouchListener(new View.OnTouchListener() {
            private boolean isPopupVisible = false;
            private boolean isDraggingDown = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Hiển thị PopupWindow
                        popupWindow.showAsDropDown(reactionLayout, 0, -350);
                        isPopupVisible = true;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        if (isPopupVisible) {
                            float deltaY = event.getRawY();
                            if (deltaY > 200) {
                                isDraggingDown = true;
                                popupWindow.dismiss();
                                isPopupVisible = false;
                            } else {
                                handleHover(event, popupView, reactionIcons);
                            }
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (isPopupVisible) {
                            if (!isDraggingDown) {
                                int selectedEmotion = getSelectedEmotion(event, popupView, reactionIcons);
                                handleEmotion(selectedEmotion);
                            }
                            popupWindow.dismiss();
                        }
                        return true;
                }
                return false;
            }

            private void handleHover(MotionEvent event, View parent, ImageView[] icons) {
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();

                // Tìm icon đang được hover
                ImageView hoveredIcon = null;
                for (ImageView icon : icons) {
                    int[] location = new int[2];
                    icon.getLocationOnScreen(location);
                    Rect rect = new Rect(location[0], location[1], location[0] + icon.getWidth(), location[1] + icon.getHeight());

                    if (rect.contains(x, y)) {
                        hoveredIcon = icon;
                        break;
                    }
                }

                // Xử lý phóng to và thu nhỏ
                for (ImageView icon : icons) {
                    if (icon == hoveredIcon) {
                        icon.setScaleX(1.5f);
                        icon.setScaleY(1.5f);
                    } else {
                        icon.setScaleX(1.0f);
                        icon.setScaleY(1.0f);
                    }
                }
            }

            private int getSelectedEmotion(MotionEvent event, View parent, ImageView[] icons) {
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();

                for (ImageView icon : icons) {
                    int[] location = new int[2];
                    icon.getLocationOnScreen(location);
                    Rect rect = new Rect(location[0], location[1], location[0] + icon.getWidth(), location[1] + icon.getHeight());

                    if (rect.contains(x, y)) {
                        return (int) icon.getTag(); // Tag chứa resourceId của cảm xúc
                    }
                }
                return 0; // Không chọn gì
            }

            private void handleEmotion(int emotionResId) {
                if (emotionResId != 0) {
                    Toast.makeText(view.getContext(), "Chọn cảm xúc: " + view.getResources().getResourceEntryName(emotionResId), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.avatar.setImageResource(post.getAvatar());
        holder.name.setText(post.getName());
        holder.content.setText(post.getContent());

        // Xử lý sự kiện click item
        holder.itemView.setOnClickListener(v -> {
            // Gọi Intent để mở PostDetailActivity
            Intent intent = new Intent(context, PostDetailActivity.class);

            // Truyền dữ liệu sang Activity
            intent.putExtra("avatar", post.getAvatar());
            intent.putExtra("name", post.getName());
            intent.putExtra("content", post.getContent());

            context.startActivity(intent); // Bắt đầu Activity
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView name, content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.imageAvatar);
            name = itemView.findViewById(R.id.textName);
            content = itemView.findViewById(R.id.textContent);
        }
    }
}
