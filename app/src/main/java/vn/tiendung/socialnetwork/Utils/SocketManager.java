package vn.tiendung.socialnetwork.Utils;

import android.util.Log;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class SocketManager {

    private static SocketManager instance;
    private Socket socket;

    private SocketManager() {
        try {
            // Kết nối đến server Socket
            //socket = IO.socket("https://socialnetwork-api-zbeb.onrender.com/chat"); // Đổi thành URL của server socket của bạn
            socket = IO.socket("http://10.0.2.2:3001/chat");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static synchronized SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    public Socket getSocket() {
        return socket;
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
}

