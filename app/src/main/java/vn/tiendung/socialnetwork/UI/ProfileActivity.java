package vn.tiendung.socialnetwork.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import androidx.lifecycle.ViewModelProvider;
import vn.tiendung.socialnetwork.ViewModel.ProfileViewModel;


import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.RetrofitClient;
import vn.tiendung.socialnetwork.Adapter.EditProfileBottomSheet;
import vn.tiendung.socialnetwork.Adapter.ViewPagerAdapter;
import vn.tiendung.socialnetwork.Model.UserProfile;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.Utils.SharedPrefManager;

public class ProfileActivity extends AppCompatActivity {
    private String userId;
    private UserProfile userProfile;

    private ImageView ivAvatar, ivUpdateProfile;
    private TextView tvUsername, tvFriendCount, tvPostCount, tvAboutMe;
    private FlexboxLayout flexboxLayout;
    private AppBarLayout appBarLayout;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    private Button btnAddFriend,btnRequestSent,btnUnfriend,btnAcceptt,btnDelete;

    private ProfileViewModel viewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Dùng lại layout fragment

        userId = getIntent().getStringExtra("userId");
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        String userIdMe = SharedPrefManager.getInstance(getApplicationContext()).getUserId();

        // Gán View
        ivAvatar = findViewById(R.id.ivAvatar);
        ivUpdateProfile = findViewById(R.id.ivUpdateProfile);
        tvUsername = findViewById(R.id.tvUsername);
        tvFriendCount = findViewById(R.id.tvFriendCount);
        tvPostCount = findViewById(R.id.tvPostCount);
        tvAboutMe = findViewById(R.id.tvAboutMe);
        flexboxLayout = findViewById(R.id.flexboxLayout);
        appBarLayout = findViewById(R.id.appBarLayout);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        btnAddFriend = findViewById(R.id.btnAddFriend);
        btnRequestSent = findViewById(R.id.btnRequestSent);
        btnUnfriend = findViewById(R.id.btnUnfriend);
        btnAcceptt = findViewById(R.id.btnAcceptt);
        btnDelete = findViewById(R.id.btnDelete);
        // Setup ViewPager
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, userId);
        viewPager.setAdapter(adapter);


        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Bài viết gần đây");
            } else {
                tab.setText("Album ảnh");
            }
        }).attach();

        // Lấy thông tin người dùng
        viewModel.fetchUserProfile(userId,userIdMe);

        // Quan sát thay đổi trong userProfileLiveData
        viewModel.getUserProfileLiveData().observe(this, userProfile -> {
            if (userProfile != null) {
                updateProfileUI(userProfile);
            }
        });

        // Quan sát thông báo lỗi
        viewModel.getErrorMessageLiveData().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Quan sát thay đổi trong thông tin kết bạn
        viewModel.getFriendStatusLiveData().observe(this, status -> {
            if (status != null) {
                updateFriendButtons(status);
            }
        });
        // Xử lý nút "Thêm bạn bè"
        btnAddFriend.setOnClickListener(v -> {
            showConfirmationDialog(this, "Bạn có chắc muốn gửi lời mời kết bạn?", () -> {
                viewModel.addFriend(userId,userIdMe);
            });

        });
        btnAcceptt.setOnClickListener(v -> {
            showConfirmationDialog(this, "Bạn có chắc muốn chấp nhận lời mời kết bạn?", () -> {
                viewModel.accept(userId,userIdMe);
            });
        });
        btnDelete.setOnClickListener(v -> {
            showConfirmationDialog(this, "Bạn có chắc muốn từ chối lời mời kết bạn?", () -> {
                viewModel.reject(userId,userIdMe);
            });
        });
        btnUnfriend.setOnClickListener(v -> {
            showConfirmationDialog(this, "Chắc chưa ?", () -> {
                viewModel.unfriend(userId,userIdMe);
            });
        });
    }

    private void updateProfileUI(UserProfile userProfile) {
        tvUsername.setText(userProfile.getFullname());
        tvFriendCount.setText(userProfile.getFriendsCount() + " bạn");
        tvPostCount.setText(userProfile.getPostsCount() + " bài viết");
        tvAboutMe.setText(userProfile.getBio().isEmpty() ? "Chạm để viết..." : userProfile.getBio());
        String status = userProfile.getRelationship();
        updateFriendButtons(status);
        Glide.with(ProfileActivity.this)
                .load(userProfile.getAvatar())
                .placeholder(R.drawable.avt_default)
                .error(R.drawable.avt_default)
                .circleCrop()
                .into(ivAvatar);

        // Hiển thị tag
        flexboxLayout.removeAllViews();
        for (String tag : userProfile.getFavoriteTags()) {
            TextView tagView = new TextView(ProfileActivity.this);
            tagView.setText("#" + tag);
            tagView.setTextSize(14);
            tagView.setTextColor(ContextCompat.getColor(ProfileActivity.this, R.color.nd));
            tagView.setBackground(ContextCompat.getDrawable(ProfileActivity.this, R.drawable.tag));
            tagView.setOnClickListener(v -> {});

            FrameLayout container = new FrameLayout(ProfileActivity.this);
            container.setPadding(16, 8, 16, 8);
            container.addView(tagView);

            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                    FlexboxLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 8, 8, 8);
            container.setLayoutParams(params);

            flexboxLayout.addView(container);
        }
    }

    private void updateFriendButtons(String status) {
        Log.d("ProfileDebug", "Relationship: " + status);

        btnAddFriend.setVisibility(View.GONE);
        btnRequestSent.setVisibility(View.GONE);
        btnUnfriend.setVisibility(View.GONE);

        switch (status) {
            case "pending":
                btnAcceptt.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
                btnAddFriend.setVisibility(View.GONE);
                break;
            case "none":
                btnAddFriend.setVisibility(View.VISIBLE);
                btnAcceptt.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
                break;
            case "received":
                btnRequestSent.setVisibility(View.VISIBLE);
                btnAddFriend.setVisibility(View.GONE);
                break;
            case "friend":
                btnUnfriend.setVisibility(View.VISIBLE);
                btnAcceptt.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
                break;
        }
    }

    private void showConfirmationDialog(Context context, String message, Runnable onConfirm) {
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận")
                .setMessage(message)
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    onConfirm.run(); // Gọi callback nếu đồng ý
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

}
