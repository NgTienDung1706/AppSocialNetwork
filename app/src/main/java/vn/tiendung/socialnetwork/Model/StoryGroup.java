package vn.tiendung.socialnetwork.Model;

import java.util.List;

public class StoryGroup {
    private Post.User user;
    private List<Post> stories;

    public StoryGroup(Post.User user, List<Post> stories) {
        this.user = user;
        this.stories = stories;
    }

    public Post.User getUser() {
        return user;
    }

    public List<Post> getStories() {
        return stories;
    }
}

