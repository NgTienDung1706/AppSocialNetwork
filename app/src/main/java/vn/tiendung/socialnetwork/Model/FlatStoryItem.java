package vn.tiendung.socialnetwork.Model;

public class FlatStoryItem {
    private Post.User user;
    private Post post;

    public FlatStoryItem(Post.User user, Post post) {
        this.user = user;
        this.post = post;
    }

    public Post.User getUser() {
        return user;
    }

    public Post getPost() {
        return post;
    }
}
