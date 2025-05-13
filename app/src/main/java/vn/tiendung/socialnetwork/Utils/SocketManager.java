package vn.tiendung.socialnetwork.Utils;

import android.util.Log;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.RetrofitClient;
import vn.tiendung.socialnetwork.Model.Message;

import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SocketManager {
    private static String URL = "http://10.0.2.2:3001";
    private static SocketManager instance;
    private Socket socket;

    private String userId;
    private String currentRoom;
    private boolean isConnected = false;
    private SocketEventListener eventListener;
    private APIService apiService;

    private static final String TAG = "SocketManager";

    private SocketManager() {
        try {
            IO.Options opts = new IO.Options();
            opts.reconnection = true;
            opts.reconnectionAttempts = 10;
            opts.reconnectionDelay = 1000;

            socket = IO.socket(URL, opts);
            setupSocketListeners();

        } catch (URISyntaxException e) {
            Log.e(TAG, "Socket URI error: " + e.getMessage());
        }
    }
    public Socket getSocket() {
        return socket;
    }
    public static synchronized SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    public void initialize(String userId) {
        this.userId = userId;

        if (!socket.connected()) {
            socket.connect();
        } else {
            emitUserConnected();
        }
    }

    public void connect() {
        if (!socket.connected()) {
            socket.connect();
        }
    }

    public void disconnect() {
        if (socket.connected()) {
            socket.disconnect();
        }
    }

    public boolean isConnected() {
        return socket.connected();
    }

    public void setEventListener(SocketEventListener listener) {
        this.eventListener = listener;
    }

    private void setupSocketListeners() {
        socket.on(Socket.EVENT_CONNECT, args -> {
            Log.d(TAG, "Socket connected");
            isConnected = true;
            emitUserConnected();

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
            Log.e(TAG, "Connection error: " + args[0]);

            if (eventListener != null) {
                eventListener.onError("Connection error: " + args[0]);
            }
        });

        socket.on("new_message", args -> {
            if (eventListener != null && args.length > 0) {
                try {
                    JSONObject messageData = (JSONObject) args[0];
                    eventListener.onNewMessage(messageData);
                } catch (Exception e) {
                    Log.e(TAG, "Error in new_message: " + e.getMessage());
                }
            }
        });

        socket.on("update_online_users", args -> {
            if (eventListener != null && args.length > 0) {
                eventListener.onOnlineUsersUpdate(args[0].toString());
            }
        });

        socket.on("user_status_change", args -> {
            if (eventListener != null && args.length > 1) {
                String userId = args[0].toString();
                boolean isOnline = (boolean) args[1];
                eventListener.onUserStatusChange(userId, isOnline);
            }
        });
    }

    private void emitUserConnected() {
        if (userId != null) {
            socket.emit("user_connected", userId);
            Log.d(TAG, "User connected: " + userId);
        }
    }

    public void joinRoom(String roomId) {
        if (socket != null && isConnected) {
            if (currentRoom != null && !currentRoom.equals(roomId)) {
                socket.emit("leave_conversation", currentRoom);
            }

            socket.emit("join_conversation", roomId);
            currentRoom = roomId;
            Log.d(TAG, "Joined room: " + roomId);
        }
    }

    public void leaveRoom(String roomId) {
        if (socket != null && isConnected && roomId.equals(currentRoom)) {
            socket.emit("leave_conversation", roomId);
            currentRoom = null;
            Log.d(TAG, "Left room: " + roomId);
        }
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

    public void sendImageMessage(Message message, File imageFile, MessageSocketManager.ImageUploadCallback callback) {
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
    public void markRead(JSONObject data) {
        if (socket != null && isConnected && currentRoom != null) {
            socket.emit("mark_read", data);
            Log.d(TAG, "Mark read for data: " + data.toString());
        } else {
            Log.e(TAG, "Cannot mark read - socket not connected or not in a conversation");
        }
    }

    public interface ImageUploadCallback {
        void onSuccess(String imageUrl);
        void onError(String error);
    }
    public interface SocketEventListener {
        void onConnect();
        void onDisconnect();
        void onError(String error);
        void onNewMessage(JSONObject messageData);
        void onOnlineUsersUpdate(String onlineUsers);
        void onUserStatusChange(String userId, boolean isOnline);
    }
}
