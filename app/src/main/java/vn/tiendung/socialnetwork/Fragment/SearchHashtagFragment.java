package vn.tiendung.socialnetwork.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.tiendung.socialnetwork.Adapter.FriendListAdapter;
import vn.tiendung.socialnetwork.Adapter.PostAdapter;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.Utils.OnSearchListener;
import vn.tiendung.socialnetwork.ViewModel.UserViewModel;

public class SearchHashtagFragment extends Fragment implements OnSearchListener {
    private RecyclerView recyclerView;

    private UserViewModel userViewModel;

    private PostAdapter adapter;

    private boolean isViewReady = false;
    private String pendingKeyword = null;

    @SuppressLint("MissingInflatedId")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_hashtag, container, false);
        recyclerView = view.findViewById(R.id.recyclerSearchHashtag);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PostAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        userViewModel.getPostList().observe(getViewLifecycleOwner(), posts -> {
            if (posts != null) {
                adapter.updateData(posts); // Cập nhật dữ liệu thay vì tạo adapter mới
            } else {
                Log.e("SearchUserFragment", "Danh sách bài viết trả về null");
            }
        });

        isViewReady = true;

        if (pendingKeyword != null) {
            onSearch(pendingKeyword);
            pendingKeyword = null;
        }


        return view;
    }
    @Override
    public void onSearch(String keyword) {
        if (!isViewReady) {
            pendingKeyword = keyword;
            return;
        }
        userViewModel.searchPosts(keyword);
    }
}
