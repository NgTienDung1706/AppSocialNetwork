package vn.tiendung.socialnetwork.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.tiendung.socialnetwork.Model.Message;
import vn.tiendung.socialnetwork.R;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_SEND = 1;
    private static final int TYPE_RECEIVE = 2;

    private Context context;
    private List<Message> messageList;

    public MessageAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getSenderId().equals("user1") ? TYPE_SEND : TYPE_RECEIVE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SEND) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_send, parent, false);
            return new SendViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_received, parent, false);
            return new ReceiveViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (holder instanceof SendViewHolder) {
            SendViewHolder sendHolder = (SendViewHolder) holder;
            if (message.getContent() != null) {
                sendHolder.tvMessageSent.setText(message.getContent());
                sendHolder.tvMessageSent.setVisibility(View.VISIBLE);
                sendHolder.imgMessageReceived.setVisibility(View.GONE);
            } else if (message.getImageUrl() != null) {
                Glide.with(context).load(message.getImageUrl()).into(sendHolder.imgMessageReceived);
                sendHolder.imgMessageReceived.setVisibility(View.VISIBLE);
                sendHolder.tvMessageSent.setVisibility(View.GONE);
            }
            sendHolder.tvTimestampSent.setText(message.getTimestamp());

        } else if (holder instanceof ReceiveViewHolder) {
            ReceiveViewHolder receiveHolder = (ReceiveViewHolder) holder;
            if (message.getContent() != null) {
                receiveHolder.tvMessageReceived.setText(message.getContent());
                receiveHolder.tvMessageReceived.setVisibility(View.VISIBLE);
                receiveHolder.imgMessageReceived.setVisibility(View.GONE);
            } else if (message.getImageUrl() != null) {
                Glide.with(context).load(message.getImageUrl()).into(receiveHolder.imgMessageReceived);
                receiveHolder.imgMessageReceived.setVisibility(View.VISIBLE);
                receiveHolder.tvMessageReceived.setVisibility(View.GONE);
            }
            receiveHolder.tvTimestampReceived.setText(message.getTimestamp());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class SendViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessageSent, tvTimestampSent;
        ImageView imgMessageReceived;

        public SendViewHolder(View itemView) {
            super(itemView);
            tvMessageSent = itemView.findViewById(R.id.tvMessageSent);
            tvTimestampSent = itemView.findViewById(R.id.tvTimestampSent);
            imgMessageReceived = itemView.findViewById(R.id.imgMessageReceived);
        }
    }

    public static class ReceiveViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessageReceived, tvTimestampReceived;
        ImageView imgMessageReceived;

        public ReceiveViewHolder(View itemView) {
            super(itemView);
            tvMessageReceived = itemView.findViewById(R.id.tvMessageReceived);
            tvTimestampReceived = itemView.findViewById(R.id.tvTimestampReceived);
            imgMessageReceived = itemView.findViewById(R.id.imgMessageReceived);
        }
    }
}
