package vn.tiendung.socialnetwork.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import vn.tiendung.socialnetwork.Adapter.PostAdapter;
import vn.tiendung.socialnetwork.Adapter.StoryAdapter;
import vn.tiendung.socialnetwork.Model.Post;
import vn.tiendung.socialnetwork.Model.StoryGroup;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.UI.StoryActivity;
import vn.tiendung.socialnetwork.UI.StoryViewActivity;
import vn.tiendung.socialnetwork.UI.MainActivity;
import vn.tiendung.socialnetwork.UI.SearchActivity;
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
    private HomeViewModel viewModel;
    private StoryAdapter storyAdapter;
	private RecyclerView recyclerViewMoments
    private ImageButton btnReaction;

    private ImageView search;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        initViews(view);
        setupViewModel();
        setupRecyclerViews(view);
        assignFunction();
        observeData();

        return view;
    }

    private void initViews(View view) {
        cardAddStory = view.findViewById(R.id.cardAddStory);
        recyclerViewPosts = view.findViewById(R.id.recyclerViewPosts);
        tvNoPosts = view.findViewById(R.id.tvNoPosts);
		recyclerViewMoments = view.findViewById(R.id.recyclerViewMoments);
		search = view.findViewById(R.id.icon_search);
    }

    private void setupRecyclerViews(View view) {
        

        // 💡 Adapter callback truyền full danh sách + vị trí người được click
        storyAdapter = new StoryAdapter(requireContext(), (group, position, allGroups) -> {
            Intent intent = new Intent(requireContext(), StoryViewActivity.class);

            // ✅ Truyền danh sách toàn bộ group + vị trí hiện tại
            intent.putExtra("storiesJson", new Gson().toJson(allGroups));
            intent.putExtra("startPosition", position);

            startActivity(intent);
        });

        recyclerViewMoments.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
 
        recyclerViewMoments.setAdapter(storyAdapter);



        postAdapter = new PostAdapter(getContext(), postList);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewPosts.setAdapter(postAdapter);
    }

    private void assignFunction() {
        cardAddStory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), StoryActivity.class);
            startActivity(intent);
        });

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

    
        
        search.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });

	}
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }
    private void observeData() {
        String userId = SharedPrefManager.getInstance(requireContext()).getUserId();
        viewModel.loadPosts(userId);
        viewModel.loadStories(userId);

        viewModel.getPosts().observe(getViewLifecycleOwner(), posts -> {
            if (posts != null && !posts.isEmpty()) {
                tvNoPosts.setVisibility(View.GONE);
                postList.clear();
                postList.addAll(posts);
                postAdapter.notifyDataSetChanged();
            } else {
                tvNoPosts.setVisibility(View.VISIBLE);
            }
        });


        viewModel.getGroupedStories().observe(getViewLifecycleOwner(), grouped -> {
            storyAdapter.setStoryGroups(grouped);
        });

        viewModel.getStories().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.getStatus()) {
                case LOADING:
                    // Show loading if needed
                    break;
                case SUCCESS:
                    viewModel.setGroupedStories(resource.getData()); // 💡 trigger grouped update
                    break;
                case ERROR:
                    Toast.makeText(getContext(), resource.getMessage(), Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
}
