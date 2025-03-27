package vn.tiendung.socialnetwork.UI;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.tiendung.socialnetwork.Adapter.MessageAdapter;
import vn.tiendung.socialnetwork.Model.Message;
import vn.tiendung.socialnetwork.R;

public class MessageActivity extends AppCompatActivity {
    private RecyclerView recyclerMessages;
    private EditText editMessage;
    private ImageView btnSend;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // Ánh xạ view
        recyclerMessages = findViewById(R.id.recyclerMessages);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);

        // Thiết lập RecyclerView
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messageList);
        recyclerMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerMessages.setAdapter(messageAdapter);

        // Dữ liệu mẫu
        loadSampleMessages();

        // Xử lý gửi tin nhắn
        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void loadSampleMessages() {
        messageList.add(new Message("user1", "Hello!", null, "10:30 AM"));
        messageList.add(new Message("user2", "Hi, how are you?", null, "10:31 AM"));
        messageList.add(new Message("user1", null, "https://example.com/image1.jpg", "10:32 AM"));
        messageList.add(new Message("user2", "Nice picture!", null, "10:33 AM"));
        messageList.add(new Message("user2", null, "https://example.com/image2.jpg", "10:34 AM"));

        messageAdapter.notifyDataSetChanged();
    }

    private void sendMessage() {
        String messageText = editMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
            messageList.add(new Message("user1", messageText, null, "10:35 AM"));
            messageAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerMessages.scrollToPosition(messageList.size() - 1);
            editMessage.setText("");
        }
    }
}
