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
    private String postId, userId;
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
        // Nhận userId từ SharePrefManager
        loadPostById(postId, userId);

    }

    private void AnhXa() {
        postId = getIntent().getStringExtra("postId");
        userId = SharedPrefManager.getInstance(getApplicationContext()).getUserId();
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

    private void loadPostById(String postId, String userId) {
        // Khởi tạo Retrofit và APIService
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<Post> call = apiService.getPostById(postId, userId);

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
        List<String> imageUrls = post.getContent().getPictures();
        PostImagesAdapter adapter = new PostImagesAdapter(this.getApplicationContext(), imageUrls);
        vpPostImages.setAdapter(adapter);
        circleIndicator.setViewPager(vpPostImages);

        // Cập nhật số lượt cảm xúc
        tvReactionCount.setText(String.valueOf(post.getTotalReactionsCount() + " lượt cảm xúc"));

        // Cập nhật trạng thái cảm xúc
        btnReaction.setImageResource(post.getMyReactionsIcon());

        // Khởi tạo commentAdapter trước khi cập nhật
        if (commentAdapter == null) {
            commentAdapter = new CommentAdapter(this, new ArrayList<>(), userId, new CommentAdapter.OnCommentActionListener() {
                @Override
                public void onLikeClicked(Comment comment) {
                    // Xử lý khi người dùng bấm like
                    // Ví dụ: Thực hiện update like cho comment
                    //updateLikeForComment(comment);
                }
            });
        }

        // Cập nhật danh sách bình luận
        loadComments(postId);  // Lấy bình luận từ API và cập nhật Adapter

        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(commentAdapter);  // Cập nhật adapter vào RecyclerView
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
    private void loadComments(String postId) {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<List<Comment>> call = apiService.getCommentsByPostId(postId,userId);

        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API Response", "Comments: " + response.body().toString());
                    commentList = response.body();
                    commentAdapter.updateComments(commentList);  // Cập nhật Adapter với dữ liệu mới
                } else {
                    Log.d("API Response", "No comments found or error: " + response.message());
                    commentList = new ArrayList<>();  // Đảm bảo commentList không null
                    Toast.makeText(PostDetailActivity.this, "Không tìm thấy comment", Toast.LENGTH_SHORT).show();
                    commentAdapter.updateComments(commentList);  // Cập nhật Adapter với danh sách rỗng
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Toast.makeText(PostDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                Log.e("loadComments", "Error: ", t);  // Ghi lại lỗi
            }
        });
    }

}