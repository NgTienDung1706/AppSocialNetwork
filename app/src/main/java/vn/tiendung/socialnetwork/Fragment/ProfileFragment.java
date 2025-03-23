package vn.tiendung.socialnetwork.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import vn.tiendung.socialnetwork.Adapter.ViewPagerAdapter;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.UI.LoginActivity;
import vn.tiendung.socialnetwork.Utils.OnScrollListener;
import vn.tiendung.socialnetwork.Utils.SharedPrefManager;

public class ProfileFragment extends Fragment implements OnScrollListener{
    private OnScrollListener activityScrollListener;
    AppBarLayout appBarLayout ;
    ImageView logout;
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
}

