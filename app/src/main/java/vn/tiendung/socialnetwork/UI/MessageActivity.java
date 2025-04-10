package vn.tiendung.socialnetwork.UI;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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

public class MessageActivity extends AppCompatActivity implements MessageSocketManager.SocketEventListener {
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
    private MessageSocketManager socketManager;
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
            socketManager = MessageSocketManager.getInstance();
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
                    // Room will be joined in onConnect callback
                } else {
                    // Socket is already connected, join room directly
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
                    socketManager.leaveRoom(conversationId);
                }
                // Remove event listener to prevent memory leaks
                socketManager.setEventListener(null);
                // Disconnect socket
                socketManager.disconnect();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy: " + e.getMessage());
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
        sender.setId(SharedPrefManager.getInstance(this).getUserId());
        sender.setUsername(SharedPrefManager.getInstance(this).getUsername());
        sender.setName(SharedPrefManager.getInstance(this).getName());
        sender.setAvatar(SharedPrefManager.getInstance(this).getAvatar());
        message.setSender(sender);

        message.setContent(content);
        message.setMessageType("text");
        message.setTimestamp(String.valueOf(System.currentTimeMillis()));

        // Log tin nhắn trước khi gửi
        Log.d(TAG, "Sending message:");
        Log.d(TAG, "- Conversation ID: " + message.getConversationId());
        Log.d(TAG, "- Sender ID: " + message.getSender().getId());
        Log.d(TAG, "- Content: " + message.getContent());
        Log.d(TAG, "- Timestamp: " + message.getTimestamp());

        socketManager.sendMessage(message);
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
            
            // Create request body for file
            RequestBody requestFile = RequestBody.create(
                MediaType.parse("multipart/form-data"),
                file
            );

            // Create MultipartBody.Part
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData(
                "image", 
                file.getName(),
                requestFile
            );

            // Create other request bodies
            RequestBody conversationIdBody = RequestBody.create(
                MediaType.parse("text/plain"),
                conversationId
            );

            RequestBody senderIdBody = RequestBody.create(
                MediaType.parse("text/plain"),
                SharedPrefManager.getInstance(this).getUserId()
            );

            // Log request details
            Log.d(TAG, "Sending image message:");
            Log.d(TAG, "Conversation ID: " + conversationId);
            Log.d(TAG, "Sender ID: " + SharedPrefManager.getInstance(this).getUserId());
            Log.d(TAG, "Image path: " + filePath);
            Log.d(TAG, "Image name: " + file.getName());
            Log.d(TAG, "Image size: " + file.length() + " bytes");

            // Show loading
            Toast.makeText(this, "Đang gửi ảnh...", Toast.LENGTH_SHORT).show();

            apiService.sendImageMessage(conversationIdBody, senderIdBody, imagePart)
                .enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Message sentMessage = response.body();
                            messageList.add(sentMessage);
                            messageAdapter.notifyItemInserted(messageList.size() - 1);
                            recyclerMessages.scrollToPosition(messageList.size() - 1);
                            Toast.makeText(MessageActivity.this, "Gửi ảnh thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Error sending image message: " + response.code());
                            if (response.errorBody() != null) {
                                try {
                                    String errorBody = response.errorBody().string();
                                    Log.e(TAG, "Error body: " + errorBody);
                                    Log.e(TAG, "Request URL: " + call.request().url());
                                    Log.e(TAG, "Request method: " + call.request().method());
                                    Log.e(TAG, "Request headers: " + call.request().headers());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            Toast.makeText(MessageActivity.this, 
                                "Lỗi khi gửi ảnh: " + response.code(), 
                                Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        Log.e(TAG, "Failed to send image message: " + t.getMessage());
                        Log.e(TAG, "Request URL: " + call.request().url());
                        t.printStackTrace();
                        Toast.makeText(MessageActivity.this, 
                            "Không thể gửi ảnh: " + t.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    }
                });

        } catch (Exception e) {
            Log.e(TAG, "Error preparing image: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi chuẩn bị ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting file name: " + e.getMessage());
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
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
    public void onNewMessage(Message message) {
        try {
            // Log thông tin tin nhắn mới
            Log.d(TAG, "New message received:");
            Log.d(TAG, "- Message ID: " + message.getId());
            Log.d(TAG, "- Conversation ID: " + message.getConversationId());
            Log.d(TAG, "- Sender: " + message.getSender().getName());
            Log.d(TAG, "- Content: " + message.getContent());
            Log.d(TAG, "- Type: " + message.getMessageType());

            // Kiểm tra xem tin nhắn có thuộc về conversation hiện tại không
            if (message.getConversationId().equals(conversationId)) {
                runOnUiThread(() -> {
                    try {
                        // Thêm tin nhắn vào adapter
                        messageAdapter.addMessage(message);
                        
                        // Cuộn xuống tin nhắn mới nhất
                        recyclerMessages.scrollToPosition(messageAdapter.getItemCount() - 1);
                        
                        // Nếu là tin nhắn của người khác, phát âm thanh thông báo
                        if (!message.getSender().getId().equals(userId)) {
                            // TODO: Phát âm thanh thông báo
                            // playNotificationSound();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error updating UI with new message: " + e.getMessage());
                        Toast.makeText(MessageActivity.this, 
                            "Lỗi hiển thị tin nhắn mới", 
                            Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.d(TAG, "Message belongs to different conversation");
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
    }
}
