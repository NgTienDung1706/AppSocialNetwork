package vn.tiendung.socialnetwork.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import vn.tiendung.socialnetwork.Model.Post;
import vn.tiendung.socialnetwork.Repository.PostRepository;
import vn.tiendung.socialnetwork.Utils.Resource;

public class HomeViewModel extends ViewModel {

    private final PostRepository postRepository;
    private final MutableLiveData<List<Post>> posts = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public HomeViewModel() {
        postRepository = new PostRepository();
    }

    public LiveData<List<Post>> getPosts() {
        return posts;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadPosts(String userId) {
        isLoading.setValue(true); // Start loading
        postRepository.getAllPosts(userId).observeForever(resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        isLoading.setValue(false);
                        posts.setValue(resource.getData());
                        break;
                    case ERROR:
                        isLoading.setValue(false);
                        errorMessage.setValue(resource.getMessage());
                        break;
                    case LOADING:
                        isLoading.setValue(true);
                        break;
                }
            }
        });
    }
}
