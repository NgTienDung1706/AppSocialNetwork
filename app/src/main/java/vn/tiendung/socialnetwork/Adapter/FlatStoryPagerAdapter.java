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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.tiendung.socialnetwork.Model.FlatStoryItem;
import vn.tiendung.socialnetwork.R;

public class FlatStoryPagerAdapter extends RecyclerView.Adapter<FlatStoryPagerAdapter.StoryViewHolder> {

    private final List<FlatStoryItem> stories;
    private final Context context;

    public FlatStoryPagerAdapter(Context context, List<FlatStoryItem> stories) {
        this.context = context;
        this.stories = stories;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_story_view, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        FlatStoryItem item = stories.get(position);

        holder.caption.setText(item.getPost().getContent().getCaption());
        holder.username.setText(item.getUser().getName());

        Glide.with(context)
                .load(item.getPost().getContent().getPictures().get(0))
                .thumbnail(0.1f)
                .dontAnimate()
                .into(holder.imageStory);

        Glide.with(context)
                .load(item.getUser().getAvatar())
                .placeholder(R.drawable.circleusersolid)
                .into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    static class StoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageStory;
        TextView caption, username;
        CircleImageView avatar;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageStory = itemView.findViewById(R.id.imageStory);
            caption = itemView.findViewById(R.id.textCaption);
            username = itemView.findViewById(R.id.textUsername);
            avatar = itemView.findViewById(R.id.imageAvatar);
        }
    }
}
