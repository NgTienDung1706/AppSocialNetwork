package vn.tiendung.socialnetwork.Adapter;

import static androidx.constraintlayout.widget.StateSet.TAG;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import vn.tiendung.socialnetwork.Model.Comment;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.Utils.TimeUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> commentList;
    private String currentUserId;
    private Map<String, Integer> commentPositionMap = new HashMap<>();

    public interface OnCommentActionListener {
        void onLikeClicked(Comment comment, int position);
    }

    public interface OnCommentDeleteListener {
        void onDelete(String commentId);
    }
    public interface OnReplyClickListener {
        void onReplyClicked(Comment comment);
    }
    public interface OnNavigateToParentListener {
        void onNavigateToParent(String parentCommentId);
    }
    private OnCommentActionListener likeListener;
    private OnCommentDeleteListener deleteListener;
    private OnReplyClickListener replyClickListener;
    private OnNavigateToParentListener navigateListener;
    public CommentAdapter(Context context, List<Comment> commentList, String currentUserId,
                          OnCommentActionListener likeListener,
                          OnCommentDeleteListener deleteListener,
                          OnReplyClickListener replyClickListener,
                          OnNavigateToParentListener navigateListener) {
        this.context = context;
        this.commentList = commentList != null ? commentList : new ArrayList<>();
        this.currentUserId = currentUserId;
        this.likeListener = likeListener;
        this.deleteListener = deleteListener;
        this.replyClickListener = replyClickListener;
        this.navigateListener = navigateListener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        // Cập nhật lại vị trí trong map
        commentPositionMap.put(comment.getId(), position);
        if (comment.isDeleted()) {
            holder.tvDeleted.setVisibility(View.VISIBLE);
            holder.tvContent.setVisibility(View.GONE);
            holder.layoutLike.setVisibility(View.GONE);
            holder.tvUserName.setVisibility(View.GONE);
        } else {
            holder.tvDeleted.setVisibility(View.GONE);
            holder.tvUserName.setVisibility(View.VISIBLE);
            holder.tvContent.setVisibility(View.VISIBLE);
            holder.layoutLike.setVisibility(View.VISIBLE);

            holder.tvUserName.setText(comment.getUserName());
            holder.tvContent.setText(comment.getContent());
            holder.tvLikeCount.setText(String.valueOf(comment.getLikes().size()));
            holder.tvTime.setText(TimeUtils.formatRelativeTime(comment.getCreatedAt()));

            holder.tvContent.post(() -> {
                int lineCount = holder.tvContent.getLineCount();
                if (lineCount > 5) {
                    holder.tvToggleContent.setVisibility(View.VISIBLE);
                    holder.tvContent.setMaxLines(5);
                } else {
                    holder.tvToggleContent.setVisibility(View.GONE);
                }
            });

            holder.tvToggleContent.setOnClickListener(v -> {
                boolean expanded = holder.tvContent.getMaxLines() == Integer.MAX_VALUE;
                holder.tvContent.setMaxLines(expanded ? 5 : Integer.MAX_VALUE);
                holder.tvToggleContent.setText(expanded ? "Xem thêm" : "Thu gọn");
            });

            holder.tvReply.setOnClickListener(v -> {
                if (replyClickListener != null) {
                    replyClickListener.onReplyClicked(comment);
                }
            });

            holder.itemView.setOnClickListener(v -> {
                if (replyClickListener != null) {
                    replyClickListener.onReplyClicked(comment);
                }
            });

            Glide.with(context)
                    .load(comment.getAvatarUrl())
                    .circleCrop()
                    .placeholder(R.drawable.circleusersolid)
                    .into(holder.imgAvatar);

            holder.btnLike.setImageResource(
                    comment.isMyLike() ? R.drawable.ic_comment_heart : R.drawable.ic_comment_outline_heart);

            holder.btnLike.setOnClickListener(v -> {
                if (likeListener != null) likeListener.onLikeClicked(comment, holder.getAdapterPosition());
            });

            holder.itemView.setOnLongClickListener(v -> {
                if (currentUserId.equals(comment.getUserId())) {
                    showBottomSheet(comment);
                }
                return true;
            });

            // 👉 Nếu comment có parent
            if (comment.getParentUserName() != null) {
                holder.tvReplyToUser.setVisibility(View.VISIBLE);
                holder.tvReplyToUser.setText("➔ @" + comment.getParentUserName());

                holder.tvReplyToUser.setOnClickListener(v -> {
                    if (navigateListener != null) {
                        navigateListener.onNavigateToParent(comment.getParent());
                    }
                });

            } else {
                holder.tvReplyToUser.setVisibility(View.GONE);
            }

            //  phần chỉnh marginStart theo depth
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
            int marginStart = dpToPx(context, 24 * comment.getDepth()); // mỗi cấp độ thụt vào 24dp
            params.setMarginStart(marginStart);
            holder.itemView.setLayoutParams(params);
        }
    }
    private int dpToPx(Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }
    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty() && payloads.contains("LIKE_UPDATE")) {
            Comment comment = commentList.get(position);

            // ✅ Chỉ cập nhật phần like
            holder.btnLike.setImageResource(
                    comment.isMyLike() ? R.drawable.ic_comment_heart : R.drawable.ic_comment_outline_heart
            );
            holder.tvLikeCount.setText(String.valueOf(comment.getLikes().size()));

            Log.d(TAG, "Payload LIKE_UPDATE applied at position: " + position);

        } else {
            // Không có payload -> cập nhật toàn bộ
            super.onBindViewHolder(holder, position, payloads);
        }
    }



    @Override
    public int getItemCount() {
        return commentList.size();
    }

    private void showBottomSheet(Comment comment) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog((Activity) context);
        View sheetView = LayoutInflater.from(context).inflate(R.layout.comment_options_bottom_sheet, null);
        bottomSheetDialog.setContentView(sheetView);

        TextView tvDelete = sheetView.findViewById(R.id.tvDeleteComment);
        TextView tvCancel = sheetView.findViewById(R.id.tvCancel);

        tvDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(comment.getId());
            }
            bottomSheetDialog.dismiss();
        });

        tvCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

