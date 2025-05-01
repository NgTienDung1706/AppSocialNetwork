package vn.tiendung.socialnetwork.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import vn.tiendung.socialnetwork.Model.Moment;
import vn.tiendung.socialnetwork.Adapter.MomentAdapter;
import vn.tiendung.socialnetwork.Model.Post;
import vn.tiendung.socialnetwork.Adapter.PostAdapter;

import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.UI.MainActivity;
import vn.tiendung.socialnetwork.UI.SearchActivity;
import vn.tiendung.socialnetwork.Utils.OnScrollListener;
import vn.tiendung.socialnetwork.Utils.SharedPrefManager;

public class HomeFragment extends Fragment {
    private OnScrollListener scrollListener;
    private PostAdapter postAdapter;
    private RecyclerView recyclerViewPosts;
    private List<Post> postList = new ArrayList<>();

    TextView tvNoPosts;
    private ImageButton btnReaction;

    ImageView search;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout của Fragment
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        // Khoảnh khắc
        RecyclerView recyclerViewMoments = view.findViewById(R.id.recyclerViewMoments);
        List<Moment> moments = new ArrayList<>();
        moments.add(new Moment(R.drawable.ic_coin, "Khoảnh khắc 1"));
        moments.add(new Moment(R.drawable.ic_coin, "Khoảnh khắc 2"));
        moments.add(new Moment(R.drawable.ic_coin, "Khoảnh khắc 3"));

        MomentAdapter momentAdapter = new MomentAdapter(moments);
        recyclerViewMoments.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMoments.setAdapter(momentAdapter);

        // Bài đăng
        recyclerViewPosts = view.findViewById(R.id.recyclerViewPosts);
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewPosts.setAdapter(postAdapter);

        // Gán scroll listener
        if (getActivity() instanceof OnScrollListener) {
            scrollListener = (OnScrollListener) getActivity();
        }

        recyclerViewPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (scrollListener != null) {
                    scrollListener.onScroll(dy > 0);
                }
            }
        });
        String userId = SharedPrefManager.getInstance(getActivity()).getUserId();
        loadPostsFromApi(userId);
        tvNoPosts = view.findViewById(R.id.tvNoPosts);
        search = view.findViewById(R.id.icon_search);
        search.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });

        return view;
    }
    private void loadPostsFromApi(String userId) {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<List<Post>> call = apiService.getAllPosts(userId);

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

