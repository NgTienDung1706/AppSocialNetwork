package vn.tiendung.socialnetwork;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SongOptionsBottomSheet extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        Button btnViewArtist = view.findViewById(R.id.btnViewArtist);
        Button btnChangeSong = view.findViewById(R.id.btnChangeSong);
        Button btnUnpin = view.findViewById(R.id.btnUnpin);
        Button btnDeleteSong = view.findViewById(R.id.btnDeleteSong);

        btnViewArtist.setOnClickListener(v -> {
            // Xử lý mở trang nghệ sĩ
            dismiss();
        });

        btnChangeSong.setOnClickListener(v -> {
            // Xử lý thay bài hát
            dismiss();
        });

        btnUnpin.setOnClickListener(v -> {
            // Xử lý bỏ ghim
            dismiss();
        });

        btnDeleteSong.setOnClickListener(v -> {
            // Xử lý xóa bài hát
            dismiss();
        });
    }
}

