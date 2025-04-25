package vn.tiendung.socialnetwork.Callback;

import vn.tiendung.socialnetwork.Model.Comment;

public interface CommentPostCallback {
    void onSuccess(Comment comment);
    void onError(String message);
}

