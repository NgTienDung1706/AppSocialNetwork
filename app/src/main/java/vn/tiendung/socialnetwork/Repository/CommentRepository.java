package vn.tiendung.socialnetwork.Repository;

import androidx.annotation.NonNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.RetrofitClient;
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
    public void likeComment(String commentId, String userId, Callback<Void> callback) {
        apiService.likeCommentByCommentId(commentId, userId).enqueue(callback);
    }

    public void unlikeComment(String commentId, String userId, Callback<Void> callback) {
        apiService.unlikeCommentByCommentId(commentId, userId).enqueue(callback);
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



}
