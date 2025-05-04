package vn.tiendung.socialnetwork.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import vn.tiendung.socialnetwork.Model.PostRequest;
import vn.tiendung.socialnetwork.Repository.PostRepository;
import vn.tiendung.socialnetwork.Utils.Resource;

public class PostViewModel extends ViewModel {
    private final PostRepository postRepository;

    private final MutableLiveData<Resource<Void>> postStatus  = new MutableLiveData<>();

    public PostViewModel() {
        postRepository = new PostRepository();
    }

    // Đăng bài viết
    public void createPost(PostRequest postRequest) {
        postStatus.setValue(Resource.loading(null));

        Log.d("PostViewModel", "Dữ liệu gửi đi: " + postRequest);

        postRepository.createPost(postRequest).observeForever(resource -> {
            if (resource.getStatus() == Resource.Status.SUCCESS) {
                postStatus.setValue(Resource.success(null));
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                postStatus.setValue(Resource.error(resource.getMessage(), null));
            }
        });
    }

    public LiveData<Resource<Void>> getPostStatus() {
        return postStatus;
    }
}
