package vn.tiendung.socialnetwork.Adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.tiendung.socialnetwork.Fragment.SearchHashtagFragment;
import vn.tiendung.socialnetwork.Fragment.SearchUserFragment;

public class SearchAdapter extends FragmentStateAdapter {

    private final SearchUserFragment userFragment = new SearchUserFragment();
    private final SearchHashtagFragment hashtagFragment = new SearchHashtagFragment();

    public SearchAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) return userFragment;
        else return hashtagFragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public SearchUserFragment getUserFragment() {
        return userFragment;
    }

    public SearchHashtagFragment getHashtagFragment() {
        return hashtagFragment;
    }
}
