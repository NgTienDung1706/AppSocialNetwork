// app/src/main/java/vn/tiendung/socialnetwork/Utils/SocketManager.java
package vn.tiendung.socialnetwork.Utils;

import android.util.Log;

import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.RetrofitClient;
import vn.tiendung.socialnetwork.Model.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.io.File;
import java.util.concurrent.TimeUnit;

public class MessageSocketManager {
    private static final String TAG = "MessageSocketManager";
    private static MessageSocketManager instance;
    private Socket socket;
    private boolean isConnected = false;
    private String currentRoom = null;
    private SocketEventListener eventListener;
    private String userId;

    private APIService apiService;

    private MessageSocketManager() {
        // Private constructor
    }

    public static synchronized MessageSocketManager getInstance() {
        if (instance == null) {
            instance = new MessageSocketManager();
        }
        return instance;
    }

    public void initialize(String userId) {
        this.userId = userId;
        if (socket == null) {
            try {
                IO.Options opts = new IO.Options();
                opts.forceNew = false;
                opts.reconnection = true;
                opts.reconnectionAttempts = 10;
                opts.reconnectionDelay = 1000;
                //socket = IO.socket("https://socialnetwork-api-zbeb.onrender.com/message", opts);
                socket = IO.socket("http://10.0.2.2:3001/message", opts);
                setupSocketListeners();
            } catch (URISyntaxException e) {
                Log.e(TAG, "Error creating socket: " + e.getMessage());
            }
        }
    }

    public void connect() {
        if (socket != null && !isConnected) {
            Log.d(TAG, "Connecting socket...");
            socket.connect();
        }
    }

    public void disconnect() {
        if (socket != null) {
            try {
                if (currentRoom != null) {
                    Log.d(TAG, "Leaving current room before disconnect: " + currentRoom);
                    leaveRoom(currentRoom);
                }
                
                Log.d(TAG, "Disconnecting socket...");
                socket.disconnect();
                isConnected = false;
                currentRoom = null;
                
                Log.d(TAG, "Socket disconnected successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error disconnecting socket: " + e.getMessage());
            }
        }
    }

    private void setupSocketListeners() {
        if (socket == null) return;

        socket.on(Socket.EVENT_CONNECT, args -> {
            Log.d(TAG, "Socket connected");
            isConnected = true;
            
            // Emit user_online event with userId
            if (userId != null) {
                try {
                    socket.emit("user_online", userId);
                    Log.d(TAG, "Emitted user_online event for user: " + userId);
                } catch (Exception e) {
                    Log.e(TAG, "Error emitting user_online: " + e.getMessage());
                }
            }
            
            if (eventListener != null) {
                eventListener.onConnect();
            }
        });

        socket.on(Socket.EVENT_DISCONNECT, args -> {
            Log.d(TAG, "Socket disconnected");
            isConnected = false;
            currentRoom = null;
            if (eventListener != null) {
                eventListener.onDisconnect();
            }
        });

        socket.on(Socket.EVENT_CONNECT_ERROR, args -> {
            Log.e(TAG, "Socket connection error: " + (args.length > 0 ? args[0] : "Unknown error"));
            isConnected = false;
            if (eventListener != null) {
                eventListener.onError("Connection error");
            }
        });

        socket.on("new_message", args -> {
            if (args.length > 0 && eventListener != null) {
                try {
                    Log.d(TAG, "Raw message data: " + args[0].toString());
                    JSONObject messageData = (JSONObject) args[0];
                    Message message = new Message();
                    
                    message.setId(messageData.getString("id"));
                    message.setConversationId(messageData.getString("conversation_id"));
                    
                    // Parse sender data correctly
                    JSONObject senderData = messageData.getJSONObject("sender_id");
                    Log.d(TAG, "Sender data: " + senderData.toString());
                    
                    Message.Sender sender = new Message.Sender();
                    sender.setId(senderData.getString("_id")); // Server sends _id for sender
                    
                    // Get username if available
                    if (senderData.has("username")) {
                        sender.setUsername(senderData.getString("username"));
                        Log.d(TAG, "Sender username: " + sender.getUsername());
                    }
                    
                    // Get name if available
                    if (senderData.has("name")) {
                        sender.setName(senderData.getString("name"));
                        Log.d(TAG, "Sender name: " + sender.getName());
                    } else if (senderData.has("fullname")) { // Try fullname if name is not available
                        sender.setName(senderData.getString("fullname"));
                        Log.d(TAG, "Sender fullname: " + sender.getName());
                    }
                    
                    // Get avatar if available
                    if (senderData.has("avatar")) {
                        sender.setAvatar(senderData.getString("avatar"));
                        Log.d(TAG, "Sender avatar: " + sender.getAvatar());
                    }
                    
                    message.setSender(sender);
                    
                    message.setContent(messageData.getString("content"));
                    message.setMessageType(messageData.getString("message_type"));
                    message.setTimestamp(messageData.getString("timestamp"));
                    //message.setImageUrl(messageData.getString("image_url"));
                    eventListener.onNewMessage(message);
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error handling new message: " + e.getMessage());
                    Log.e(TAG, "Stack trace: ", e);
                    if (args.length > 0) {
                        Log.e(TAG, "Raw message that caused error: " + args[0].toString());
                    }
                }
            }
        });
    }

