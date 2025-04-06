package vn.tiendung.socialnetwork.UI;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import vn.tiendung.socialnetwork.Fragment.ChatFragment;
import vn.tiendung.socialnetwork.Fragment.FriendFragment;
import vn.tiendung.socialnetwork.Fragment.HomeFragment;
import vn.tiendung.socialnetwork.Fragment.ProfileFragment;
import vn.tiendung.socialnetwork.Fragment.WritePostFragment;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.Utils.OnScrollListener;
import vn.tiendung.socialnetwork.Utils.SharedPrefManager;
import vn.tiendung.socialnetwork.Utils.SocketManager;

public class MainActivity extends AppCompatActivity implements OnScrollListener {
    private ChatFragment chatFragment;

    private List<String> onlineUserIds = new ArrayList<>(); // Thêm biến này để lưu trữ danh sách user online
    private boolean isSocketListenerSet = false;


    private BottomNavigationView bottomNavigationView;
    private View fragmentContainer;
    private RecyclerView recyclerViewPosts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Khởi tạo chatFragment ngay từ đầu
        if (chatFragment == null) {
            chatFragment = new ChatFragment();
        }
        
        // Thiết lập socket listener ngay từ khi khởi tạo
        setupSocketListener();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        // Hiển thị Fragment mặc định khi mở app
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containerr, new HomeFragment()).commit();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_chat) {
                // Luôn sử dụng instance hiện tại của chatFragment
                selectedFragment = chatFragment;
                // Cập nhật trạng thái online ngay khi chuyển sang tab chat
                if (chatFragment != null) {
                    // Yêu cầu cập nhật trạng thái online
                    SocketManager.getInstance().getSocket().emit("request_online_status");
                    // Cập nhật UI với danh sách hiện tại
                    chatFragment.refreshOnlineStatus(onlineUserIds);
                }
            } else if (itemId == R.id.nav_friends) {
                selectedFragment = new FriendFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            } else if (itemId == R.id.nav_writepost) {
                selectedFragment = new WritePostFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_containerr, selectedFragment)
                        .commit();
            }

            return true;
        });
    }

    @Override
    public void onScroll(boolean isScrollingDown) {
        if (bottomNavigationView != null) {
            if (isScrollingDown) {
                bottomNavigationView.animate().translationY(bottomNavigationView.getHeight() + 30).setDuration(300);
            } else {
                bottomNavigationView.animate().translationY(0).setDuration(300);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Đã cấp quyền truy cập!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền để chọn ảnh!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void setupSocketListener() {
        if (isSocketListenerSet) return;

        // Lắng nghe sự kiện cập nhật danh sách người dùng online
        SocketManager.getInstance().getSocket().on("update_online_users", args -> {
            JSONArray jsonArray = (JSONArray) args[0];
            onlineUserIds.clear();

            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    onlineUserIds.add(jsonArray.getString(i));
                }
                Log.d("MainActivity", "Received online users: " + onlineUserIds.size());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Cập nhật UI trên thread chính
            runOnUiThread(() -> {
                if (chatFragment != null) {
                    chatFragment.refreshOnlineStatus(onlineUserIds);
                }
            });
        });

        // Lắng nghe sự kiện user online/offline
        SocketManager.getInstance().getSocket().on("user_status_change", args -> {
            String userId = (String) args[0];
            boolean isOnline = (boolean) args[1];
            
            runOnUiThread(() -> {
                if (isOnline && !onlineUserIds.contains(userId)) {
                    onlineUserIds.add(userId);
                } else if (!isOnline) {
                    onlineUserIds.remove(userId);
                }
                
                if (chatFragment != null) {
                    chatFragment.refreshOnlineStatus(onlineUserIds);
                }
            });
        });

        isSocketListenerSet = true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Kết nối socket nếu chưa kết nối
        if (!SocketManager.getInstance().isConnected()) {
            SocketManager.getInstance().connect();
            setupSocketListener();
        }

        // Gửi thông tin người dùng lên server khi kết nối
        String userId = SharedPrefManager.getInstance(getApplicationContext()).getUserId();
        SocketManager.getInstance().getSocket().emit("user_connected", userId);

        // Yêu cầu danh sách người dùng online ngay lập tức
        SocketManager.getInstance().getSocket().emit("request_online_status");
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Gửi thông báo user ngắt kết nối trước khi ngắt socket
        String userId = SharedPrefManager.getInstance(getApplicationContext()).getUserId();
        SocketManager.getInstance().getSocket().emit("user_disconnected", userId);

        // Không ngắt kết nối socket ở đây nữa để giữ kết nối
        // if (SocketManager.getInstance().isConnected()) {
        //     SocketManager.getInstance().disconnect();
        // }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Yêu cầu cập nhật trạng thái online mỗi khi activity được focus
        if (SocketManager.getInstance().isConnected()) {
            SocketManager.getInstance().getSocket().emit("request_online_status");
        }
    }


}