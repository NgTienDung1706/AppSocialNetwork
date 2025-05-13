package vn.tiendung.socialnetwork.UI;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;
import vn.tiendung.socialnetwork.Adapter.CommentAdapter;
import vn.tiendung.socialnetwork.Adapter.PostImagesAdapter;
import vn.tiendung.socialnetwork.Model.Comment;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.Utils.SharedPrefManager;
import vn.tiendung.socialnetwork.ViewModel.PostDetailViewModel;

public class PostDetailActivity extends AppCompatActivity {

    private TextView tvUserName, tvCaption, tvHashtags, tvReactionCount, tvReplyingUserName;
    private ImageView ivUserAvatar, ivReplyingAvatar;
    private ImageButton btnReaction, btnSendComment, btnCloseReply;
    private NestedScrollView nestedScrollView;
    private EditText etComment;
    private Button btnFollow;
    private RecyclerView rvComments;
    private ConstraintLayout replyingToCommentLayout;
    private ViewPager2 vpPostImages;
    private CircleIndicator3 circleIndicator;
    private CommentAdapter commentAdapter;
    private PostDetailViewModel viewModel;
    private GestureDetector gestureDetector;
    private String postId;
    private String currentUserId;
    private String parentCommentId = null;
    private boolean shouldDismiss = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        postId = getIntent().getStringExtra("postId");
        currentUserId = SharedPrefManager.getInstance(this).getUserId();

        initView();
        setupViewModel();
        observeData();
        assignFunction();

