package vn.tiendung.socialnetwork.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.tiendung.socialnetwork.Model.Comment;
import vn.tiendung.socialnetwork.Model.Post;
import vn.tiendung.socialnetwork.Model.UserProfile;
import vn.tiendung.socialnetwork.Repository.CommentRepository;
import vn.tiendung.socialnetwork.Repository.PostRepository;
import vn.tiendung.socialnetwork.Repository.UserRepository;
import vn.tiendung.socialnetwork.Utils.Resource;

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
        postRepository.getPostById(postId, userId).observeForever(result -> {
            if (result.getStatus() == Resource.Status.SUCCESS) {
                postLiveData.postValue(result.getData());
                if (result.getData() != null) {
                    loadUser(result.getData().getUser().get_id());
                    loadComments(postId, userId);
                }
            } else {
                errorMessage.postValue(result.getMessage());
            }
        });
    }

    public void loadUser(String userId) {
        userRepository.getUserProfile(userId).observeForever(result -> {
            if (result.getStatus() == Resource.Status.SUCCESS) {
                userProfileLiveData.postValue(result.getData());
            } else {
                errorMessage.postValue(result.getMessage());
            }
        });
    }

    public void loadComments(String postId, String userId) {
        commentRepository.getCommentsByPostId(postId, userId).observeForever(result -> {
            if (result.getStatus() == Resource.Status.SUCCESS) {
                commentListLiveData.postValue(result.getData());
            } else {
                errorMessage.postValue(result.getMessage());
            }
        });
    }

    public void createCommentByPostId(String postId, String userId, String content, String parentId) {
        Comment newComment = new Comment();
        newComment.setUserId(userId);
        newComment.setContent(content);
        newComment.setParent(parentId);

        commentRepository.createCommentByPostId(postId, newComment).observeForever(result -> {
            if (result.getStatus() == Resource.Status.SUCCESS) {
                loadComments(postId, userId);
            } else {
                errorMessage.postValue(result.getMessage());
            }
        });
    }

    public void toggleLikeComment(String currentUserId, Comment comment, int position) {
        boolean isLiked = comment.isMyLike();
        comment.setMyLike(!isLiked);

        if (isLiked) {
            comment.getLikes().remove(currentUserId);
        } else {
            comment.getLikes().add(currentUserId);
        }

        // Cập nhật trong danh sách
        List<Comment> currentList = commentListLiveData.getValue();
        if (currentList != null && position >= 0 && position < currentList.size()) {
            currentList.set(position, comment);
            commentListLiveData.postValue(new ArrayList<>(currentList));
        }

        // Gửi lên server
        LiveData<Resource<Void>> likeLiveData = isLiked ?
                commentRepository.unlikeComment(comment.getId(), currentUserId) :
                commentRepository.likeComment(comment.getId(), currentUserId);

        likeLiveData.observeForever(result -> {
            if (result.getStatus() == Resource.Status.ERROR) {
                errorMessage.postValue(result.getMessage());
            }
        });
    }

    public void deleteComment(String commentId) {
        commentRepository.deleteCommentByCommentId(commentId).observeForever(result -> {
            if (result.getStatus() == Resource.Status.SUCCESS) {
                String postId = postLiveData.getValue() != null ? postLiveData.getValue().getId() : null;
                String userId = userProfileLiveData.getValue() != null ? userProfileLiveData.getValue().getId() : null;

                if (postId != null && userId != null) {
                    loadComments(postId, userId);
                }
            } else {
                errorMessage.postValue(result.getMessage());
            }
        });
    }

    public List<Comment> buildCommentTree(List<Comment> flatComments) {
        List<Comment> topLevelComments = new ArrayList<>();
        Map<String, Comment> commentMap = new HashMap<>();

        for (Comment comment : flatComments) {
            commentMap.put(comment.getId(), comment);
        }

        for (Comment comment : flatComments) {
            if (comment.getParent() == null) {
                topLevelComments.add(comment);
            } else {
                Comment parentComment = commentMap.get(comment.getParent());
                if (parentComment != null) {
                    parentComment.getNestedComments().add(comment);
                }
            }
        }

        return topLevelComments;
    }

    public List<Comment> flattenCommentTree(List<Comment> comments, int currentDepth) {
        List<Comment> result = new ArrayList<>();
        for (Comment comment : comments) {
            if (currentDepth >= 2) {
                currentDepth = 1;
            }
            comment.setDepth(currentDepth);
            result.add(comment);

            if (comment.getNestedComments() != null && !comment.getNestedComments().isEmpty()) {
                result.addAll(flattenCommentTree(comment.getNestedComments(), currentDepth + 1));
            }
        }
        return result;
    }


}
