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

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import vn.tiendung.socialnetwork.Model.Chat;
import vn.tiendung.socialnetwork.Model.ChatItem;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.UI.MessageActivity;
import vn.tiendung.socialnetwork.Utils.SharedPrefManager;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private Context context;
    private List<ChatItem> chatList;

    private List<ChatItem> originalChatList; // Lưu danh sách gốc

    public ChatListAdapter(Context context, List<ChatItem> chatList) {
        this.context = context;
        this.chatList = chatList;
        this.originalChatList = new ArrayList<>(chatList); // ✅ Đảm bảo có dữ liệu gốc
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_list_item, parent, false);
        return new ViewHolder(view);  // 🔹 Đảm bảo class ViewHolder được khai báo bên dưới
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String userId = SharedPrefManager.getInstance(context).getUserId();
        ChatItem chat = chatList.get(position);
        holder.txtName.setText(chat.getUser().getFullname());

        // Kiểm tra loại tin nhắn gần nhất
        String lastMessage = chat.getLastMessage().getContent();
        String messageType = chat.getLastMessage().getMessage_type(); // Kiểm tra loại tin nhắn

        if (messageType.equals("image")) {
            // Nếu là ảnh, hiển thị "Bạn đã gửi một ảnh" nếu là tin nhắn của người dùng hiện tại
            if (chat.getLastMessage().getSender_id().equals(userId)) {
                lastMessage = "Bạn đã gửi một ảnh";
            } else {
                lastMessage = "Đã gửi một ảnh";
            }
        } else {
            // Nếu là tin nhắn văn bản, hiển thị "Bạn: [nội dung]" nếu là tin nhắn của người dùng hiện tại
            if (chat.getLastMessage().getSender_id().equals(userId)) {
                lastMessage = "Bạn: " + lastMessage;
            }
        }

        // Cập nhật nội dung tin nhắn
        holder.txtLastMessage.setText(lastMessage);
        holder.txtTime.setText(formatTime(chat.getLastMessage().getTimestamp()));

        // Hiển thị avatar bằng Glide
        Glide.with(context)
                .load(chat.getUser().getAvatar())
                .placeholder(R.drawable.avt_default)
                .into(holder.imgAvatar);

        // Hiển thị trạng thái online
        if (chat.getUser().isOnline()) {
            holder.viewOnlineStatus.setVisibility(View.VISIBLE);
        } else {
            holder.viewOnlineStatus.setVisibility(View.GONE);
        }

        // Hiển thị số tin nhắn chưa đọc
        if (chat.getUnreadMessages() > 0) {
            holder.txtUnreadCount.setText(String.valueOf(chat.getUnreadMessages()));
            holder.txtUnreadCount.setVisibility(View.VISIBLE);
        } else {
            holder.txtUnreadCount.setVisibility(View.GONE);
        }

        // Bắt sự kiện click mở chat
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("conversationId", chat.getConversationId());
            intent.putExtra("userId", chat.getUser().getId());
            intent.putExtra("userName", chat.getUser().getFullname());
            intent.putExtra("userAvatar", chat.getUser().getAvatar());
            context.startActivity(intent);
        });
    }
    private String formatTime(String timestamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf.parse(timestamp);

            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    @Override
    public int getItemCount() {
        return chatList.size();
    }

    // 🔹 Định nghĩa ViewHolder ngay bên trong Adapter
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtName, txtLastMessage, txtTime, txtUnreadCount;
        View viewOnlineStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtName = itemView.findViewById(R.id.txtName);
            txtLastMessage = itemView.findViewById(R.id.txtLastMessage);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtUnreadCount = itemView.findViewById(R.id.txtUnreadCount);
            viewOnlineStatus = itemView.findViewById(R.id.onlineStatus);
        }
    }
    public void filter(String text) {
        if (originalChatList == null || chatList == null) {
            return;
        }

        List<ChatItem> filteredList = new ArrayList<>();
        if (text.isEmpty()) {
            filteredList.addAll(originalChatList);
        } else {
            for (ChatItem chat : originalChatList) {
                if (chat.getUser().getFullname().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(chat);
                }
            }
        }

        chatList.clear();
        chatList.addAll(filteredList);
        notifyDataSetChanged();
    }


}
