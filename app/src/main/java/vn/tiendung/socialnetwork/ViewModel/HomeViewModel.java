package vn.tiendung.socialnetwork.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.tiendung.socialnetwork.Model.Post;
import vn.tiendung.socialnetwork.Model.StoryGroup;
import vn.tiendung.socialnetwork.Repository.PostRepository;
import vn.tiendung.socialnetwork.Repository.StoryRepository;
import vn.tiendung.socialnetwork.Utils.Resource;

public class HomeViewModel extends ViewModel {

    private final PostRepository postRepository;
    private final StoryRepository storyRepository = new StoryRepository();
    private final MutableLiveData<List<Post>> posts = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<StoryGroup>>> stories = new MutableLiveData<>();
    private final MutableLiveData<List<StoryGroup>> groupedStories = new MutableLiveData<>();

    public HomeViewModel() {
        postRepository = new PostRepository();
    }

    public LiveData<List<Post>> getPosts() {
        return posts;
    }
    public MutableLiveData<Resource<List<StoryGroup>>> getStories() {
        return stories;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    public LiveData<List<StoryGroup>> getGroupedStories() {
        return groupedStories;
    }
    public void setGroupedStories(List<StoryGroup> groups) {
        groupedStories.setValue(groups);
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
    public void loadStories(String userId) {
        storyRepository.loadStories(userId).observeForever(resource -> {
            if (resource.getStatus() == Resource.Status.SUCCESS) {
                List<Post> flatStories = resource.getData();
                List<StoryGroup> grouped = groupByUser(flatStories);
                groupedStories.setValue(grouped);
            }
        });
    }
    private List<StoryGroup> groupByUser(List<Post> stories) {
        Map<String, List<Post>> map = new HashMap<>();

        for (Post story : stories) {
            String uid = story.getUser().get_id();
            map.computeIfAbsent(uid, k -> new ArrayList<>()).add(story);
        }

        List<StoryGroup> groups = new ArrayList<>();
        for (Map.Entry<String, List<Post>> entry : map.entrySet()) {
            Post.User user = entry.getValue().get(0).getUser();
            groups.add(new StoryGroup(user, entry.getValue()));
        }

        return groups;
    }

}
