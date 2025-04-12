package vn.tiendung.socialnetwork.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import vn.tiendung.socialnetwork.Model.Message;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.Utils.SharedPrefManager;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private static final String TAG = "MessageAdapter";

    private Context context;
    private List<Message> messages;
    private String currentUserId;

    public MessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
        this.currentUserId = SharedPrefManager.getInstance(context).getUserId();
    }

    public void addMessage(Message message) {
        if (message != null) {
            messages.add(message);
            notifyItemInserted(messages.size() - 1);
            Log.d(TAG, "Message added: " + message.getContent());
        }
    }

    public void addMessages(List<Message> messages) {
        if (messages != null && !messages.isEmpty()) {
            int startPosition = this.messages.size();
            this.messages.addAll(messages);
            notifyItemRangeInserted(startPosition, messages.size());
            Log.d(TAG, "Added " + messages.size() + " messages");
        }
    }

    public void updateMessage(Message message) {
        if (message != null) {
            for (int i = 0; i < messages.size(); i++) {
                if (messages.get(i).getId().equals(message.getId())) {
                    messages.set(i, message);
                    notifyItemChanged(i);
                    Log.d(TAG, "Message updated: " + message.getContent());
                    break;
                }
            }
        }
    }

    public void clearMessages() {
        messages.clear();
        notifyDataSetChanged();
        Log.d(TAG, "Messages cleared");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        if (holder.getItemViewType() == VIEW_TYPE_SENT) {
            configureSentMessageHolder((SentMessageHolder) holder, message);
        } else {
            configureReceivedMessageHolder((ReceivedMessageHolder) holder, message);
        }

        // Thiết lập sự kiện nhấn vào tin nhắn để hiển thị/ẩn timestamp với hiệu ứng mượt mà
        holder.itemView.setOnClickListener(v -> {
            if (holder instanceof SentMessageHolder) {
                SentMessageHolder sentHolder = (SentMessageHolder) holder;
                if (sentHolder.tvTimestamp.getVisibility() == View.GONE) {
                    // Đặt visibility thành VISIBLE ngay lập tức và áp dụng hiệu ứng alpha
                    sentHolder.tvTimestamp.setAlpha(0f);
                    sentHolder.tvTimestamp.setVisibility(View.VISIBLE);
                    sentHolder.tvTimestamp.animate().alpha(1f).setDuration(1000).start();  // Hiển thị mượt mà
                } else {
                    // Áp dụng hiệu ứng ẩn alpha trước, sau đó đổi visibility sau khi xong
                    sentHolder.tvTimestamp.animate().alpha(0f).setDuration(1000).withEndAction(() -> {
                        sentHolder.tvTimestamp.setVisibility(View.GONE); // Đặt visibility thành GONE sau khi mờ
                    }).start();
                }
            } else if (holder instanceof ReceivedMessageHolder) {
                ReceivedMessageHolder receivedHolder = (ReceivedMessageHolder) holder;
                if (receivedHolder.tvTimestamp.getVisibility() == View.GONE) {
                    // Đặt visibility thành VISIBLE ngay lập tức và áp dụng hiệu ứng alpha
                    receivedHolder.tvTimestamp.setAlpha(0f);
                    receivedHolder.tvTimestamp.setVisibility(View.VISIBLE);
                    receivedHolder.tvTimestamp.animate().alpha(1f).setDuration(1000).start();
                } else {
                    // Áp dụng hiệu ứng ẩn alpha trước, sau đó đổi visibility sau khi xong
                    receivedHolder.tvTimestamp.animate().alpha(0f).setDuration(1000).withEndAction(() -> {
                        receivedHolder.tvTimestamp.setVisibility(View.GONE); // Đặt visibility thành GONE sau khi mờ
                    }).start();
                }
            }
        });
    }

    private void configureSentMessageHolder(SentMessageHolder holder, Message message) {
        if (message.getMessageType().equals("text")) {
            holder.imgMessage.setVisibility(View.GONE);
            holder.tvMessage.setVisibility(View.VISIBLE);
            holder.tvMessage.setText(message.getContent());
        } else {
            holder.imgMessage.setVisibility(View.VISIBLE);
            holder.tvMessage.setVisibility(View.GONE);
            Glide.with(context)
                    .load(message.getContent())
                    .into(holder.imgMessage);
        }
        holder.tvTimestamp.setText(message.getTimestamp());
    }

    private void configureReceivedMessageHolder(ReceivedMessageHolder holder, Message message) {
        if (message.getMessageType().equals("text")) {
            holder.imgMessage.setVisibility(View.GONE);
            holder.tvMessage.setVisibility(View.VISIBLE);
            holder.tvMessage.setText(message.getContent());
        } else {
            holder.imgMessage.setVisibility(View.VISIBLE);
            holder.tvMessage.setVisibility(View.GONE);
            Glide.with(context)
                    .load(message.getContent())
                    .into(holder.imgMessage);
        }
        holder.tvTimestamp.setText(message.getTimestamp());

        if (message.getSender() != null) {
            String avatarUrl = message.getSender().getAvatar();
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                holder.imgAvatar.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(avatarUrl)
                        .placeholder(R.drawable.avt_default)
                        .error(R.drawable.avt_default)
                        .circleCrop()
                        .into(holder.imgAvatar);
            } else {
                holder.imgAvatar.setVisibility(View.VISIBLE);
                holder.imgAvatar.setImageResource(R.drawable.avt_default);
            }
        } else {
            holder.tvSenderName.setVisibility(View.GONE);
            holder.imgAvatar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getSender() == null) return VIEW_TYPE_SENT;
        return message.getSender().getId().equals(currentUserId) ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    static class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTimestamp;
        ImageView imgMessage;

        SentMessageHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessageSent);
            tvTimestamp = itemView.findViewById(R.id.tvTimestampSent);
            imgMessage = itemView.findViewById(R.id.imgMessageSent);
        }
    }

    static class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTimestamp, tvSenderName;
        ImageView imgMessage, imgAvatar;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessageReceived);
            tvTimestamp = itemView.findViewById(R.id.tvTimestampReceived);
            imgMessage = itemView.findViewById(R.id.imgMessageReceived);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
            imgAvatar = itemView.findViewById(R.id.imgSenderAvatar);
        }
    }
}
