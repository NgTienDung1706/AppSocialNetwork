package vn.tiendung.socialnetwork.UI;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import vn.tiendung.socialnetwork.Adapter.FlatStoryPagerAdapter;
import vn.tiendung.socialnetwork.Model.FlatStoryItem;
import vn.tiendung.socialnetwork.Model.Post;
import vn.tiendung.socialnetwork.Model.StoryGroup;
import vn.tiendung.socialnetwork.R;

public class StoryViewActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private List<FlatStoryItem> flatStories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_view);

        viewPager = findViewById(R.id.storyViewPager);

        // ✅ Nhận từ Intent
        String json = getIntent().getStringExtra("storiesJson");
        int startGroupIndex = getIntent().getIntExtra("startPosition", 0);

        // ✅ Parse group list
        Type type = new TypeToken<List<StoryGroup>>() {}.getType();
        List<StoryGroup> groups = new Gson().fromJson(json, type);

        // ✅ Flatten stories
        flatStories = flattenStories(groups);  // hoặc từ ViewModel nếu bạn để ở đó

        // ✅ Tính index phẳng tương ứng với người được chọn
        int startFlatIndex = 0;
        for (int i = 0; i < startGroupIndex; i++) {
            startFlatIndex += groups.get(i).getStories().size();
        }

        // ✅ Gắn adapter
        viewPager.setAdapter(new FlatStoryPagerAdapter(this, flatStories));
        viewPager.setCurrentItem(startFlatIndex, false);
    }

    private List<FlatStoryItem> flattenStories(List<StoryGroup> groups) {
        List<FlatStoryItem> result = new ArrayList<>();
        for (StoryGroup group : groups) {
            for (Post story : group.getStories()) {
                result.add(new FlatStoryItem(group.getUser(), story));
            }
        }
        return result;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}

