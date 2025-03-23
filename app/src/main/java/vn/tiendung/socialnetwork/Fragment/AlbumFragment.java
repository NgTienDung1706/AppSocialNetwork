package vn.tiendung.socialnetwork.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import vn.tiendung.socialnetwork.R;
import vn.tiendung.socialnetwork.Utils.OnScrollListener;

public class AlbumFragment extends Fragment {
    private OnScrollListener scrollListener;
    private RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.album_fragment, container, false);
        recyclerView = view.findViewById(R.id.recyclerAlbum);

        // Gửi sự kiện lên ProfileFragment
        if (getActivity() instanceof OnScrollListener) {
            scrollListener = (OnScrollListener) getActivity();
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (scrollListener != null) {
                    scrollListener.onScroll(dy > 0);
                }
            }
        });

        return view;
    }
}
