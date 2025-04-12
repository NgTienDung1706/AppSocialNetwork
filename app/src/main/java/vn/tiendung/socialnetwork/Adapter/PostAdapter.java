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

import com.bumptech.glide.Glide;

import java.util.List;

import vn.tiendung.socialnetwork.Fragment.ReactionPopupWindow;
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
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        Post post = posts.get(position);

        // Load avatar
        Glide.with(context)
                .load(post.getUser().getAvatar())
                .placeholder(R.drawable.circleusersolid)
                .into(holder.avatar);

        holder.name.setText(post.getUser().getName());
        holder.time.setText(post.getCreatedAt());
        holder.content.setText(post.getContent().getCaption());

        // Load ảnh đầu tiên nếu có
        List<String> pictures = post.getContent().getPictures();
        if (pictures != null && !pictures.isEmpty()) {
            holder.picture.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(pictures.get(0))
                    .placeholder(R.drawable.ic_coin)
                    .into(holder.picture);
        } else {
            holder.picture.setVisibility(View.GONE);
        }

        // Tạo popup riêng cho mỗi item
        ReactionPopupWindow popup = new ReactionPopupWindow(holder.itemView.getContext(), reaction -> {
            holder.tvReaction.setText(reaction);
            holder.btnReaction.setImageResource(getReactionIcon(reaction));
        });

        // Nhấn giữ để mở reaction bar
        holder.btnReaction.setOnLongClickListener(v -> {
            popup.show(holder.btnReaction);
            return true;
        });

        // Nhấn thường là thích
        holder.btnReaction.setOnClickListener(v -> {
            holder.tvReaction.setText("Thích");
            holder.btnReaction.setImageResource(getReactionIcon("Thích"));
        });

        // Click ảnh -> mở chi tiết bài viết
        holder.picture.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("avatar", post.getUser().getAvatar());
            intent.putExtra("name", post.getUser().getName());
            intent.putExtra("content", post.getContent().getCaption());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar, picture;
        TextView name, time, content, tvReaction;
        ImageButton btnReaction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.imageAvatar);
            name = itemView.findViewById(R.id.textName);
            time = itemView.findViewById(R.id.textTime);
            content = itemView.findViewById(R.id.textContent);
            picture = itemView.findViewById(R.id.imgpicture);
            tvReaction = itemView.findViewById(R.id.tvReaction);
            btnReaction = itemView.findViewById(R.id.btnReaction);
        }
    }

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