    public void joinRoom(String roomId) {
        if (socket != null && isConnected && roomId != null && !roomId.isEmpty()) {
            // If already in the same room, don't rejoin
            if (currentRoom != null && currentRoom.equals(roomId)) {
                Log.d(TAG, "Already in conversation: " + roomId);
                return;
            }
            
            // Leave current room if in a different room
            if (currentRoom != null && !currentRoom.equals(roomId)) {
                Log.d(TAG, "Leaving current conversation: " + currentRoom);
                socket.emit("leave_conversation", currentRoom);
            }
            
            Log.d(TAG, "Joining conversation: " + roomId);
            socket.emit("join_conversation", roomId);
            currentRoom = roomId;
        }
    }

    public void leaveRoom(String roomId) {
        if (socket != null && isConnected && roomId != null && !roomId.isEmpty()) {
            if (currentRoom != null && currentRoom.equals(roomId)) {
                Log.d(TAG, "Leaving conversation: " + roomId);
                socket.emit("leave_conversation", roomId);
                currentRoom = null;
            }
        }
    }

    public boolean isConnected() {
        return socket != null && isConnected;
    }

    public void setEventListener(SocketEventListener listener) {
        this.eventListener = listener;
    }

    public void sendMessage(Message message) {
        if (socket != null && isConnected && currentRoom != null) {
            try {
                JSONObject messageData = new JSONObject();
                messageData.put("conversation_id", message.getConversationId());
                messageData.put("sender", new JSONObject()
                    .put("id", message.getSender().getId())
                    .put("username", message.getSender().getUsername())
                    .put("name", message.getSender().getName())
                    .put("avatar", message.getSender().getAvatar()));
                messageData.put("content", message.getContent());
                messageData.put("message_type", message.getMessageType());
                
                String isoTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                    .format(new Date(Long.parseLong(message.getTimestamp())));
                messageData.put("timestamp", isoTimestamp);

                Log.d(TAG, "Sending message data: " + messageData.toString());
                socket.emit("send_message", messageData);
            } catch (Exception e) {
                Log.e(TAG, "Error sending message: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "Cannot send message - socket not connected or not in a conversation");
        }
    }

    /**
     * Interface để callback kết quả upload ảnh
     */
    public interface ImageUploadCallback {
        void onSuccess(String imageUrl);
        void onError(String error);
    }

    /**
     * Gửi tin nhắn hình ảnh thông qua backend
     */
    public void sendImageMessage(Message message, File imageFile, ImageUploadCallback callback) {
        if (socket == null || !isConnected || currentRoom == null) {
            callback.onError("Socket not connected or not in conversation");
            return;
        }

        // Tạo MultipartBody.Part cho file ảnh
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image",
                imageFile.getName(), requestFile);

        // Tạo RequestBody cho các field khác
        RequestBody conversationId = RequestBody.create(MediaType.parse("text/plain"),
                message.getConversationId());
        RequestBody senderId = RequestBody.create(MediaType.parse("text/plain"),
                message.getSender().getId());

        // Gọi API
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ResponseBody> call = apiService.sendImageMessage(imagePart, conversationId, senderId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess("Upload successful");
                } else {
                    callback.onError("Upload failed: " + response.message());
                }
            }

            @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        callback.onError("Network error: " + t.getMessage());
                    }
                });
    }

    public interface SocketEventListener {
        void onConnect();
        void onDisconnect();
        void onError(String error);
        void onNewMessage(Message message);
        void onUserTyping(JSONObject typingData);
        void onMessageStatus(JSONObject statusData);
    }
}
