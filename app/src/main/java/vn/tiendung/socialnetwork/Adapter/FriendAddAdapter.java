package vn.tiendung.socialnetwork.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.Model.Friend;
import vn.tiendung.socialnetwork.R;

public class FriendAddAdapter extends RecyclerView.Adapter<FriendAddAdapter.FriendViewHolder> {
    private List<Friend> friendList;
    private Context context;

    private APIService apiService;
    private String userIdMe;

    private Fragment fragment;

    public FriendAddAdapter(Context context, List<Friend> friendList, APIService apiService, String userId,Fragment fragment) {
        this.context = context;
        this.friendList = friendList;
        this.apiService = apiService;
        this.userIdMe = userId;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_add_item, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Friend friend = friendList.get(position);

        // Bind dữ liệu
        holder.tvUserName.setText(friend.getName());
        holder.tvMutualFriends.setText(friend.getMutualFriends() + " bạn chung");

        // Load avatar bằng Glide
        Glide.with(context)
                .load(friend.getAvatarResId())
                .placeholder(R.drawable.avt_default)
                .into(holder.imageProfile);

        // Xử lý loại (Request/Suggest)
        if (friend.getType() == Friend.TYPE_REQUEST) {
            holder.btnAccept.setVisibility(View.VISIBLE);
            holder.btnRemove.setVisibility(View.VISIBLE);
            holder.btnAddFriend.setVisibility(View.GONE);
        } else {
            //holder.btnAccept.setVisibility(View.GONE);
            //holder.btnAddFriend.setVisibility(View.VISIBLE);

            if (friend.isRequestSent()) {
                holder.btnAddFriend.setVisibility(View.GONE);
                holder.tVDagui.setVisibility(View.VISIBLE);
                holder.btnRecall.setVisibility(View.VISIBLE);
            } else {
                holder.btnAccept.setVisibility(View.GONE);
                holder.btnAddFriend.setVisibility(View.VISIBLE);
                holder.tVDagui.setVisibility(View.GONE);
                holder.btnRecall.setVisibility(View.GONE);
            }
        }
        String userId = friend.getUserId();
        // Xử lý nút chấp nhận
        holder.btnAccept.setOnClickListener(v -> {
            apiService.acceptFriend(userId,userIdMe).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Đã chấp nhận lời mời", Toast.LENGTH_SHORT).show();
                        friendList.remove(position);
                        notifyItemRemoved(position);

                        Bundle result = new Bundle();
                        result.putBoolean("refresh_friend_list", true);
                        fragment.getParentFragmentManager().setFragmentResult("refresh_friend_list_key", result);
                    } else {
                        Toast.makeText(context, "Lỗi khi chấp nhận", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Xử lý nút từ chối
        holder.btnRemove.setOnClickListener(v -> {
            apiService.rejectFriend(userId,userIdMe).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Đã từ chối lời mời", Toast.LENGTH_SHORT).show();
                        friendList.remove(position);
                        notifyItemRemoved(position);
                    } else {
                        Toast.makeText(context, "Lỗi khi từ chối", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        });

        holder.btnAddFriend.setOnClickListener(v -> {
            apiService.addFriend(userId, userIdMe).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        friend.setRequestSent(true);
                        notifyItemChanged(holder.getAdapterPosition());
                    } else {
                        Toast.makeText(context, "Không thể gửi lời mời", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        });

        holder.btnRecall.setOnClickListener(v -> {
            apiService.cancelFriendRequest(userId, userIdMe).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        friend.setRequestSent(false);
                        notifyItemChanged(holder.getAdapterPosition());
                    } else {
                        Toast.makeText(context, "Không thể gỡ lời mời", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProfile;
        TextView tvUserName, tvMutualFriends, tVDagui;
        Button btnAccept, btnAddFriend, btnRemove, btnRecall;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.imageProfile);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvMutualFriends = itemView.findViewById(R.id.tvMutualFriends);
            tVDagui = itemView.findViewById(R.id.tVDagui);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnAddFriend = itemView.findViewById(R.id.btnAddFriend);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            btnRecall = itemView.findViewById(R.id.btnRecall);
        }
    }
}

