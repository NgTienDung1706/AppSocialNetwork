package vn.tiendung.socialnetwork.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
