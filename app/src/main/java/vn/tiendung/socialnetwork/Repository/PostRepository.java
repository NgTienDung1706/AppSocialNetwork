package vn.tiendung.socialnetwork.Repository;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.RetrofitClient;
import vn.tiendung.socialnetwork.Model.Post;

public class PostRepository {

    private final APIService apiService;

    public interface PostCallback {
        void onSuccess(Post post);
        void onError(String message);
    }

    public PostRepository() {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
    }

    public void getPostById(String postId, String userId, PostCallback callback) {
        Call<Post> call = apiService.getPostById(postId, userId);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(@NonNull Call<Post> call, @NonNull Response<Post> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không tìm thấy bài viết");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Post> call, @NonNull Throwable t) {
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}
