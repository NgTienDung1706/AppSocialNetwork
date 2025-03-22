package vn.tiendung.socialnetwork.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.Model.Adapter.FriendFragmentTabAdapter;

public class FriendFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FriendFragmentTabAdapter tabAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friend_fragmment, container, false);

        // Khởi tạo các thành phần UI
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        // Thiết lập adapter cho ViewPager2
        tabAdapter = new FriendFragmentTabAdapter(requireActivity());
        viewPager.setAdapter(tabAdapter);

        // Liên kết TabLayout và ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Danh sách bạn");
            } else if (position == 1) {
                tab.setText("Yêu cầu kết bạn");
            } else {
                tab.setText("Gợi ý kết bạn");
            }
        }).attach();

        return view;
    }
}
