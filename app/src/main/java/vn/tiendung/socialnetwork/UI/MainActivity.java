package vn.tiendung.socialnetwork.UI;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
    private FloatingActionButton fabWritePost;
    private View fragmentContainer;
    private RecyclerView recyclerViewPosts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabWritePost = findViewById(R.id.fabWritePost);
        fabWritePost.setVisibility(View.VISIBLE);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fragmentContainer = findViewById(R.id.fragment_containerr);


        // Gắn sự kiện theo dõi lướt nội dung
        setupScrollListener();


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
    // Hàm chưa chạy được
    private void setupScrollListener() {
        fragmentContainer.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    // Lướt xuống - Ẩn BottomNavigationView và FAB
                    bottomNavigationView.animate().translationY(bottomNavigationView.getHeight()).setDuration(200);
                    fabWritePost.animate().translationY(fabWritePost.getHeight() + 20).setDuration(200);
                } else if (scrollY < oldScrollY) {
                    // Lướt lên - Hiện BottomNavigationView và FAB
                    bottomNavigationView.animate().translationY(0).setDuration(200);
                    fabWritePost.animate().translationY(0).setDuration(200);
                }
            }
        });
    }}