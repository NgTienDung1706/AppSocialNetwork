package vn.tiendung.socialnetwork.Repository;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.RetrofitClient;
import vn.tiendung.socialnetwork.Model.Post;
import vn.tiendung.socialnetwork.Model.PostRequest;
import vn.tiendung.socialnetwork.Utils.Resource;

public class PostRepository {
    private final APIService apiService;


    public PostRepository() {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
    }

    //  Get All Posts
    public LiveData<Resource<List<Post>>> getAllPosts(String userId) {
        MutableLiveData<Resource<List<Post>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getAllPosts(userId).enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Không tìm thấy bài viết", null));
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    //  Get Single Post by ID
    public LiveData<Resource<Post>> getPostById(String postId, String userId) {
        MutableLiveData<Resource<Post>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getPostById(postId, userId).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(response.body()));
                } else {
                    result.setValue(Resource.error("Không tìm thấy bài viết", null));
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    // Tạo bài viết mới
    public LiveData<Resource<Void>> createPost(PostRequest postRequest) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        apiService.createPost(postRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    result.setValue(Resource.success(null));
                } else {
                    result.setValue(Resource.error("Đăng bài thất bại: " + response.message(), null));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public interface PostCallback {
        void onSuccess();
        void onError(String errorMessage);
    }
}
