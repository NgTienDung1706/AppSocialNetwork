package vn.tiendung.socialnetwork.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.tiendung.socialnetwork.Model.Friend;
import vn.tiendung.socialnetwork.R;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendViewHolder> {

    private Context context;
    private List<Friend> friends;

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

        holder.avatar.setImageResource(friend.getAvatarResId());
        holder.name.setText(friend.getName());
        holder.mutualFriends.setText(friend.getMutualFriends() + " bạn chung");

        holder.messageIcon.setOnClickListener(v -> {
            // Xử lý sự kiện nhắn tin
            Toast.makeText(context, "Nhắn tin đê", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar, messageIcon;
        TextView name, mutualFriends;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.imgAvatar);
            name = itemView.findViewById(R.id.tvName);
            mutualFriends = itemView.findViewById(R.id.tvMutualFriends);
            messageIcon = itemView.findViewById(R.id.imgMessage);
        }
    }
}
