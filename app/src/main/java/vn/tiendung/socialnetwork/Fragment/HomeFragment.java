package vn.tiendung.socialnetwork.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.tiendung.socialnetwork.Adapter.MomentAdapter;
import vn.tiendung.socialnetwork.Adapter.PostAdapter;
import vn.tiendung.socialnetwork.Model.Moment;
import vn.tiendung.socialnetwork.Model.Post;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.UI.StoryActivity;
import vn.tiendung.socialnetwork.Utils.OnScrollListener;
import vn.tiendung.socialnetwork.Utils.SharedPrefManager;
import vn.tiendung.socialnetwork.ViewModel.HomeViewModel;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewPosts;
    private PostAdapter postAdapter;
    private List<Post> postList = new ArrayList<>();
    private TextView tvNoPosts;
    private CardView cardAddStory;
    private OnScrollListener scrollListener;
    private HomeViewModel homeViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        initViews(view);
        setupViewModel();
        observeData();
        assignFunction();

        return view;
    }

    private void initViews(View view) {
        cardAddStory = view.findViewById(R.id.cardAddStory);
        recyclerViewPosts = view.findViewById(R.id.recyclerViewPosts);
        tvNoPosts = view.findViewById(R.id.tvNoPosts);
        setupRecyclerViews(view);
    }

    private void setupRecyclerViews(View view) {
        // Moments
        RecyclerView recyclerViewMoments = view.findViewById(R.id.recyclerViewMoments);
        List<Moment> moments = new ArrayList<>();
        moments.add(new Moment(R.drawable.ic_coin, "Khoảnh khắc 1"));
        moments.add(new Moment(R.drawable.ic_coin, "Khoảnh khắc 2"));
        moments.add(new Moment(R.drawable.ic_coin, "Khoảnh khắc 3"));
        MomentAdapter momentAdapter = new MomentAdapter(moments);
        recyclerViewMoments.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMoments.setAdapter(momentAdapter);

        // Posts
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewPosts.setAdapter(postAdapter);
    }

    private void assignFunction() {
        cardAddStory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), StoryActivity.class);
            startActivity(intent);
        });

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
    }

    private void setupViewModel() {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }
    private void observeData(){
        String userId = SharedPrefManager.getInstance(requireContext()).getUserId();
        homeViewModel.loadPosts(userId);

        homeViewModel.getPosts().observe(getViewLifecycleOwner(), posts -> {
            if (posts != null && !posts.isEmpty()) {
                tvNoPosts.setVisibility(View.GONE);
                postList.clear();
                postList.addAll(posts);
                postAdapter.notifyDataSetChanged();
            } else {
                tvNoPosts.setVisibility(View.VISIBLE);
            }
        });
    }
}
