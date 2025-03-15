package vn.tiendung.socialnetwork.UI;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import vn.tiendung.socialnetwork.Fragment.ChatFragment;
import vn.tiendung.socialnetwork.Fragment.FriendFragment;
import vn.tiendung.socialnetwork.Fragment.HomeFragment;
import vn.tiendung.socialnetwork.Fragment.ProfileFragment;
import vn.tiendung.socialnetwork.Fragment.WritePostFragment;
import vn.tiendung.socialnetwork.R;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fabWritePost = findViewById(R.id.fabWritePost);
        fabWritePost.setVisibility(View.VISIBLE);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

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
}