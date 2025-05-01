package vn.tiendung.socialnetwork.Repository;

import androidx.annotation.NonNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.RetrofitClient;
import vn.tiendung.socialnetwork.Callback.CommentDeleteCallback;
import vn.tiendung.socialnetwork.Callback.CommentLikeCallback;
import vn.tiendung.socialnetwork.Callback.CommentPostCallback;
import vn.tiendung.socialnetwork.Model.Comment;

public class CommentRepository {

    private final APIService apiService;

    public interface CommentCallback {
        void onSuccess(List<Comment> comments);
        void onError(String message);
    }

    public CommentRepository() {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
    }

    public void getCommentsByPostId(String postId, String userId, CommentCallback callback) {
        Call<List<Comment>> call = apiService.getCommentsByPostId(postId, userId);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Comment>> call, @NonNull Response<List<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không tìm thấy comment");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Comment>> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    public void likeComment(String commentId, String userId, CommentLikeCallback callback) {
        apiService.likeCommentByCommentId(commentId, userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Like thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }


    public void unlikeComment(String commentId, String userId, CommentLikeCallback callback) {
        apiService.unlikeCommentByCommentId(commentId, userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Bỏ like thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void createCommentByPostId(String postId, Comment comment, CommentPostCallback callback) {
        apiService.createCommentByPostId(postId, comment).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể gửi bình luận: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    public void deleteCommentByCommentId(String commentId, CommentDeleteCallback callback) {
        apiService.deleteCommentByCommentId(commentId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Xóa thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

}
