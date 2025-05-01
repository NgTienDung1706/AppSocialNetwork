package vn.tiendung.socialnetwork.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import vn.tiendung.socialnetwork.Model.Moment;
import vn.tiendung.socialnetwork.Model.Post;
import vn.tiendung.socialnetwork.R;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private List<Post> stories = new ArrayList<>();

    private Context context;

    public StoryAdapter(Context context) {
        this.context = context;
    }
    public void setStories(List<Post> stories) {
        this.stories = stories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_story, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        Post story = stories.get(position);

        holder.textUsername.setText(story.getUser().getName());


        // Load avatar bằng Glide
        Glide.with(context)
                .load(story.getContent().getPictures().get(0))
                .placeholder(R.drawable.circleusersolid)
                .into(holder.imageStory);
        Glide.with(context)
                .load(story.getUser().getAvatar())
                .placeholder(R.drawable.circleusersolid)
                .circleCrop()
                .into(holder.imageAvatar);

        // TODO: onClick mở StoryViewerActivity (nếu bạn có)
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageAvatar, imageStory;
        TextView textUsername;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageStory = itemView.findViewById(R.id.imageStory);
            imageAvatar = itemView.findViewById(R.id.imageAvatar);
            textUsername = itemView.findViewById(R.id.textUsername);
        }
    }
}

