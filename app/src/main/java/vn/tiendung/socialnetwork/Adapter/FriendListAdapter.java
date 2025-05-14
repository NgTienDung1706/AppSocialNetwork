package vn.tiendung.socialnetwork.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.Model.Friend;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.UI.MessageActivity;
import vn.tiendung.socialnetwork.UI.ProfileActivity;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendViewHolder> {

    private Context context;
    private List<Friend> friends;

    private APIService apiService;
    private String userIdMe;

    public FriendListAdapter(Context context, List<Friend> friends,APIService apiService, String userId) {
        this.context = context;
        this.friends = friends;
        this.apiService = apiService;
        this.userIdMe = userId;
    }
    public FriendListAdapter(Context context, List<Friend> friends) {
        this.context = context;
        this.friends = friends;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_item, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friends.get(position);

        //holder.avatar.setImageResource(friend.getAvatarResId());
        holder.name.setText(friend.getName());
        holder.mutualFriends.setText(friend.getMutualFriends() + " bạn chung");
        Glide.with(context).load(friend.getAvatarResId()).into(holder.avatar);
        if (friend.isFriend()) {
            holder.tvIsFriend.setVisibility(View.VISIBLE); // Hiển thị "Friend"
            holder.messageIcon.setVisibility(View.VISIBLE);
        } else {
            holder.tvIsFriend.setVisibility(View.GONE); // Ẩn "Friend"
            holder.messageIcon.setVisibility(View.GONE);
        }
        String userId = friend.getUserId();
        holder.messageIcon.setOnClickListener(v -> {
            apiService.createOrGetPrivateConversation(userId,userIdMe)
                    .enqueue(new Callback<Map<String, String>>() {
                        @Override
                        public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                String conversationId = response.body().get("conversationId");

                                Intent intent = new Intent(context, MessageActivity.class);
//                                intent.putExtra("conversationId", conversationId);
//                                intent.putExtra("receiverId", friend.getUserId());
//                                context.startActivity(intent);

                                intent.putExtra("conversation_id", conversationId);
                                intent.putExtra("user_id", friend.getUserId());
                                intent.putExtra("fullname", friend.getName());
                                intent.putExtra("avatar", friend.getAvatarResId());

                                context.startActivity(intent);
                            } else {
                                Toast.makeText(context, "Không thể tạo cuộc trò chuyện", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, String>> call, Throwable t) {
                            Toast.makeText(context, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
                        }
                    });

        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("userId", friend.getUserId()); // Truyền userId sang ProfileActivity
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar, messageIcon;
        TextView name, mutualFriends,tvIsFriend;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.imgAvatar);
            name = itemView.findViewById(R.id.tvName);
            mutualFriends = itemView.findViewById(R.id.tvMutualFriends);
            messageIcon = itemView.findViewById(R.id.imgMessage);
            tvIsFriend = itemView.findViewById((R.id.tvIsFriend));
        }
    }

    public void updateData(List<Friend> newFriends) {
        this.friends.clear();
        this.friends.addAll(newFriends);
        notifyDataSetChanged();
    }
}