        viewModel.loadPost(postId, currentUserId);
    }

    private void initView() {
        tvUserName = findViewById(R.id.tvUserName);
        tvCaption = findViewById(R.id.tvCaption);
        tvHashtags = findViewById(R.id.tvHashtags);
        tvReactionCount = findViewById(R.id.tvReactionCount);
        tvReplyingUserName = findViewById(R.id.tvReplyingUserName);
        ivUserAvatar = findViewById(R.id.ivUserAvatar);
        ivReplyingAvatar = findViewById(R.id.ivReplyingAvatar);
        btnCloseReply = findViewById(R.id.btnCloseReply);
        btnReaction = findViewById(R.id.btnReaction);
        etComment = findViewById(R.id.etComment);
        btnFollow = findViewById(R.id.btnFollow);
        rvComments = findViewById(R.id.rvComments);
        vpPostImages = findViewById(R.id.vpPostImages);
        circleIndicator = findViewById(R.id.circleIndicator);
        btnSendComment = findViewById(R.id.btnSendComment);
        replyingToCommentLayout = findViewById(R.id.replyingToCommentLayout);
        nestedScrollView = findViewById(R.id.nestedScrollView);

        rvComments.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(
                this,
                new ArrayList<>(),
                currentUserId,
                (comment, position) -> viewModel.toggleLikeComment(currentUserId, comment, position),
                commentId -> viewModel.deleteComment(commentId),
                comment -> {
                    parentCommentId = comment.getId();
                    replyingToCommentLayout.setVisibility(View.VISIBLE);
                    tvReplyingUserName.setText("Trả lời bình luận của @" + comment.getUserName());
                    Glide.with(this)
                            .load(comment.getAvatarUrl())
                            .placeholder(R.drawable.circleusersolid)
                            .error(R.drawable.circleusersolid)
                            .circleCrop()
                            .into(ivReplyingAvatar);
                },
                parentCommentId -> { // scroll đến comment parent
                    int position = commentAdapter.findCommentPositionById(parentCommentId);
                    if (position != -1) {
                        rvComments.post(() -> {
                            scrollCommentIntoView(position);
                        });
                    }
                }
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
                List<Comment> treeComments = viewModel.buildCommentTree(comments);
                List<Comment> flatList = viewModel.flattenCommentTree(treeComments, 0);

                commentAdapter.updateComments(flatList);
            }
        });
        viewModel.getUpdatedCommentLiveData().observe(this, pair -> {
            if (pair != null) {
                Comment updatedComment = pair.first;
                int position = pair.second;
                commentAdapter.updateSingleComment(updatedComment, position);
            }
        });
        viewModel.getDeletedCommentIdLiveData().observe(this, deletedCommentId -> {
            if (deletedCommentId != null) {
                commentAdapter.removeComment(deletedCommentId);
            }
        });


        viewModel.getErrorMessage().observe(this, msg -> {
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assignFunction() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;
            private boolean hasShaked = false;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) return false;

                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();

                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        shouldDismiss = true;
                        if (diffX > 0) {
                            hideReplyingLayout(true);
                        } else {
                            hideReplyingLayout(false);
                        }
                        return true;
                    }
                }
                shouldDismiss = false;
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (e1 == null || e2 == null) return false;

                float diffX = e2.getX() - e1.getX();
                float width = replyingToCommentLayout.getWidth();
                float progress = Math.min(1f, Math.abs(diffX) / width);

                replyingToCommentLayout.setAlpha(1f - progress);

                if (progress > 0.8f && !hasShaked) {
                    shakeView(replyingToCommentLayout);
                    hasShaked = true;
                }
                return false;
            }
        });

        replyingToCommentLayout.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);

            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                if (!shouldDismiss) {
                    replyingToCommentLayout.animate()
                            .alpha(1f)
                            .translationX(0f)
                            .setDuration(200)
                            .start();
                }
            }
            return true;
        });

        btnCloseReply.setOnClickListener(v -> hideReplyingLayout(true));

        btnSendComment.setOnClickListener(v -> {
            String content = etComment.getText().toString().trim();
            if (!content.isEmpty()) {
                if (content.length() > 5000) {
                    Toast.makeText(this, "Nội dung bình luận không được quá 5000 ký tự!", Toast.LENGTH_SHORT).show();
                } else {
                    viewModel.createCommentByPostId(postId, currentUserId, content, parentCommentId);
                    etComment.setText("");
                    replyingToCommentLayout.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(this, "Vui lòng nhập bình luận", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void hideReplyingLayout(boolean toRight) {
        float translationX = toRight ? replyingToCommentLayout.getWidth() : -replyingToCommentLayout.getWidth();

        replyingToCommentLayout.animate()
                .alpha(0f)
                .translationX(translationX)
                .setDuration(300)
                .withEndAction(() -> {
                    replyingToCommentLayout.setVisibility(View.GONE);
                    replyingToCommentLayout.setAlpha(1f);
                    replyingToCommentLayout.setTranslationX(0f);
                })
                .start();
    }
    private void shakeView(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 0, 10, -10, 6, -6, 3, -3, 0);
        animator.setDuration(300);
        animator.start();
    }
    private void scrollCommentIntoView(int position) {
        rvComments.post(() -> {
            RecyclerView.ViewHolder viewHolder = rvComments.findViewHolderForAdapterPosition(position);
            if (viewHolder != null) {
                View itemView = viewHolder.itemView;

                // Tính scrollY cần thiết:
                int rvTopInNestedScrollView = rvComments.getTop(); // Vị trí RecyclerView trong NestedScrollView
                int itemTopInRecyclerView = itemView.getTop();     // Vị trí item trong RecyclerView

                int finalScrollY = rvTopInNestedScrollView + itemTopInRecyclerView - 400; 

                nestedScrollView.smoothScrollTo(0, finalScrollY);  // Scroll tới vị trí comment cha

                highlightCommentAtPosition(position); // Highlight sau scroll
            }
        });
    }

    private void highlightCommentAtPosition(int position) {
        rvComments.smoothScrollToPosition(position);

        rvComments.postDelayed(() -> {
            RecyclerView.ViewHolder viewHolder = rvComments.findViewHolderForAdapterPosition(position);
            if (viewHolder != null) {
                View itemView = viewHolder.itemView;
                int originalColor = itemView.getSolidColor(); // màu gốc nếu có, nếu không lấy sẵn màu background

                itemView.setBackgroundColor(getResources().getColor(R.color.highlightColor)); // đổi sang màu vàng nhạt

                // Sau 1.5s, đổi lại màu nền bình thường
                itemView.postDelayed(() -> {
                    itemView.setBackgroundColor(getResources().getColor(android.R.color.transparent)); // hoặc màu nền gốc
                }, 1500);
            }
        }, 300); // Đợi 300ms để scroll xong mới lấy View
    }

}
