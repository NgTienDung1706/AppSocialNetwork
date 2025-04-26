package vn.tiendung.socialnetwork.UI;

import android.os.Bundle;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator3;
import vn.tiendung.socialnetwork.Adapter.CommentAdapter;
import vn.tiendung.socialnetwork.Adapter.PostImagesAdapter;
import vn.tiendung.socialnetwork.Model.Comment;
import vn.tiendung.socialnetwork.Model.Post;
import vn.tiendung.socialnetwork.Model.UserProfile;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.Utils.SharedPrefManager;
import vn.tiendung.socialnetwork.ViewModel.PostDetailViewModel;

public class PostDetailActivity extends AppCompatActivity {

    private TextView tvUserName, tvCaption, tvHashtags, tvReactionCount;
    private ImageView ivUserAvatar;
    private ImageButton btnReaction, btnSendComment;
    private EditText etComment;
    private Button btnFollow;
    private RecyclerView rvComments;
    private ViewPager2 vpPostImages;
    private CircleIndicator3 circleIndicator;

    private CommentAdapter commentAdapter;
    private PostDetailViewModel viewModel;

    private String postId;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        postId = getIntent().getStringExtra("postId");
        currentUserId = SharedPrefManager.getInstance(this).getUserId();

        initView();
        setupViewModel();
        observeData();

        viewModel.loadPost(postId, currentUserId);

        // Gắn sự kiện cho nút gửi bình luận
        btnSendComment.setOnClickListener(v -> {
            String content = etComment.getText().toString().trim();
            if (!content.isEmpty()) {
                // Chuyển việc gửi comment sang ViewModel
                if (content.length() > 5000) {
                    Toast.makeText(this, "Nội dung bình luận không được quá 5000 ký tự!", Toast.LENGTH_SHORT).show();
                } else {
                    // Gửi bình luận đi
                    viewModel.createCommentByPostId(postId, currentUserId, content, null);// chọn gửi comment bằng null sẽ làm reply sau
                    etComment.setText("");  // Reset ô nhập comment
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập bình luận", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        tvUserName = findViewById(R.id.tvUserName);
        tvCaption = findViewById(R.id.tvCaption);
        tvHashtags = findViewById(R.id.tvHashtags);
        tvReactionCount = findViewById(R.id.tvReactionCount);
        ivUserAvatar = findViewById(R.id.ivUserAvatar);
        btnReaction = findViewById(R.id.btnReaction);
        etComment = findViewById(R.id.etComment);
        btnFollow = findViewById(R.id.btnFollow);
        rvComments = findViewById(R.id.rvComments);
        vpPostImages = findViewById(R.id.vpPostImages);
        circleIndicator = findViewById(R.id.circleIndicator);
        btnSendComment = findViewById(R.id.btnSendComment);

        rvComments.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(
                this,
                new ArrayList<>(),
                currentUserId,
                (comment, position) -> viewModel.toggleLikeComment(currentUserId, comment, position),
                commentId -> viewModel.deleteComment(commentId)
        );

        rvComments.setAdapter(commentAdapter);
    }


    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(PostDetailViewModel.class);
    }

    private void observeData() {
        viewModel.getPost().observe(this, post -> {
            if (post != null) {
                tvCaption.setText(post.getContent().getCaption());
                tvHashtags.setText(post.getContent().getHashtags().toString());
                tvReactionCount.setText(post.getTotalReactionsCount() + " lượt cảm xúc");
                btnReaction.setImageResource(post.getMyReactionsIcon());

                PostImagesAdapter adapter = new PostImagesAdapter(this, post.getContent().getPictures());
                vpPostImages.setAdapter(adapter);
                circleIndicator.setViewPager(vpPostImages);
            }
        });

        viewModel.getUserProfile().observe(this, user -> {
            if (user != null) {
                tvUserName.setText(user.getFullname());
                Glide.with(this)
                        .load(user.getAvatar())
                        .placeholder(R.drawable.circleusersolid)
                        .error(R.drawable.circleusersolid)
                        .circleCrop()
                        .into(ivUserAvatar);
            }
        });

        viewModel.getComments().observe(this, comments -> {
            if (comments != null) {
                commentAdapter.updateComments(comments);
            }
        });

        viewModel.getErrorMessage().observe(this, msg -> {
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
