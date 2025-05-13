package vn.tiendung.socialnetwork.UI;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.RetrofitClient;
import vn.tiendung.socialnetwork.Model.Message;
import vn.tiendung.socialnetwork.Model.MessageResponse;
import vn.tiendung.socialnetwork.Model.UserProfile;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.Utils.MessageSocketManager;
import vn.tiendung.socialnetwork.Adapter.MessageAdapter;
import vn.tiendung.socialnetwork.Utils.SharedPrefManager;
import vn.tiendung.socialnetwork.Utils.SocketManager;

public class MessageActivity extends AppCompatActivity implements SocketManager.SocketEventListener {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "MessageActivity";

    private ImageView imgAvatar;
    private TextView txtName;
    private RecyclerView recyclerMessages;
    private EditText edtMessage;
    private ImageView btnSend;
    private ImageView btnAttach;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private APIService apiService;
    private String conversationId;
    private String recipientId;
    private String recipientName;
    private String recipientAvatar;
    private SocketManager socketManager;
    private Uri selectedImageUri;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        initializeViews();
        Intent intent = getIntent();
        if (intent == null) {
            Toast.makeText(this, "Lỗi: Không có dữ liệu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        conversationId = intent.getStringExtra("conversation_id");
        recipientId = intent.getStringExtra("user_id");
        recipientName = intent.getStringExtra("fullname");
        recipientAvatar = intent.getStringExtra("avatar");
        userId = SharedPrefManager.getInstance(this).getUserId();

        updateRecipientInfo();
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        initializeMessageList();
        setClickListeners();
        initializeSocket();

    }

    private void initializeViews() {
        try {
            imgAvatar = findViewById(R.id.avatar);
            txtName = findViewById(R.id.userName);
            recyclerMessages = findViewById(R.id.recyclerMessages);
            edtMessage = findViewById(R.id.editMessage);
            btnSend = findViewById(R.id.btnSend);
            btnAttach = findViewById(R.id.btnImage);

            ImageView btnBack = findViewById(R.id.btnBack);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
                Log.d(TAG, "Back button initialized successfully");
            } else {
                Log.e(TAG, "Back button view is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateRecipientInfo() {
        try {
            runOnUiThread(() -> {
                if (txtName != null) {
                    if (recipientName != null && !recipientName.isEmpty()) {
                        txtName.setText(recipientName);
                        Log.d(TAG, "Name set to: " + recipientName);
                    } else {
                        txtName.setText("Người dùng");
                        Log.d(TAG, "Name set to default");
                    }
                } else {
                    Log.e(TAG, "txtName view is null");
                }

                if (imgAvatar != null) {
                    if (recipientAvatar != null && !recipientAvatar.isEmpty()) {
                        Log.d(TAG, "Loading avatar from URL: " + recipientAvatar);
                        Glide.with(this)
                                .load(recipientAvatar)
                                .placeholder(R.drawable.avt_default)
                                .circleCrop()
                                .error(R.drawable.avt_default)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                            Target<Drawable> target, boolean isFirstResource) {
                                        Log.e(TAG, "Avatar load failed: " + (e != null ? e.getMessage() : "Unknown error"));
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model,
                                            Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        Log.d(TAG, "Avatar loaded successfully");
                                        return false;
                                    }
                                })
                                .into(imgAvatar);
                    } else {
                        imgAvatar.setImageResource(R.drawable.avt_default);
                        Log.d(TAG, "Avatar set to default");
                    }
                } else {
                    Log.e(TAG, "imgAvatar view is null");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error updating recipient info: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeMessageList() {
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerMessages.setLayoutManager(layoutManager);
        recyclerMessages.setAdapter(messageAdapter);
    }

    private void initializeSocket() {
        try {
            socketManager = SocketManager.getInstance();
            if (socketManager != null) {
                socketManager.setEventListener(this);
                socketManager.initialize(userId);
                
                if (!socketManager.isConnected()) {
                    Log.d(TAG, "Socket not connected, connecting...");
                    socketManager.connect();
                } else {
                    Log.d(TAG, "Socket already connected, joining room directly");
                    if (conversationId != null && !conversationId.isEmpty()) {
                        socketManager.joinRoom(conversationId);
                    }
                }
                loadMessages();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing socket: " + e.getMessage());
            Toast.makeText(this, "Lỗi kết nối socket", Toast.LENGTH_SHORT).show();
        }
    }

    private void setClickListeners() {
        btnSend.setOnClickListener(v -> sendMessage());
        btnAttach.setOnClickListener(v -> openImagePicker());
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (socketManager != null) {
                if (!socketManager.isConnected()) {
                    Log.d(TAG, "Socket not connected, connecting...");
                    socketManager.connect();
                } else {
                    Log.d(TAG, "Socket already connected, joining room directly");
                    if (conversationId != null && !conversationId.isEmpty()) {
                        socketManager.joinRoom(conversationId);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onResume: " + e.getMessage());
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        // Don't disconnect socket here as we might come back
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Don't disconnect socket here as we might come back from background
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (socketManager != null) {
                // Leave the conversation room first
                if (conversationId != null && !conversationId.isEmpty()) {
                    emitMarkRead();
                    socketManager.leaveRoom(conversationId);
                }
                // Remove event listener to prevent memory leaks
                //socketManager.setEventListener(null);
                // Disconnect socket
                //socketManager.disconnect();
                // Delay disconnect slightly to ensure emits are sent
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    socketManager.setEventListener(null);
                    socketManager.disconnect();
                    Log.d(TAG, "Socket disconnected after markRead emit");
                }, 2000); // Delay
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy: " + e.getMessage());
        }
    }
    private void emitMarkRead() {
        try {
            JSONObject data = new JSONObject();
            data.put("conversationid", conversationId);
            data.put("userid", userId); // user đang đọc
            socketManager.markRead(data);
            Log.d(TAG, "Emitted mark_read for conversation: " + conversationId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void loadMessages() {
        if (conversationId == null || conversationId.isEmpty()) {
            Log.e(TAG, "conversation_id is null or empty");
            return;
        }

        Log.d(TAG, "Loading messages for conversation: " + conversationId);
        apiService.getMessages(conversationId).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Messages loaded successfully: " + response.body().getMessages().size() + " messages");
                    messageList.clear();
                    messageList.addAll(response.body().getMessages());
                    messageAdapter.notifyDataSetChanged();
                    recyclerMessages.scrollToPosition(messageList.size() - 1);
                } else {
                    Log.e(TAG, "Error loading messages: " + response.code() + " - " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Log.e(TAG, "Failed to load messages: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void sendMessage() {
        String content = edtMessage.getText().toString().trim();
        if (content.isEmpty()) return;

        Message message = new Message();
        message.setConversationId(conversationId);

        // Create sender object with full info
        Message.Sender sender = new Message.Sender();
        sender.setId(userId);
        sender.setUsername(SharedPrefManager.getInstance(this).getUsername());
        sender.setName(SharedPrefManager.getInstance(this).getName());
        sender.setAvatar(SharedPrefManager.getInstance(this).getAvatar());
        message.setSender(sender);

        message.setContent(content);
        message.setMessageType("text");
        message.setTimestamp(String.valueOf(System.currentTimeMillis()));

        // Gửi tin nhắn qua socket
        socketManager.sendMessage(message);

        // Xóa nội dung input
        edtMessage.setText("");
    }


    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                sendImageMessage(selectedImageUri);
            }
        }
    }

    private void sendImageMessage(Uri imageUri) {
        try {
            // Get real file path from Uri
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
            if (cursor == null) {
                Toast.makeText(this, "Không thể đọc file ảnh", Toast.LENGTH_SHORT).show();
                return;
            }

            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            if (filePath == null) {
                Toast.makeText(this, "Không tìm thấy file ảnh", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create file from path
            File file = new File(filePath);

            // Tạo message object
            Message message = new Message();
            message.setConversationId(conversationId);

            // Create sender object with full info
            Message.Sender sender = new Message.Sender();
            sender.setId(SharedPrefManager.getInstance(this).getUserId());
            sender.setUsername(SharedPrefManager.getInstance(this).getUsername());
            sender.setName(SharedPrefManager.getInstance(this).getName());
            sender.setAvatar(SharedPrefManager.getInstance(this).getAvatar());
            message.setSender(sender);

            // Show loading
            Toast.makeText(this, "Đang gửi ảnh...", Toast.LENGTH_SHORT).show();


            // Delay 2 giây rồi mới gửi ảnh
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // Gửi ảnh qua MessageSocketManager
                socketManager.sendImageMessage(message, file, new MessageSocketManager.ImageUploadCallback() {
                    @Override
                    public void onSuccess(String imageUrl) {
                        runOnUiThread(() -> {
                            Toast.makeText(MessageActivity.this,
                                    "Gửi ảnh thành công",
                                    Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            Toast.makeText(MessageActivity.this,
                                    "Lỗi gửi ảnh: " + error,
                                    Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }, 2000);

        } catch (Exception e) {
            Log.e(TAG, "Error preparing image: " + e.getMessage());
            Toast.makeText(this, "Lỗi khi chuẩn bị ảnh", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onConnect() {
        Log.d(TAG, "Socket connected");
        if (conversationId != null && !conversationId.isEmpty()) {
            Log.d(TAG, "Joining room after connect: " + conversationId);
            socketManager.joinRoom(conversationId);
        }
    }

    @Override
    public void onDisconnect() {
        Log.d(TAG, "Socket disconnected");
        runOnUiThread(() -> 
            Toast.makeText(this, "Mất kết nối socket", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void onError(String error) {
        Log.e(TAG, "Socket error: " + error);
        runOnUiThread(() -> 
            Toast.makeText(this, "Lỗi kết nối: " + error, Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void onNewMessage(JSONObject messageData) {
        try {
            String conversationIdReceived = messageData.getString("conversation_id");

            if (conversationId.equals(conversationIdReceived)) {
                Message newMessage = new Message();
                newMessage.setConversationId(conversationIdReceived);
                newMessage.setContent(messageData.getString("content"));
                newMessage.setMessageType(messageData.getString("message_type"));
                newMessage.setTimestamp(messageData.getString("timestamp"));

                JSONObject senderData = messageData.getJSONObject("sender");
                Message.Sender sender = new Message.Sender();
                sender.setId(senderData.getString("id"));
                sender.setUsername(senderData.getString("username"));
                sender.setName(senderData.getString("name"));
                sender.setAvatar(senderData.getString("avatar"));
                newMessage.setSender(sender);

                runOnUiThread(() -> {
                    messageAdapter.addMessage(newMessage);
                    recyclerMessages.scrollToPosition(messageAdapter.getItemCount() - 1);
                });
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error in onNewMessage: " + e.getMessage());
        }
    }



/*    @Override
    public void onNewMessage(Message message) {
        try {
            if (message.getConversationId().equals(conversationId)) {
                runOnUiThread(() -> {
                    try {
                        // Thêm tin nhắn vào adapter
                        messageAdapter.addMessage(message);
                        recyclerMessages.scrollToPosition(messageAdapter.getItemCount() - 1);
                        
                        // Phát âm thanh nếu là tin nhắn từ người khác
                        if (!message.getSender().getId().equals(userId)) {
                            // TODO: Phát âm thanh thông báo
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error updating UI with new message: " + e.getMessage());
                        Toast.makeText(MessageActivity.this, 
                            "Lỗi hiển thị tin nhắn mới", 
                            Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling new message: " + e.getMessage());
            runOnUiThread(() -> 
                Toast.makeText(MessageActivity.this, 
                    "Lỗi xử lý tin nhắn mới", 
                    Toast.LENGTH_SHORT).show()
            );
        }
    }

    @Override
    public void onUserTyping(JSONObject typingData) {
        // TODO: Hiển thị trạng thái typing
    }

    @Override
    public void onMessageStatus(JSONObject statusData) {
        // TODO: Cập nhật trạng thái tin nhắn (đã gửi, đã nhận, đã đọc)
    }*/
    @Override
    public void onOnlineUsersUpdate(String onlineUsers) {
        Log.d(TAG, "Online users updated: " + onlineUsers);
    }

    @Override
    public void onUserStatusChange(String userId, boolean isOnline) {
        Log.d(TAG, "User " + userId + " is " + (isOnline ? "online" : "offline"));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
