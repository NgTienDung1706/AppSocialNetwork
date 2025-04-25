package vn.tiendung.socialnetwork.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import vn.tiendung.socialnetwork.Callback.CommentPostCallback;
import vn.tiendung.socialnetwork.Model.Comment;
import vn.tiendung.socialnetwork.Model.Post;
import vn.tiendung.socialnetwork.Model.UserProfile;
import vn.tiendung.socialnetwork.Repository.CommentRepository;
import vn.tiendung.socialnetwork.Repository.PostRepository;
import vn.tiendung.socialnetwork.Repository.UserRepository;

public class PostDetailViewModel extends ViewModel {

    private final PostRepository postRepository = new PostRepository();
    private final UserRepository userRepository = new UserRepository();
    private final CommentRepository commentRepository = new CommentRepository();

    private final MutableLiveData<Post> postLiveData = new MutableLiveData<>();
    private final MutableLiveData<UserProfile> userProfileLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Comment>> commentListLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<Post> getPost() {
        return postLiveData;
    }

    public LiveData<UserProfile> getUserProfile() {
        return userProfileLiveData;
    }

    public LiveData<List<Comment>> getComments() {
        return commentListLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadPost(String postId, String userId) {
        postRepository.getPostById(postId, userId, new PostRepository.PostCallback() {
            @Override
            public void onSuccess(Post post) {
                postLiveData.postValue(post);
                loadUser(post.getUser().get_id());
                loadComments(postId, userId);
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }

    public void loadUser(String userId) {
        userRepository.getUserProfile(userId, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(UserProfile userProfile) {
                userProfileLiveData.postValue(userProfile);
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }

    public void loadComments(String postId, String userId) {
        commentRepository.getCommentsByPostId(postId, userId, new CommentRepository.CommentCallback() {
            @Override
            public void onSuccess(List<Comment> comments) {
                commentListLiveData.postValue(comments);
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }
    public void createCommentByPostId(String postId, String userId, String content, String parentId) {
        Comment newComment = new Comment();
        newComment.setUserId(userId);
        newComment.setContent(content);
        newComment.setParent(parentId);  // Nếu comment là trả lời, truyền parentId

        // Gọi repository để gửi comment lên backend
        commentRepository.createCommentByPostId(postId, newComment, new CommentPostCallback() {
            @Override
            public void onSuccess(Comment comment) {
                loadComments(postId, userId);  // Refresh lại list bình luận
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }

}
