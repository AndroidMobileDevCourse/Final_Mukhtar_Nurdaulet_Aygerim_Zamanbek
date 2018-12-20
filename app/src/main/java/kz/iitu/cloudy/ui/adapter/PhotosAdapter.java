package kz.iitu.cloudy.ui.adapter;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kz.iitu.cloudy.R;
import kz.iitu.cloudy.databinding.LayoutItemPhotoBinding;
import kz.iitu.cloudy.model.Photo;

/**
 * Created by 1506k on 5/17/18.
 */

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder> {

    private static final int MODE_NONE = 0;
    private static final int MODE_MULTI_SELECT = 1;

    private List<Photo> mPhotos;

    private OnPhotoClickListener mOnPhotoClickListener;

    private int mCurrentMode = MODE_NONE;

    private List<String> mSelectedPhotoIds;

    public PhotosAdapter() {
        mPhotos = new ArrayList<>();
        mSelectedPhotoIds = new ArrayList<>();
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemPhotoBinding photoBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.layout_item_photo, parent, false);
        return new PhotoViewHolder(photoBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        holder.bindPhoto(mPhotos.get(position));
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    public void clear() {
        mPhotos.clear();
    }

    public void setPhotos(List<Photo> photos) {
        mPhotos.clear();
        mPhotos.addAll(photos);

        notifyDataSetChanged();
    }

    public List<String> getSelectedPhotoIds() {
        return mSelectedPhotoIds;
    }

    public void setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener) {
        mOnPhotoClickListener = onPhotoClickListener;
    }

    public void changeMode() {
        mSelectedPhotoIds.clear();

        mCurrentMode = mCurrentMode == MODE_NONE ? MODE_MULTI_SELECT : MODE_NONE;
        notifyDataSetChanged();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private LayoutItemPhotoBinding mPhotoBinding;

        public PhotoViewHolder(LayoutItemPhotoBinding photoBinding) {
            super(photoBinding.getRoot());
            mPhotoBinding = photoBinding;

        }

        public void bindPhoto(@NonNull Photo photo) {
            Picasso.get()
                    .load(photo.getUrl())
                    .into(mPhotoBinding.photoView);

            if (mCurrentMode == MODE_NONE) {
                mPhotoBinding.photoView.setColorFilter(ContextCompat.getColor(itemView.getContext(),
                        android.R.color.transparent));
            }

            mPhotoBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mCurrentMode == MODE_NONE) {
                mOnPhotoClickListener.onPhotoClicked(mPhotos.get(getAdapterPosition()));
                return;
            }

            Photo photo = mPhotos.get(getAdapterPosition());

            if (mCurrentMode == MODE_MULTI_SELECT) {
                if (!mSelectedPhotoIds.contains(photo.getId())) {
                    mSelectedPhotoIds.add(photo.getId());
                    mPhotoBinding.photoView.setColorFilter(Color.rgb(123, 123, 123), android.graphics.PorterDuff.Mode.MULTIPLY);
                } else {
                    mPhotoBinding.photoView.setColorFilter(ContextCompat.getColor(itemView.getContext(), android.R.color.transparent));
                    mSelectedPhotoIds.remove(photo.getId());
                }
            } else {
                mPhotoBinding.photoView.setColorFilter(ContextCompat.getColor(itemView.getContext(), android.R.color.transparent));
            }
        }
    }

    public interface OnPhotoClickListener {
        void onPhotoClicked(@NonNull Photo photo);
    }
}
