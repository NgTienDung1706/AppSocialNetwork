package vn.tiendung.socialnetwork.Utils;

import io.socket.client.IO;
import io.socket.client.Socket;
import java.net.URI;
import java.net.URISyntaxException;

public class SocketManager {

    private static SocketManager instance;
    private Socket socket;

    private SocketManager() {
        try {
            // Kết nối đến server Socket
            socket = IO.socket("https://socialnetwork-api-zbeb.onrender.com"); // Đổi thành URL của server socket của bạn
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

