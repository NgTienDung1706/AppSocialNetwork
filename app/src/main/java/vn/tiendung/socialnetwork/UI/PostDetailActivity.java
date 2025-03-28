package vn.tiendung.socialnetwork.UI;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;
import vn.tiendung.socialnetwork.Adapter.CommentAdapter;
import vn.tiendung.socialnetwork.Adapter.PostImagesAdapter;
import vn.tiendung.socialnetwork.Model.Comment;
import vn.tiendung.socialnetwork.R;

public class PostDetailActivity extends AppCompatActivity {

    private RecyclerView rvComments;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.postDetailActivity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ViewPager2 vpPostImages = findViewById(R.id.vpPostImages);

        // Danh sách ảnh mẫu (thay bằng dữ liệu thực tế)
        List<Integer> imageList = Arrays.asList(
                R.drawable.bell,
                R.drawable.ic_camera,
                R.drawable.usergroupsolid
        );

        PostImagesAdapter adapter = new PostImagesAdapter(this, imageList);
        CircleIndicator3 circleIndicator = findViewById(R.id.circleIndicator);
        vpPostImages.setAdapter(adapter);
        // Liên kết CircleIndicator3 với ViewPager2
        circleIndicator.setViewPager(vpPostImages);

        rvComments = findViewById(R.id.rvComments);
        rvComments.setLayoutManager(new LinearLayoutManager(this));

        // Dữ liệu mẫu
        commentList = new ArrayList<>();
        commentList.add(new Comment(1, "User 1", "Great post!", 10, 0));
        commentList.add(new Comment(2, "User 2", "Amazing content!", 5, 0));
        commentList.add(new Comment(2, "User 2", "Amazing content!", 5, 0));
        commentList.add(new Comment(2, "User 2", "Amazing content!", 5, 0));
        commentList.add(new Comment(2, "User 2", "Amazing content!", 5, 0));
        commentList.add(new Comment(2, "User 2", "Amazing content!", 5, 0));
        commentList.add(new Comment(3, "User 3", "Thanks for sharing.", 3, 0));

        // Gắn adapter
        commentAdapter = new CommentAdapter(commentList);
        rvComments.setAdapter(commentAdapter);



    }
}