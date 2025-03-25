package vn.tiendung.socialnetwork.Fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.tiendung.socialnetwork.Model.Moment;
import vn.tiendung.socialnetwork.Adapter.MomentAdapter;
import vn.tiendung.socialnetwork.Model.Post;
import vn.tiendung.socialnetwork.Adapter.PostAdapter;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.Utils.OnScrollListener;

public class HomeFragment extends Fragment {
    private OnScrollListener scrollListener;

    private ImageButton btnReaction;


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

        PostAdapter postAdapter = new PostAdapter(getContext(), posts);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewPosts.setAdapter(postAdapter);

        // Gán listener từ MainActivity
        if (getActivity() instanceof OnScrollListener) {
            scrollListener = (OnScrollListener) getActivity();
        }

        recyclerViewPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Kiểm tra nếu cuộn xuống (dy > 0) thì ẩn, nếu cuộn lên (dy < 0) thì hiện
                if (scrollListener != null) {
                    scrollListener.onScroll(dy > 0);
                }
            }
        });

        return view;
    }
}

