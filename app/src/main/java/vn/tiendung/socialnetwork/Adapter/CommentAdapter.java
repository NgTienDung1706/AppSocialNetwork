package vn.tiendung.socialnetwork.Adapter;

import android.app.Activity;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> commentList;
    private String currentUserId;

    public interface OnCommentActionListener {
        void onLikeClicked(Comment comment, int position);
    }

    public interface OnCommentDeleteListener {
        void onDelete(String commentId);
    }

    private OnCommentActionListener likeListener;
    private OnCommentDeleteListener deleteListener;

    public CommentAdapter(Context context, List<Comment> commentList, String currentUserId,
                          OnCommentActionListener likeListener,
                          OnCommentDeleteListener deleteListener) {
        this.context = context;
        this.commentList = commentList != null ? commentList : new ArrayList<>();
        this.currentUserId = currentUserId;
        this.likeListener = likeListener;
        this.deleteListener = deleteListener;
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
            holder.tvTime.setText(formatTime(comment.getCreatedAt()));

            holder.tvContent.post(() -> {
                int lineCount = holder.tvContent.getLineCount();
                if (lineCount > 3) {
                    holder.tvToggleContent.setVisibility(View.VISIBLE);
                    holder.tvContent.setMaxLines(3);
                } else {
                    holder.tvToggleContent.setVisibility(View.GONE);
                }
            });

            holder.tvToggleContent.setOnClickListener(v -> {
                boolean expanded = holder.tvContent.getMaxLines() == Integer.MAX_VALUE;
                holder.tvContent.setMaxLines(expanded ? 3 : Integer.MAX_VALUE);
                holder.tvToggleContent.setText(expanded ? "Xem thêm" : "Thu gọn");
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

    public void updateComments(List<Comment> newComments) {
        commentList = new ArrayList<>(newComments);
        notifyDataSetChanged();
    }

    public void updateSingleComment(Comment updatedComment, int position) {
        commentList.set(position, updatedComment);
        notifyItemChanged(position);
    }

    private String formatTime(String createdAt) {
        return "Vài phút trước"; // Placeholder, có thể dùng thư viện time sau
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvUserName, tvContent, tvTime, tvLikeCount, tvDeleted, tvToggleContent;
        ImageButton btnLike;
        View layoutLike;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvToggleContent = itemView.findViewById(R.id.tvToggleContent);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvDeleted = itemView.findViewById(R.id.tvDeletedComment);
            btnLike = itemView.findViewById(R.id.btnLike);
            layoutLike = itemView.findViewById(R.id.layoutLike);
        }
    }
}