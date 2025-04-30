package vn.tiendung.socialnetwork.Repository;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.tiendung.socialnetwork.API.APIService;
import vn.tiendung.socialnetwork.API.RetrofitClient;
import vn.tiendung.socialnetwork.Model.Post;
import vn.tiendung.socialnetwork.Utils.CloudinaryManager;
import vn.tiendung.socialnetwork.Utils.Resource;
import vn.tiendung.socialnetwork.Utils.SharedPrefManager;

public class StoryRepository {
    private final APIService apiService;

    public StoryRepository() {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
    }

    public LiveData<Resource<Void>> uploadStory(Context context, Uri imageUri, String caption) {
        MediatorLiveData<Resource<Void>> result = new MediatorLiveData<>();
        result.setValue(Resource.loading(null));

        LiveData<Resource<String>> uploadLiveData = CloudinaryManager.getInstance().uploadImage(context, imageUri);

        result.addSource(uploadLiveData, resource -> {
            if (resource.getStatus() == Resource.Status.SUCCESS) {
                String cloudinaryUrl = resource.getData();
                String userId = SharedPrefManager.getInstance(context).getUserId();
                Post story = new Post(userId, cloudinaryUrl, caption);

                createStory(story).observeForever(apiResult -> {
                    result.setValue(apiResult);
                });

            } else if (resource.getStatus() == Resource.Status.ERROR) {
                result.setValue(Resource.error("Lỗi upload ảnh: " + resource.getMessage(), null));
            }
        });

        return result;
    }

    private LiveData<Resource<Void>> createStory(Post story) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        apiService.createStory(story).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                Gson gson = new Gson();
                Log.d("DEBUG_JSON", "Story gửi đi: " + gson.toJson(story));

                if (response.isSuccessful()) {
                    result.setValue(Resource.success(null));
                } else {
                    result.setValue(Resource.error("Tạo story thất bại", null));
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                result.setValue(Resource.error("Lỗi kết nối: " + t.getMessage(), null));
            }
        });

        return result;
    }
}


