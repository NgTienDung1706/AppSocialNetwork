package vn.tiendung.socialnetwork.Repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.RetrofitClient;
import vn.tiendung.socialnetwork.Model.Comment;
import vn.tiendung.socialnetwork.Utils.Resource;

import java.util.List;

public class CommentRepository {

    private final APIService apiService;

    public CommentRepository() {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
    }

    public LiveData<Resource<List<Comment>>> getCommentsByPostId(String postId, String userId) {
        MutableLiveData<Resource<List<Comment>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.getCommentsByPostId(postId, userId).enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(Resource.success(response.body()));
                } else {
                    result.postValue(Resource.error("Không tìm thấy comment", null));
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                result.postValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Void>> likeComment(String commentId, String userId) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.likeCommentByCommentId(commentId, userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    result.postValue(Resource.success(null));
                } else {
                    result.postValue(Resource.error("Like thất bại: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.postValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Void>> unlikeComment(String commentId, String userId) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.unlikeCommentByCommentId(commentId, userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    result.postValue(Resource.success(null));
                } else {
                    result.postValue(Resource.error("Bỏ like thất bại: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.postValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Comment>> createCommentByPostId(String postId, Comment comment) {
        MutableLiveData<Resource<Comment>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.createCommentByPostId(postId, comment).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(Resource.success(response.body()));
                } else {
                    result.postValue(Resource.error("Không thể gửi bình luận: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                result.postValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }

    public LiveData<Resource<Void>> deleteCommentByCommentId(String commentId) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.deleteCommentByCommentId(commentId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    result.postValue(Resource.success(null));
                } else {
                    result.postValue(Resource.error("Xóa thất bại: " + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.postValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }
}
