package vn.tiendung.socialnetwork.Model.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.tiendung.socialnetwork.Fragment.FriendListFragment;
import vn.tiendung.socialnetwork.Fragment.FriendRequestFragment;
import vn.tiendung.socialnetwork.Fragment.SuggestedFriendFragment;

public class FriendFragmentTabAdapter extends FragmentStateAdapter {

    public FriendFragmentTabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new FriendListFragment(); // Fragment cho "Danh sách bạn"
        } else if (position == 1) {
            return new FriendRequestFragment(); // Fragment cho "Yêu cầu kết bạn"
        } else {
            return new SuggestedFriendFragment(); // Fragment cho "Gợi ý kết bạn"
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Số lượng tab
    }
}

