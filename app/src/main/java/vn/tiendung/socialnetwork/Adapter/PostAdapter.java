package vn.tiendung.socialnetwork.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.tiendung.socialnetwork.Fragment.ReactionPopupWindow;
import vn.tiendung.socialnetwork.Model.Post;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.UI.PostDetailActivity;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<Post> posts;
    private Context context;

    private TextView tvReaction;
    private ImageButton btnReaction;
    private ReactionPopupWindow reactionPopup;

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);

        tvReaction = view.findViewById(R.id.tvReaction);
        btnReaction = view.findViewById(R.id.btnReaction);

        reactionPopup = new ReactionPopupWindow(view.getContext(), reaction -> {
            tvReaction.setText(reaction);
        });
        btnReaction.setOnLongClickListener(v -> {
            reactionPopup.show(btnReaction);
            return true;
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.avatar.setImageResource(post.getAvatar());
        holder.name.setText(post.getName());
        holder.content.setText(post.getContent());

        // Tạo popup riêng cho mỗi item
        ReactionPopupWindow popup = new ReactionPopupWindow(holder.itemView.getContext(), reaction -> {
            holder.tvReaction.setText(reaction);  // Cập nhật TextView phản hồi
            holder.btnReaction.setImageResource(getReactionIcon(reaction));  //  thay icon
        });

        // Gắn sự kiện nhấn giữ để mở reaction bar
        holder.btnReaction.setOnLongClickListener(v -> {
            popup.show(holder.btnReaction);
            return true;
        });
        // Nếu chỉ ấn thì là thích
        holder.btnReaction.setOnClickListener(v -> {
            holder.tvReaction.setText("Thích");
            holder.btnReaction.setImageResource(getReactionIcon("Thích"));

        });
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
        TextView name, content, tvReaction;
        ImageButton btnReaction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.imageAvatar);
            name = itemView.findViewById(R.id.textName);
            content = itemView.findViewById(R.id.textContent);
            tvReaction = itemView.findViewById(R.id.tvReaction);
            btnReaction = itemView.findViewById(R.id.btnReaction);
        }
    }

    // chuyển reaction thành icon tương ứng
    private int getReactionIcon(String reaction) {
        switch (reaction) {
            case "Thích": return R.drawable.ic_reaction_like;
            case "Thương": return R.drawable.ic_reaction_love;
            case "Haha": return R.drawable.ic_reaction_haha;
            case "Tim": return R.drawable.ic_reaction_heart;
            case "Wow": return R.drawable.ic_reaction_wow;
            case "Buồn": return R.drawable.ic_reaction_sad;
            case "Giận": return R.drawable.ic_reaction_angry;
            default: return R.drawable.ic_reaction_like;
        }
    }
}
