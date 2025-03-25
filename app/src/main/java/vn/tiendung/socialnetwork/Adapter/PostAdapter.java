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

        // Lắng nghe sự kiện nhấn giữ
        reactionLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Hiển thị PopupWindow
                        popupWindow.showAsDropDown(reactionLayout, 0, -200);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        // Xác định cảm xúc dựa trên vị trí tay người dùng
                        int x = (int) event.getRawX();
                        int y = (int) event.getRawY();

                        View hoveredView = findHoveredView(popupView, x, y);
                        if (hoveredView != null) {
                            hoveredView.setScaleX(1.5f);
                            hoveredView.setScaleY(1.5f);
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        // Xử lý cảm xúc được chọn
                        int selectedEmotion = getSelectedEmotion(event, popupView);
                        handleEmotion(selectedEmotion);

                        popupWindow.dismiss();
                        return true;
                }
                return false;
            }
            // Hàm xác định cảm xúc được chọn
            private int getSelectedEmotion(MotionEvent event, View popupView) {
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                View hoveredView = findHoveredView(popupView, x, y);
                if (hoveredView != null) {
                    // Switch case không được nên mới phải dùng như này
                    if (hoveredView.getId() == R.id.img_like) {
                        return R.drawable.ic_reaction_like;
                    } else if (hoveredView.getId() == R.id.img_love) {
                        return R.drawable.ic_reaction_love;
                    } else if (hoveredView.getId() == R.id.img_heart) {
                        return R.drawable.ic_reaction_heart;
                    } else if (hoveredView.getId() == R.id.img_haha) {
                        return R.drawable.ic_reaction_haha;
                    } else if (hoveredView.getId() == R.id.img_huhu) {
                        return R.drawable.ic_reaction_huhu;
                    } else if (hoveredView.getId() == R.id.img_angry) {
                        return R.drawable.ic_reaction_angry;
                    }
                }
                return 0; // Không chọn gì
            }

            // Hàm tìm View được hover
            private View findHoveredView(View parent, int x, int y) {
                int[] location = new int[2];
                parent.getLocationOnScreen(location);

                Rect rect = new Rect(location[0], location[1],
                        location[0] + parent.getWidth(),
                        location[1] + parent.getHeight());

                if (rect.contains(x, y)) {
                    return parent;
                }
                return null;
            }

            // Hàm xử lý cảm xúc
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
