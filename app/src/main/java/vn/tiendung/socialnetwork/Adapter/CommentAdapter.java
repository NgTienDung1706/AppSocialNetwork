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

import vn.tiendung.socialnetwork.Callback.LikeCallback;
import vn.tiendung.socialnetwork.Model.Comment;
import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.Repository.CommentRepository;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> commentList;
    private String currentUserId;

    public interface OnCommentActionListener {
        void onLikeClicked(Comment comment);
    }

    private OnCommentActionListener listener;

    public CommentAdapter(Context context, List<Comment> commentList, String currentUserId, OnCommentActionListener listener) {
        this.context = context;
        // Khởi tạo commentList nếu chưa có dữ liệu
        this.commentList = (commentList != null) ? commentList : new ArrayList<>();
        this.currentUserId = currentUserId;
        this.listener = listener;
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

            // Cài đặt số dòng tối đa cho comment
            holder.tvContent.post(() -> {
                int lineCount = holder.tvContent.getLineCount();
                if (lineCount > 3) {
                    holder.tvToggleContent.setVisibility(View.VISIBLE);
                    holder.tvContent.setMaxLines(3);
                } else {
                    holder.tvToggleContent.setVisibility(View.GONE);
                }
            });

            // Xử lý sự kiện cho nút xem thêm
            holder.tvToggleContent.setOnClickListener(v -> {
                if (holder.tvToggleContent.getText().equals("Xem thêm")) {
                    holder.tvContent.setMaxLines(Integer.MAX_VALUE);
                    holder.tvToggleContent.setText("Thu gọn");
                } else {
                    holder.tvContent.setMaxLines(3);
                    holder.tvToggleContent.setText("Xem thêm");
                }
            });


            Glide.with(context)
                    .load(comment.getAvatarUrl())
                    .circleCrop()
                    .placeholder(R.drawable.circleusersolid)
                    .into(holder.imgAvatar);

            if (comment.isMyLike()) {
                holder.btnLike.setImageResource(R.drawable.ic_comment_heart); // icon fill
            } else {
                holder.btnLike.setImageResource(R.drawable.ic_comment_outline_heart); // icon mặc định
            }

            holder.btnLike.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLikeClicked(comment);  // xử lý logic
                }

                boolean isLiked = comment.isMyLike();
                comment.setMyLike(!isLiked);

                if (comment.isMyLike()) {
                    comment.getLikes().add(currentUserId);
                    new CommentRepository().likeComment(comment.getId(), currentUserId, new LikeCallback(comment.getId()));
                } else {
                    comment.getLikes().remove(currentUserId);
                    new CommentRepository().unlikeComment(comment.getId(), currentUserId, new LikeCallback(comment.getId()));
                }

                notifyItemChanged(holder.getAdapterPosition());
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

    private void showBottomSheet(Comment comment) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog((Activity) context);
        View sheetView = LayoutInflater.from(context).inflate(R.layout.comment_options_bottom_sheet, null);
        bottomSheetDialog.setContentView(sheetView);

        TextView tvDelete = sheetView.findViewById(R.id.tvDeleteComment);
        TextView tvCancel = sheetView.findViewById(R.id.tvCancel);

        tvDelete.setOnClickListener(v -> {
            // Xóa comment trong database
            deleteCommentFromDatabase(comment);

            // Cập nhật lại RecyclerView
            removeCommentFromList(comment);

            // Đóng bottomSheet
            bottomSheetDialog.dismiss();
        });

        tvCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

    private void deleteCommentFromDatabase(Comment comment) {
        // Logic để xóa comment khỏi database (API, hoặc local storage)
    }

    private void removeCommentFromList(Comment comment) {
        // Xóa comment trong danh sách và cập nhật RecyclerView
        commentList.remove(comment);
        notifyDataSetChanged();
    }
    public void updateComments(List<Comment> newComments) {
        if (this.commentList == null) {
            this.commentList = new ArrayList<>();
        }
        this.commentList.clear();
        this.commentList.addAll(newComments);
        notifyDataSetChanged();  // Cập nhật RecyclerView
    }


    private String formatTime(String createdAt) {
        // TODO: Chuyển thời gian ISO thành định dạng dễ đọc (VD: "3 phút trước")
        return "Vài phút trước"; // Placeholder tạm
    }
}
