package vn.tiendung.socialnetwork.UI;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.relex.circleindicator.CircleIndicator3;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.RetrofitClient;
import vn.tiendung.socialnetwork.Adapter.CommentAdapter;
import vn.tiendung.socialnetwork.Adapter.PostImagesAdapter;
import vn.tiendung.socialnetwork.Model.Comment;
import vn.tiendung.socialnetwork.Model.Post;
import vn.tiendung.socialnetwork.Model.UserProfile;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.Utils.SharedPrefManager;

public class PostDetailActivity extends AppCompatActivity {
    private TextView tvUserName, tvCaption, tvHashtags, tvReactionCount;
    private ImageView ivUserAvatar;
    private RecyclerView rvComments;
    private ViewPager2 vpPostImages;
    private CircleIndicator3 circleIndicator;
    private EditText etComment;
    private Button btnFollow;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private String postId;
    private ImageButton btnReaction;

    private Post postModel;

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
        // lấy thông tin từ view
        AnhXa();

        // Nhận postId từ Intend để loadPost
        loadPostById(postId);

        // Danh sách ảnh mẫu (thay bằng dữ liệu thực tế)
        List<Integer> imageList = Arrays.asList(
                R.drawable.bell,
                R.drawable.ic_camera,
                R.drawable.usergroupsolid
        );



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

    private void AnhXa() {
        postId = getIntent().getStringExtra("postId");
        // Khởi tạo các view từ XML
        btnReaction = findViewById(R.id.btnReaction);
        tvReactionCount = findViewById(R.id.tvReactionCount);
        tvUserName = findViewById(R.id.tvUserName);
        tvCaption = findViewById(R.id.tvCaption);
        tvHashtags = findViewById(R.id.tvHashtags);
        ivUserAvatar = findViewById(R.id.ivUserAvatar);
        rvComments = findViewById(R.id.rvComments);
        rvComments.setLayoutManager(new LinearLayoutManager(this));

        vpPostImages = findViewById(R.id.vpPostImages);

        // Liên kết CircleIndicator3 với ViewPager2
        circleIndicator = findViewById(R.id.circleIndicator);
        circleIndicator.setViewPager(vpPostImages);

        etComment = findViewById(R.id.etComment);
        btnFollow = findViewById(R.id.btnFollow);
    }

    private void loadPostById(String postId) {
        // Khởi tạo Retrofit và APIService
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<Post> call = apiService.getPostById(postId);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful() && response.body() != null) {
                    postModel = response.body();

                    // Cập nhật UI với dữ liệu từ bài viết
                    updateUIWithPost(postModel);
                } else {
                    Toast.makeText(PostDetailActivity.this, "Không tìm thấy bài viết", Toast.LENGTH_SHORT).show();
                }

/*                debug, đừng quan tâm
                try {
                    if (response.errorBody() != null) {
                        Log.e("ERROR_BODY", response.errorBody().string());
                    } else if (response.body() == null) {
                        // Debug: API trả JSON nhưng không parse được
                        Log.e("DEBUG", "Response body null. Có thể do sai định dạng hoặc thiếu field.");

                        BufferedSource source = Okio.buffer(Okio.source(response.raw().body().byteStream()));
                        String rawJson = source.readUtf8();
                        Log.d("RAW_JSON", rawJson);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(PostDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateUIWithPost(Post post) {
        // Cập nhật thông tin người dùng
        loadUserById(post.getUser().get_id());

        // Cập nhật Caption và Hashtags
        tvCaption.setText(post.getContent().getCaption());
        tvHashtags.setText(post.getContent().getHashtags().toString());

        // Cập nhật ảnh bài viết (ViewPager2)
        List<String> imageUrls = post.getContent().getPictures(); // Giả sử post có trường images chứa URL ảnh
        PostImagesAdapter adapter = new PostImagesAdapter(this.getApplicationContext(),imageUrls);
        vpPostImages.setAdapter(adapter);
        circleIndicator.setViewPager(vpPostImages);

        // Cập nhật danh sách bình luận
/*        CommentsAdapter commentsAdapter = new CommentsAdapter(post.get); // Giả sử post có trường comments chứa danh sách bình luận
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(commentsAdapter);*/

        // Cập nhật số lượt cảm xúc Gọi API
        //tvReactionCount.setText(String.valueOf(post.getReactionCount()));

        // Cập nhật trạng thái cảm xúc
        // gọi API và hàm, sửa từ trong ReactionPopupWindow - đem hàm reaction đưa từ postAdatper vào trong đó
        //btnReaction.setImageResource(post.isLiked() ? R.drawable.ic_like_filled : R.drawable.ic_like);

/*        // Cập nhật nút Follow (nếu cần thiết)
        btnFollow.setOnClickListener(v -> {
            // Xử lý sự kiện theo dõi
        });*/
    }
    private void loadUserById(String userId) {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<UserProfile> call = apiService.getUserProfile(userId);

        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile userProfile = response.body();
                    // Hiển thị thông tin user lên giao diện
                    tvUserName.setText(userProfile.getFullname());
                    Glide.with(PostDetailActivity.this)
                            .load(userProfile.getAvatar())
                            .placeholder(R.drawable.circleusersolid) // Avatar mặc định
                            .error(R.drawable.circleusersolid)       // Avatar khi lỗi
                            .circleCrop() // Biến ảnh thành hình tròn
                            .into(ivUserAvatar);
                } else {
                    Toast.makeText(PostDetailActivity.this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Toast.makeText(PostDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

}