package kz.iitu.cloudy.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kz.iitu.cloudy.R;

/**
 * Created by 1506k on 6/10/17.
 */

public class PhotoGridAdapter extends RecyclerView.Adapter<PhotoGridAdapter.PhotoGridViewHolder> {

    private ArrayList<String> mPhotoPaths;

    public PhotoGridAdapter() {
        mPhotoPaths = new ArrayList<>();
    }

    @Override
    public PhotoGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_photo_grid, parent, false);

        return new PhotoGridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoGridViewHolder holder, int position) {
        holder.loadPhoto(mPhotoPaths.get(position));
    }

    @Override
    public int getItemCount() {
        return mPhotoPaths.size();
    }

    public void addPhoto(@NonNull String photoPath) {
        mPhotoPaths.add(photoPath);
        notifyDataSetChanged();
    }

    public void addPhotos(@NonNull List<String> photos) {
        mPhotoPaths.addAll(photos);
        notifyDataSetChanged();
    }

    public ArrayList<String> getImagePaths() {
        return mPhotoPaths;
    }


    class PhotoGridViewHolder extends RecyclerView.ViewHolder {

        private ImageView mThumbnailView;

        public PhotoGridViewHolder(View itemView) {
            super(itemView);

            mThumbnailView = (ImageView) itemView.findViewById(R.id.layout_item_photo_grid_thumb);
        }

        public void loadPhoto(@NonNull String photoPath) {
            Log.d("taaag", photoPath);

            Picasso.get()
                    .load("file://" + photoPath)
                    .resize(480, 480)
                    .centerCrop()
                    .into(mThumbnailView);
        }
    }
}