/*    public void updateComments(List<Comment> newComments) {
        commentList = new ArrayList<>(newComments);
        notifyDataSetChanged();
    }*/

    public void updateComments(List<Comment> newComments) {
        if (newComments == null) return;

        for (Comment newComment : newComments) {
            Integer position = commentPositionMap.get(newComment.getId());

            if (position != null) {
                // Nếu bình luận đã tồn tại, cập nhật nội dung
            commentList.set(position, newComment);
            notifyItemChanged(position);
            } else {
                // Nếu là bình luận mới, thêm vào cuối danh sách
                commentList.add(newComment);
                int newIndex = commentList.size() - 1;
                commentPositionMap.put(newComment.getId(), newIndex);
                notifyItemInserted(newIndex);
            }
        }
    }

    public void updateSingleComment(Comment updatedComment, int position) {
        if (position >= 0 && position < commentList.size()) {
            commentList.set(position, updatedComment);
            notifyItemChanged(position, "LIKE_UPDATE");
            Log.d(TAG, "Updated comment at position: " + position);
        }
    }


    public int findCommentPositionById(String commentId) {
        for (int i = 0; i < commentList.size(); i++) {
            if (commentList.get(i).getId().equals(commentId)) {
                return i;
            }
        }
        return -1; // Không tìm thấy
    }
    public void removeComment(String commentId) {
        Integer position = commentPositionMap.get(commentId);

        if (position != null) {
            commentList.remove((int) position);
            notifyItemRemoved(position);
            commentPositionMap.remove(commentId);

            // Cập nhật lại vị trí trong commentPositionMap
            for (int i = position; i < commentList.size(); i++) {
                commentPositionMap.put(commentList.get(i).getId(), i);
            }
        } else {
            Log.w(TAG, "Comment not found for removal: " + commentId);
        }
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvUserName, tvContent, tvTime, tvLikeCount, tvDeleted, tvToggleContent, tvReply, tvReplyToUser;
        ImageButton btnLike;
        View layoutLike;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvReply = itemView.findViewById(R.id.tvReply);
            tvReplyToUser = itemView.findViewById(R.id.tvReplyToUser);
            tvToggleContent = itemView.findViewById(R.id.tvToggleContent);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvDeleted = itemView.findViewById(R.id.tvDeletedComment);
            btnLike = itemView.findViewById(R.id.btnLike);
            layoutLike = itemView.findViewById(R.id.layoutLike);
        }
    }
}