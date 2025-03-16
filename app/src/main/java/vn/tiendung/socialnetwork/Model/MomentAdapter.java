package vn.tiendung.socialnetwork.Model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.tiendung.socialnetwork.R;

public class MomentAdapter extends RecyclerView.Adapter<MomentAdapter.MomentViewHolder> {

    private List<Moment> moments;

    public MomentAdapter(List<Moment> moments) {
        this.moments = moments;
    }

    @NonNull
    @Override
    public MomentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.moment_item, parent, false);
        return new MomentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MomentViewHolder holder, int position) {
        Moment moment = moments.get(position);
        holder.imageMoment.setImageResource(moment.getImageResId());
        holder.textMomentDescription.setText(moment.getDescription());
    }

    @Override
    public int getItemCount() {
        return moments.size();
    }

    public static class MomentViewHolder extends RecyclerView.ViewHolder {
        ImageView imageMoment;
        TextView textMomentDescription;

        public MomentViewHolder(@NonNull View itemView) {
            super(itemView);
            imageMoment = itemView.findViewById(R.id.imageMoment);
            textMomentDescription = itemView.findViewById(R.id.textMomentDescription);
        }
    }
}

