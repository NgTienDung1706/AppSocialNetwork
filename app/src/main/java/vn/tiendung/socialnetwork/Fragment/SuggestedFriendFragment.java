package vn.tiendung.socialnetwork.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.RetrofitClient;
import vn.tiendung.socialnetwork.Adapter.FriendAddAdapter;
import vn.tiendung.socialnetwork.Model.Friend;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.Utils.SharedPrefManager;

public class SuggestedFriendFragment extends Fragment {

    private RecyclerView recyclerView;
    private FriendAddAdapter adapter;
    private List<Friend> suggestedList = new ArrayList<>();

    private APIService apiService;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suggested_friend, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Dữ liệu mẫu
//        List<Friend> friends = new ArrayList<>();
//        friends.add(new Friend("Nguyễn Văn A", R.drawable.circleusersolid, 31, Friend.TYPE_SUGGEST));
//        friends.add(new Friend("Hoàng C", R.drawable.circleusersolid, 48, Friend.TYPE_SUGGEST));
//        friends.add(new Friend("Hoàng C", R.drawable.circleusersolid, 48, Friend.TYPE_SUGGEST));
//        friends.add(new Friend("Hoàng C", R.drawable.circleusersolid, 48, Friend.TYPE_SUGGEST));
//        friends.add(new Friend("Hoàng C", R.drawable.circleusersolid, 48, Friend.TYPE_SUGGEST));
//        friends.add(new Friend("Hoàng C", R.drawable.circleusersolid, 48, Friend.TYPE_SUGGEST));
//        friends.add(new Friend("Hoàng C", R.drawable.circleusersolid, 48, Friend.TYPE_SUGGEST));
//
//        // Thiết lập adapter
//        RecyclerView.Adapter adapter = new FriendAddAdapter(getContext(), friends);
//        recyclerView.setAdapter(adapter);

        userId = SharedPrefManager.getInstance(getActivity()).getUserId();
        apiService = RetrofitClient.getRetrofit().create(APIService.class);

        loadSuggestedFriends();

        return view;
    }

    private void loadSuggestedFriends() {
        apiService.getSuggestedFriends(userId).enqueue(new Callback<List<Friend>>() {
            @Override
            public void onResponse(Call<List<Friend>> call, Response<List<Friend>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    suggestedList = response.body();

                    // Gán type là TYPE_SUGGEST nếu chưa có trong dữ liệu
//                    for (Friend friend : suggestedList) {
//                        friend.setType(Friend.TYPE_SUGGEST);
//                    }

                    adapter = new FriendAddAdapter(getContext(), suggestedList, apiService, userId, SuggestedFriendFragment.this);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Không thể tải gợi ý kết bạn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Friend>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
