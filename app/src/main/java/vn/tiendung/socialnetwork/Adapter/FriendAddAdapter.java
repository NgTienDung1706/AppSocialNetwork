package vn.tiendung.socialnetwork.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.tiendung.socialnetwork.Model.Friend;
import vn.tiendung.socialnetwork.R;

public class FriendAddAdapter extends RecyclerView.Adapter<FriendAddAdapter.FriendViewHolder> {
    private List<Friend> friendList;
    private Context context;

    public FriendAddAdapter(Context context, List<Friend> friendList) {
        this.context = context;
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_add_item, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friendList.get(position);

        // Bind dữ liệu
        holder.tvUserName.setText(friend.getName());
        holder.tvMutualFriends.setText(friend.getMutualFriends() + " bạn chung");

        // Hiển thị ảnh profile tạm thời lấy trong local
        //holder.imageProfile.setImageResource(friend.getAvatarResId());
/*        Glide.with(context)
                .load(friend.getProfileImage())
                .centerCrop()
                .placeholder(R.drawable.circleusersolid)
                .into(holder.imageProfile);*/

        // Xử lý loại (Request/Suggest)
        if (friend.getType() == Friend.TYPE_REQUEST) {
            holder.btnAccept.setVisibility(View.VISIBLE);
            holder.btnAddFriend.setVisibility(View.GONE);
        } else {
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnAddFriend.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProfile;
        TextView tvUserName, tvMutualFriends;
        Button btnAccept, btnAddFriend, btnRemove;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.imageProfile);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvMutualFriends = itemView.findViewById(R.id.tvMutualFriends);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnAddFriend = itemView.findViewById(R.id.btnAddFriend);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}

