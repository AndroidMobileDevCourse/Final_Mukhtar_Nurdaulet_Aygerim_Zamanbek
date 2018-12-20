package kz.iitu.cloudy.ui.fragment;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.esafirm.imagepicker.model.Image;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import kz.iitu.cloudy.R;
import kz.iitu.cloudy.databinding.LayoutLoadingDialogFragmentBinding;
import kz.iitu.cloudy.model.Hashtag;
import kz.iitu.cloudy.model.Photo;
import kz.iitu.cloudy.model.User;
import kz.iitu.cloudy.utils.PreferenceUtils;

/**
 * Created by 1506k on 5/12/18.
 */

public class DeletingDialogFragment extends BottomSheetDialogFragment {

    private static final String ARG_HASHTAG = "hashtag";
    private static final String ARG_PHOTOS = "photos";

    private static final String COLLECTION_DATA = "data";
    private static final String COLLECTION_HASHTAGS = "hashtags";
    private static final String COLLECTION_PHOTOS = "photos";

    private LayoutLoadingDialogFragmentBinding mLoadingDialogFragmentBinding;

    private String mHashtag;
    private List<String> mPhotosList;

    private User mCurrentUser;

    private FirebaseFirestore mFirestore;

    private int mUploadedPhoto = 1;

    private OnDeleteCompleteListener mOnDeleteCompleteListener;

    public static DeletingDialogFragment getInstance(String hashtag,
                                                     List<String> photos) {
        DeletingDialogFragment dialogFragment = new DeletingDialogFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_PHOTOS, new ArrayList<>(photos));
        args.putString(ARG_HASHTAG, hashtag);

        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        mLoadingDialogFragmentBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.layout_loading_dialog_fragment, null, false);

        dialog.setContentView(mLoadingDialogFragmentBinding.getRoot());

        FrameLayout bottomSheet = dialog.getWindow().findViewById(android.support.design.R.id.design_bottom_sheet);
        bottomSheet.setBackgroundResource(android.R.color.transparent);

        mFirestore = FirebaseFirestore.getInstance();

        mPhotosList = getArguments().getStringArrayList(ARG_PHOTOS);
        mHashtag = getArguments().getString(ARG_HASHTAG);

        mCurrentUser = PreferenceUtils.getCurrentUser(getContext());

        mLoadingDialogFragmentBinding.label.setText(getString(R.string.deleting_fmt, mUploadedPhoto,
                mPhotosList.size()));

        delete();

    }

    public void setOnDeleteCompleteListener(OnDeleteCompleteListener onDeleteCompleteListener) {
        mOnDeleteCompleteListener = onDeleteCompleteListener;
    }

    private void delete() {
        final DocumentReference documentReference = mFirestore.collection(COLLECTION_DATA)
                .document(mCurrentUser.getUsername())
                .collection(COLLECTION_HASHTAGS).document(mHashtag);

        documentReference.set(new Hashtag(mHashtag, 0));

        final DocumentReference hashtagReference = mFirestore.collection(COLLECTION_HASHTAGS).document(mHashtag);

        for (final String image: mPhotosList) {

            documentReference.collection(COLLECTION_PHOTOS).document(image)
                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    hashtagReference.collection(COLLECTION_PHOTOS).document(image)
                            .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mUploadedPhoto++;

                            if (mUploadedPhoto > mPhotosList.size()) {
                                mOnDeleteCompleteListener.onDeleteComplete();
                                dismiss();
                            }

                            mLoadingDialogFragmentBinding.label.setText(getString(R.string.loading_fmt, mUploadedPhoto,
                                    mPhotosList.size()));
                        }
                    });
                }
            });
        }
    }

    public interface OnDeleteCompleteListener {
        void onDeleteComplete();
    }

}
