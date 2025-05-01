package vn.tiendung.socialnetwork.Fragment;

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
import java.util.List;

import vn.tiendung.socialnetwork.Adapter.FriendListAdapter;
import vn.tiendung.socialnetwork.Model.Friend;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.Utils.OnSearchListener;
import vn.tiendung.socialnetwork.ViewModel.UserViewModel;

public class SearchUserFragment extends Fragment implements OnSearchListener {
    private RecyclerView recyclerView;
    private UserViewModel userViewModel;

    private FriendListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_user, container, false);
        recyclerView = view.findViewById(R.id.recyclerSearchUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FriendListAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        userViewModel.getFriendList().observe(getViewLifecycleOwner(), friends -> {
            if (friends != null) {
                adapter.updateData(friends); // Cập nhật dữ liệu thay vì tạo adapter mới
            } else {
                Log.e("SearchUserFragment", "Danh sách bạn bè trả về null");
            }
        });

        return view;
    }
    @Override
    public void onSearch(String keyword) {
    Log.d("SearchUserFragment", "onSearch called with keyword: " + keyword);
        userViewModel.searchFriends(keyword);
    }
}
