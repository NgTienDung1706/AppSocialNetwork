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
import vn.tiendung.socialnetwork.Model.StoryGroup;
import vn.tiendung.socialnetwork.R;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {
    private List<StoryGroup> storyGroups = new ArrayList<>();
    private Context context;
    private OnStoryGroupClickListener listener;
    public interface OnStoryGroupClickListener {
        void onStoryGroupClick(StoryGroup group, int position, List<StoryGroup> allGroups);
    }


    public StoryAdapter(Context context, OnStoryGroupClickListener listener) {
        this.context = context;
        this.listener = listener;
    }
    public void setStoryGroups(List<StoryGroup> groups) {
        this.storyGroups.clear();
        if (groups != null) {
            this.storyGroups.addAll(groups);
        }
        notifyDataSetChanged();
    }
    public List<StoryGroup> getStoryGroups() {
        return storyGroups;
    }
    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_story, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        StoryGroup group = storyGroups.get(position);
        Post.User user = group.getUser();

        holder.textUsername.setText(user.getName());


        // Load avatar bằng Glide
        Glide.with(context)
                .load(group.getStories().get(0).getContent().getPictures().get(0))
                .placeholder(R.drawable.circleusersolid)
                .into(holder.imageStory);
        Glide.with(context)
                .load(group.getUser().getAvatar())
                .placeholder(R.drawable.circleusersolid)
                .circleCrop()
                .into(holder.imageAvatar);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStoryGroupClick(group, position, storyGroups);
            }
        });
    }

    @Override
    public int getItemCount() {
        return storyGroups.size();
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

