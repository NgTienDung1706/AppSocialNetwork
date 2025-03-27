package vn.tiendung.socialnetwork.UI;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import vn.tiendung.socialnetwork.Fragment.ChatFragment;
import vn.tiendung.socialnetwork.Fragment.FriendFragment;
import vn.tiendung.socialnetwork.Fragment.HomeFragment;
import vn.tiendung.socialnetwork.Fragment.ProfileFragment;
import vn.tiendung.socialnetwork.Fragment.WritePostFragment;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.Utils.OnScrollListener;

public class MainActivity extends AppCompatActivity implements OnScrollListener {

    private BottomNavigationView bottomNavigationView;
    private View fragmentContainer;
    private RecyclerView recyclerViewPosts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

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
                selectedFragment = new ChatFragment();
            } else if (itemId == R.id.nav_friends) {
                selectedFragment = new FriendFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            } else if (itemId == R.id.nav_writepost) {  // Xử lý khi nhấn nút viết bài
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
}