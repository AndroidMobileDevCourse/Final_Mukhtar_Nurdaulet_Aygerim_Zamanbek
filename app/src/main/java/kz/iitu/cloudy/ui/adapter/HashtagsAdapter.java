package kz.iitu.cloudy.ui.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import kz.iitu.cloudy.R;
import kz.iitu.cloudy.databinding.LayoutItemHashtagBinding;
import kz.iitu.cloudy.model.Hashtag;

/**
 * Created by 1506k on 5/17/18.
 */

public class HashtagsAdapter extends RecyclerView.Adapter<HashtagsAdapter.HashtagViewHolder> {

    private List<Hashtag> mHashtags;

    private OnHashtagClickedListener mListener;

    public HashtagsAdapter() {
        mHashtags = new ArrayList<>();
    }

    @NonNull
    @Override
    public HashtagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemHashtagBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.layout_item_hashtag, parent, false);

        return new HashtagViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HashtagViewHolder holder, int position) {
        holder.bindHashtag(mHashtags.get(position));
    }

    @Override
    public int getItemCount() {
        return mHashtags.size();
    }

    public void setListener(OnHashtagClickedListener listener) {
        mListener = listener;
    }

    public void setHashtags(List<Hashtag> hashtags) {
        mHashtags.clear();
        mHashtags.addAll(hashtags);

        notifyDataSetChanged();
    }

    public void addHashtag(@NonNull Hashtag hashtag) {
        mHashtags.add(hashtag);

        notifyDataSetChanged();
    }

    public class HashtagViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LayoutItemHashtagBinding mHashtagBinding;

        public HashtagViewHolder(LayoutItemHashtagBinding hashtagBinding) {
            super(hashtagBinding.getRoot());
            mHashtagBinding = hashtagBinding;
        }

        public void bindHashtag(@NonNull Hashtag hashtag) {
            mHashtagBinding.setHashtag(hashtag);

            mHashtagBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onHashtagClicked(mHashtags.get(getAdapterPosition()));
        }
    }

    public interface OnHashtagClickedListener {
        void onHashtagClicked(@NonNull Hashtag hashtag);
    }
}
