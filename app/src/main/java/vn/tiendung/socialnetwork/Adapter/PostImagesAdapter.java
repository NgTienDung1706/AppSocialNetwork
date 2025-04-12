package vn.tiendung.socialnetwork.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.tiendung.socialnetwork.R;

public class PostImagesAdapter extends RecyclerView.Adapter<PostImagesAdapter.ImageViewHolder> {
    private final List<String> imageList; // URL của các ảnh trong drawable
    private final Context context;

    public PostImagesAdapter(Context context, List<String> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        // Lấy URL ảnh từ imageList và sử dụng Glide để tải vào ImageView
        String imageUrl = imageList.get(position);

        Glide.with(context)
                .load(imageUrl)  // Tải ảnh từ URL
/*                .placeholder(R.drawable.placeholder)  // Ảnh hiển thị trong khi đang tải
                .error(R.drawable.error_image)  // Ảnh lỗi nếu không thể tải*/
                .into(holder.imageView);  // Đặt ảnh vào ImageView
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivImage);
        }
    }
}
