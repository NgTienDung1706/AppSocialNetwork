package vn.tiendung.socialnetwork.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import vn.tiendung.socialnetwork.Adapter.PostAdapter;
import vn.tiendung.socialnetwork.Model.Post;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.Utils.OnScrollListener;
import vn.tiendung.socialnetwork.Utils.SharedPrefManager;

public class RecentPostsFragment extends Fragment {
    private OnScrollListener scrollListener;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList = new ArrayList<>();

    TextView tvNoPosts;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recent_posts_fragment, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewPosts);

        // Gửi sự kiện lên ProfileFragment
        if (getActivity() instanceof OnScrollListener) {
            scrollListener = (OnScrollListener) getActivity();
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("SCROLL_EVENT", "RecyclerView đang cuộn: dy = " + dy);
                if (scrollListener != null) {
                    scrollListener.onScroll(dy > 0);
                }
            }
        });
        // Bài đăng
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(postAdapter);

        String userId = SharedPrefManager.getInstance(getActivity()).getUserId();
        loadPostsFromApi(userId);
        tvNoPosts = view.findViewById(R.id.tvNoPosts);

        return view;
    }
    private void loadPostsFromApi(String userId) {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<List<Post>> call = apiService.getMyPosts(userId);

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    postList.clear();
                    postList.addAll(response.body());
                    postAdapter.notifyDataSetChanged();
                    // Ẩn hoặc hiện TextView tùy vào danh sách
                    if (postList.isEmpty()) {
                        tvNoPosts.setVisibility(View.VISIBLE);
                    } else {
                        tvNoPosts.setVisibility(View.GONE);
                    }
                } else {
                    tvNoPosts.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}