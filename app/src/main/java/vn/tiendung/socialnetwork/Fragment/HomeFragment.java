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

import vn.tiendung.socialnetwork.Model.Moment;
import vn.tiendung.socialnetwork.Model.Adapter.MomentAdapter;
import vn.tiendung.socialnetwork.Model.Post;
import vn.tiendung.socialnetwork.Model.Adapter.PostAdapter;
import vn.tiendung.socialnetwork.R;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout của Fragment
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        // Danh sách khoảnh khắc
        RecyclerView recyclerViewMoments = view.findViewById(R.id.recyclerViewMoments);
        List<Moment> moments = new ArrayList<>();
        moments.add(new Moment(R.drawable.ic_coin, "Khoảnh khắc 1"));
        moments.add(new Moment(R.drawable.ic_coin, "Khoảnh khắc 2"));
        moments.add(new Moment(R.drawable.ic_coin, "Khoảnh khắc 3"));

        MomentAdapter momentAdapter = new MomentAdapter(moments);
        recyclerViewMoments.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewMoments.setAdapter(momentAdapter);

        // Danh sách bài đăng
        RecyclerView recyclerViewPosts = view.findViewById(R.id.recyclerViewPosts);
        List<Post> posts = new ArrayList<>();
        posts.add(new Post(R.drawable.circleusersolid, "Tiến Dũng", "Hôm nay trời đẹp quá!"));
        posts.add(new Post(R.drawable.circleusersolid, "Minh Tâm", "Mình vừa hoàn thành một dự án lớn!"));
        posts.add(new Post(R.drawable.circleusersolid, "Thu Hà", "Cùng đi cafe không mọi người?"));

        PostAdapter postAdapter = new PostAdapter(posts);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewPosts.setAdapter(postAdapter);

        return view;
    }
}

