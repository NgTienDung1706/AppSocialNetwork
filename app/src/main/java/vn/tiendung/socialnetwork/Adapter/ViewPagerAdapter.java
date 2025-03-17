package vn.tiendung.socialnetwork.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.tiendung.socialnetwork.Fragment.AlbumFragment;
import vn.tiendung.socialnetwork.Fragment.RecentPostsFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new RecentPostsFragment(); // Tab 1
            case 1:
                return new AlbumFragment(); // Tab 2
            default:
                return new RecentPostsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Tổng số tab
    }
}
