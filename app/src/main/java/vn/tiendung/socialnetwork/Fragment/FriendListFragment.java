package vn.tiendung.socialnetwork.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.tiendung.socialnetwork.Adapter.FriendListAdapter;
import vn.tiendung.socialnetwork.Model.Friend;
import vn.tiendung.socialnetwork.R;

public class FriendListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Dữ liệu mẫu
//        List<Friend> friends = new ArrayList<>();
//        friends.add(new Friend("Thao Pham", 15, R.drawable.circleusersolid,true));
//        friends.add(new Friend("Nguyen Van A", 10, R.drawable.circleusersolid,true));
//        friends.add(new Friend("Tran Thi B", 20, R.drawable.circleusersolid,true));
//        friends.add(new Friend("Tran Thi B", 20, R.drawable.circleusersolid,true));
//        friends.add(new Friend("Tran Thi B", 20, R.drawable.circleusersolid,true));
//        friends.add(new Friend("Tran Thi B", 20, R.drawable.circleusersolid,true));
//        // Thiết lập adapter
//        RecyclerView.Adapter adapter = new FriendListAdapter(getContext(),friends);
//        recyclerView.setAdapter(adapter);

        return view;
    }
}
