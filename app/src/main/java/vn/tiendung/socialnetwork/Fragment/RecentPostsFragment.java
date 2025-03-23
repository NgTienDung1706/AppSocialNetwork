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

import vn.tiendung.socialnetwork.Model.Adapter.PostAdapter;
import vn.tiendung.socialnetwork.Model.Post;
import vn.tiendung.socialnetwork.R;

public class RecentPostsFragment extends Fragment {
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private List<String> postList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recent_posts_fragment, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewPosts);

        List<Post> posts = new ArrayList<>();
        posts.add(new Post(R.drawable.circleusersolid, "Tiến Dũng", "Hôm nay trời đẹp quá!"));
        posts.add(new Post(R.drawable.circleusersolid, "Minh Tâm", "Mình vừa hoàn thành một dự án lớn!"));
        posts.add(new Post(R.drawable.circleusersolid, "Thu Hà", "Cùng đi cafe không mọi người?"));

        adapter = new PostAdapter(getContext(), posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }
}