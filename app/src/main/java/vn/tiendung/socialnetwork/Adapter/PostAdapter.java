package vn.tiendung.socialnetwork.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.util.Log;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.RetrofitClient;
import vn.tiendung.socialnetwork.Fragment.ReactionPopupWindow;
import vn.tiendung.socialnetwork.Model.Friend;
import vn.tiendung.socialnetwork.Model.Post;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.UI.PostDetailActivity;
import vn.tiendung.socialnetwork.Utils.SharedPrefManager;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<Post> posts;
    private Context context;

    APIService apiService;

    String userId;
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
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        userId = SharedPrefManager.getInstance(context).getUserId();

        // Load avatar
        Glide.with(context)
                .load(post.getUser().getAvatar())
                .placeholder(R.drawable.circleusersolid) // Avatar mặc định
                .error(R.drawable.circleusersolid)       // Avatar khi lỗi
                .circleCrop() // Biến ảnh thành hình tròn
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

            Map<String, String> body = new HashMap<>();
            body.put("userId", userId);
            body.put("reaction", reaction);

            apiService.addOrUpdateReaction(post.getId(), body).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        holder.tvReaction.setText(reaction);
                        holder.btnReaction.setImageResource(getReactionIcon(reaction));
                    } else {
                        Toast.makeText(context, "Cập nhật cảm xúc thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Nhấn giữ để mở reaction bar
        holder.btnReaction.setOnLongClickListener(v -> {
            popup.show(holder.btnReaction);
            return true;
        });

        // Nhấn thường là thích
        holder.btnReaction.setOnClickListener(v -> {
            // Kiểm tra icon hiện tại đang hiển thị
            int currentIcon = ((ImageButton) v).getDrawable() != null
                    ? ((ImageButton) v).getDrawable().getConstantState().hashCode()
                    : -1;

            // Lấy icon mặc định
            int defaultIcon = context.getResources().getDrawable(R.drawable.ic_like).getConstantState().hashCode();

            // Nếu icon hiện tại khác icon mặc định (không phải "Thích"), đặt lại icon về mặc định
            if (currentIcon != defaultIcon) {
                apiService.deleteReaction(post.getId(), userId).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            post.setMyReaction(null);
                            holder.tvReaction.setText("Thích");
                            holder.btnReaction.setImageResource(getReactionIcon("default"));
                        } else {
                            Toast.makeText(context, "Xoá cảm xúc thất bại", Toast.LENGTH_SHORT).show();
                            Log.e("DELETE_REACTION", "Code: " + response.code());

                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Map<String, String> body = new HashMap<>();
                body.put("userId", userId);
                body.put("reaction", "like");

                apiService.addOrUpdateReaction(post.getId(), body).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            //post.setMyReaction("Thích");
                            holder.tvReaction.setText("Thích");
                            holder.btnReaction.setImageResource(getReactionIcon("Thích"));
                        } else {
                            Toast.makeText(context, "Cập nhật cảm xúc thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(context, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        // Click ảnh -> mở chi tiết bài viết
        holder.picture.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("avatar", post.getUser().getAvatar());
            intent.putExtra("name", post.getUser().getName());
            intent.putExtra("content", post.getContent().getCaption());
            intent.putExtra("postId", post.getId());
            context.startActivity(intent);
        });

        String currentReaction = post.getMyReaction();
        if (currentReaction != null) {
            holder.tvReaction.setText(currentReaction);
            holder.btnReaction.setImageResource(getReactionIcon(currentReaction));
        } else {
            holder.tvReaction.setText("Thích");
            holder.btnReaction.setImageResource(getReactionIcon("default"));
        }
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
            default: return R.drawable.ic_like;
        }
    }
    public void updateData(List<Post> newPosts) {
        this.posts.clear();
        this.posts.addAll(newPosts);
        notifyDataSetChanged();
    }
}
