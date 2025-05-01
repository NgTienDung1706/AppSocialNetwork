package vn.tiendung.socialnetwork.UI;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import vn.tiendung.socialnetwork.Adapter.SearchAdapter;
import vn.tiendung.socialnetwork.Fragment.SearchHashtagFragment;
import vn.tiendung.socialnetwork.Fragment.SearchUserFragment;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.Utils.OnSearchListener;

public class SearchActivity extends AppCompatActivity {

    private final Handler handler = new Handler();
    private Runnable searchRunnable;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private EditText edtSearch;
    private SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        tabLayout = findViewById(R.id.tabLayoutSearch);
        viewPager = findViewById(R.id.viewPagerSearch);
        edtSearch = findViewById(R.id.edtSearch);

        adapter = new SearchAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "Tìm bạn bè" : "Hashtag");
        }).attach();

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();

                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }

                searchRunnable = () -> sendKeywordToCurrentFragment(keyword);
                handler.postDelayed(searchRunnable, 500); // 500ms debounce

                //sendKeywordToCurrentFragment(keyword);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                String keyword = edtSearch.getText().toString().trim();
                sendKeywordToCurrentFragment(keyword);
            }
        });
    }

    private void sendKeywordToCurrentFragment(String keyword) {
        int position = viewPager.getCurrentItem();
        Log.d("SearchActivity", "Current tab position: " + position);
    if (position == 0) {
        SearchUserFragment userFragment = adapter.getUserFragment();
        if (userFragment != null) {
            userFragment.onSearch(keyword);
        } else {
            Log.e("SearchActivity", "UserFragment is null");
        }
    } else {
        SearchHashtagFragment hashtagFragment = adapter.getHashtagFragment();
        if (hashtagFragment != null) {
            hashtagFragment.onSearch(keyword);
        } else {
            Log.e("SearchActivity", "HashtagFragment is null");
        }
    }
    }
}
