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

import java.util.ArrayList;
import java.util.List;

import vn.tiendung.socialnetwork.Model.Chat;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.UI.MessageActivity;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private Context context;
    private List<Chat> chatList;

    private List<Chat> originalChatList; // Lưu danh sách gốc

    public ChatListAdapter(Context context, List<Chat> chatList) {
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
        Chat chat = chatList.get(position);
        holder.txtUserName.setText(chat.getUserName());
        holder.txtLastMessage.setText(chat.getLastMessage());
        holder.txtTime.setText(chat.getLastMessageTime());

        if (chat.getUnreadCount() > 0) {
            holder.txtUnreadCount.setText(String.valueOf(chat.getUnreadCount()));
            holder.txtUnreadCount.setVisibility(View.VISIBLE);
        } else {
            holder.txtUnreadCount.setVisibility(View.GONE);
        }
        //holder.textView.setText(chatList.get(position));

        // Bắt sự kiện khi click vào một người
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, MessageActivity.class);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    // 🔹 Định nghĩa ViewHolder ngay bên trong Adapter
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtUserName, txtLastMessage, txtTime, txtUnreadCount;
        View viewOnlineStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtUserName = itemView.findViewById(R.id.txtUserName);
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

        List<Chat> filteredList = new ArrayList<>();
        if (text.isEmpty()) {
            filteredList.addAll(originalChatList);
        } else {
            for (Chat chat : originalChatList) {
                if (chat.getUserName().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(chat);
                }
            }
        }

        chatList.clear();
        chatList.addAll(filteredList);
        notifyDataSetChanged();
    }


}
