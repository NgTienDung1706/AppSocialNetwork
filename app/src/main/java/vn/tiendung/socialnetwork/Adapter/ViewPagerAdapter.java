package vn.tiendung.socialnetwork.Adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.tiendung.socialnetwork.Fragment.AlbumFragment;
import vn.tiendung.socialnetwork.Fragment.RecentPostsFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final String userId;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, String userId) {
        super(fragmentActivity);
        this.userId = userId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);

        Fragment fragment;
        if (position == 0) {
            fragment = new RecentPostsFragment();
        } else {
            fragment = new AlbumFragment();
        }

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
