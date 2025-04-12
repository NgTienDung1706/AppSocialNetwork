package vn.tiendung.socialnetwork.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

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
import vn.tiendung.socialnetwork.UI.LoginActivity;
import vn.tiendung.socialnetwork.Utils.OnScrollListener;
import vn.tiendung.socialnetwork.Utils.SharedPrefManager;

public class ProfileFragment extends Fragment implements OnScrollListener, EditProfileBottomSheet.OnProfileUpdatedListener {
    private OnScrollListener activityScrollListener;
    AppBarLayout appBarLayout ;
    ImageView logout;
    private String username;

    private UserProfile userProfile;
    ImageView ivUpdateProfile;

    private ImageView ivAvatar;
    private TextView tvUsername, tvFriendCount, tvPostCount, tvAboutMe;
    private FlexboxLayout flexboxLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Tạo View trước khi return
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        logout = view.findViewById(R.id.ivLogout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(getActivity()).logout();

                // Chuyển về màn hình Login
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity());
        viewPager.setAdapter(adapter);

        // Liên kết TabLayout với ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Bài viết gần đây");
            } else {
                tab.setText("Album ảnh");
            }
        }).attach();
        // Gửi sự kiện cuộn lên Activity (MainActivity)
        if (getActivity() instanceof OnScrollListener) {
            activityScrollListener = (OnScrollListener) getActivity();
        }

        appBarLayout = view.findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            private int lastOffset = 0;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (activityScrollListener != null) {
                    boolean isScrollingDown = verticalOffset < lastOffset; // Nếu offset nhỏ hơn trước, nghĩa là đang cuộn xuống
                    activityScrollListener.onScroll(isScrollingDown);
                }
                lastOffset = verticalOffset;
            }
        });

        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvFriendCount = view.findViewById(R.id.tvFriendCount);
        tvPostCount = view.findViewById(R.id.tvPostCount);
        tvAboutMe = view.findViewById(R.id.tvAboutMe);
        flexboxLayout = view.findViewById(R.id.flexboxLayout);

        String userId = SharedPrefManager.getInstance(getActivity()).getUserId();
        getUserProfile(userId);

        ivUpdateProfile = view.findViewById(R.id.ivUpdateProfile);
        ivUpdateProfile.setOnClickListener(v -> {
            if (userProfile != null) {
                EditProfileBottomSheet bottomSheet = EditProfileBottomSheet.newInstance(
                        userProfile.getAvatar(),
                        userProfile.getFullname(),
                        userProfile.getBio(),
                        userProfile.getUsername() // Truyền username vào BottomSheet
                );
                bottomSheet.setOnProfileUpdatedListener(this);
                bottomSheet.show(getChildFragmentManager(), "EditProfileBottomSheet");
            } else {
                Toast.makeText(getContext(), "Đang tải dữ liệu, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            }
        });

        return view; // ĐẢM BẢO return SAU KHI setup xong
    }
    @Override
    public void onScroll(boolean isScrollingDown) {
        Log.d("SCROLL_EVENT", "ProfileFragment nhận sự kiện cuộn: " + isScrollingDown);
        if (activityScrollListener != null) {
            activityScrollListener.onScroll(isScrollingDown);
        }
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnScrollListener) {
            activityScrollListener = (OnScrollListener) context;
        }
    }
    private void getUserProfile(String userId) {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);;
        apiService.getUserProfile(userId).enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(@NonNull Call<UserProfile> call, @NonNull Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userProfile = response.body();
                    username = userProfile.getUsername();
                    tvUsername.setText(userProfile.getFullname());
                    tvFriendCount.setText(userProfile.getFriendsCount() + " bạn");
                    tvPostCount.setText(userProfile.getPostsCount() + " bài viết");
                    tvAboutMe.setText(userProfile.getBio().isEmpty() ? "Chạm để viết..." : userProfile.getBio());

                    // Hiển thị ảnh đại diện bằng Glide
                    Glide.with(requireContext())
                            .load(userProfile.getAvatar())
                            .placeholder(R.drawable.avt_default)
                            .into(ivAvatar);

                    // Hiển thị các chủ đề yêu thích
                    flexboxLayout.removeAllViews();
                    for (String topic : userProfile.getFavoriteTags()) {
                        // Tạo TextView
                        TextView tagView = new TextView(getContext());
                        tagView.setText("#" + topic);
                        tagView.setTextSize(14);
                        tagView.setTextColor(ContextCompat.getColor(getContext(), R.color.nd));
                        tagView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.tag));
                        tagView.setOnClickListener(v -> {

                        });
                        // Tạo container để đảm bảo padding
                        FrameLayout container = new FrameLayout(getContext());
                        container.setPadding(16, 8, 16, 8);
                        container.addView(tagView);

                        // LayoutParams cho FlexboxLayout
                        FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(8, 8, 8, 8);
                        container.setLayoutParams(params);

                        flexboxLayout.addView(container);
                    }

                } else {
                    Toast.makeText(getActivity(), "Lỗi khi lấy dữ liệu hồ sơ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfile> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onProfileUpdated() {
        // Load lại thông tin profile khi cập nhật thành công
        String userId = SharedPrefManager.getInstance(getActivity()).getUserId();
        getUserProfile(userId);
    }
}

