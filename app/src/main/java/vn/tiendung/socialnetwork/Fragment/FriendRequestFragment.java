package vn.tiendung.socialnetwork.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import vn.tiendung.socialnetwork.Adapter.FriendAddAdapter;
import vn.tiendung.socialnetwork.Model.Friend;
import vn.tiendung.socialnetwork.R;

public class FriendRequestFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Dữ liệu mẫu

//        List<Friend> friends = new ArrayList<>();
//        friends.add(new Friend("Nguyễn Văn A", R.drawable.circleusersolid, 31, Friend.TYPE_REQUEST));
//        friends.add(new Friend("Hoàng C", R.drawable.circleusersolid, 48, Friend.TYPE_REQUEST));
//        friends.add(new Friend("Hoàng C", R.drawable.circleusersolid, 48, Friend.TYPE_REQUEST));
//        friends.add(new Friend("Hoàng C", R.drawable.circleusersolid, 48, Friend.TYPE_REQUEST));
//        friends.add(new Friend("Hoàng C", R.drawable.circleusersolid, 48, Friend.TYPE_REQUEST));
//        friends.add(new Friend("Hoàng C", R.drawable.circleusersolid, 48, Friend.TYPE_REQUEST));
//        friends.add(new Friend("Hoàng C", R.drawable.circleusersolid, 48, Friend.TYPE_REQUEST));
//
//        // Thiết lập adapter
//        RecyclerView.Adapter adapter = new FriendAddAdapter(getContext(), friends);
//        recyclerView.setAdapter(adapter);

        return view;
    }
}