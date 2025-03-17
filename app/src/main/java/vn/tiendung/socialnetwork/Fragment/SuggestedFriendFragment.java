package vn.tiendung.socialnetwork.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import vn.tiendung.socialnetwork.Model.Adapter.TabFragmentAdapter;
import vn.tiendung.socialnetwork.R;

public class SuggestedFriendFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suggested_friend, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Dữ liệu mẫu
        List<String> suggestions = Arrays.asList("Gợi ý A", "Gợi ý B", "Gợi ý C");

        // Thiết lập adapter
        RecyclerView.Adapter adapter = new TabFragmentAdapter(suggestions);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
