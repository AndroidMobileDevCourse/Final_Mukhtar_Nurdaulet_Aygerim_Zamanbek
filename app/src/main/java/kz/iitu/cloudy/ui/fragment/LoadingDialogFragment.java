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

import kz.iitu.cloudy.model.Hashtag;
import kz.iitu.cloudy.model.Photo;
import kz.iitu.cloudy.utils.PreferenceUtils;
import kz.iitu.cloudy.R;
import kz.iitu.cloudy.model.User;
import kz.iitu.cloudy.databinding.LayoutLoadingDialogFragmentBinding;

/**
 * Created by 1506k on 5/12/18.
 */

public class LoadingDialogFragment extends BottomSheetDialogFragment {

    private static final String ARG_HASHTAG = "hashtag";
    private static final String ARG_PHOTOS = "photos";

    private static final String COLLECTION_DATA = "data";
    private static final String COLLECTION_HASHTAGS = "hashtags";
    private static final String COLLECTION_PHOTOS = "photos";

    private LayoutLoadingDialogFragmentBinding mLoadingDialogFragmentBinding;

    private String mHashtag;
    private List<Image> mPhotosList;

    private User mCurrentUser;

    private StorageReference mStorageReference;
    private FirebaseFirestore mFirestore;

    private int mUploadedPhoto = 1;

    private OnUploadCompleteListener mOnUploadCompleteListener;

    public static LoadingDialogFragment getInstance(String hashtag,
                                                    List<Image> photos) {
        LoadingDialogFragment dialogFragment = new LoadingDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PHOTOS, new ArrayList<>(photos));
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

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mFirestore = FirebaseFirestore.getInstance();

        mPhotosList = getArguments().getParcelableArrayList(ARG_PHOTOS);
        mHashtag = getArguments().getString(ARG_HASHTAG);

        mCurrentUser = PreferenceUtils.getCurrentUser(getContext());

        mLoadingDialogFragmentBinding.label.setText(getString(R.string.loading_fmt, mUploadedPhoto,
                mPhotosList.size()));

        upload();

    }

    public void setOnUploadCompleteListener(OnUploadCompleteListener onUploadCompleteListener) {
        mOnUploadCompleteListener = onUploadCompleteListener;
    }

    private void upload() {

        final DocumentReference documentReference = mFirestore.collection(COLLECTION_DATA)
                .document(mCurrentUser.getUsername())
                .collection(COLLECTION_HASHTAGS).document(mHashtag);

        documentReference.set(new Hashtag(mHashtag, 0));

        final DocumentReference hashtagReference = mFirestore.collection(COLLECTION_HASHTAGS).document(mHashtag);
        hashtagReference.set(new Hashtag(mHashtag, mCurrentUser.getUsername()));

        for (final Image image: mPhotosList) {
            Uri uri = Uri.fromFile(new File(image.getPath()));

            StorageReference reference = mStorageReference.child(COLLECTION_PHOTOS).child(image.getName());
            reference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        UploadTask.TaskSnapshot snapshot = task.getResult();

                        try {
                            URL url = new URL(snapshot.getDownloadUrl().toString());
                            final String stringUrl = url.toString();
                            documentReference.collection(COLLECTION_PHOTOS)
                                    .add(new Photo(image.getName(), stringUrl))
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            hashtagReference.collection(COLLECTION_PHOTOS).add(new Photo(image.getName(), stringUrl))
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                                            mUploadedPhoto++;

                                                            if (mUploadedPhoto > mPhotosList.size()) {
                                                                mOnUploadCompleteListener.onUploadComplete();
                                                                dismiss();
                                                            }

                                                            mLoadingDialogFragmentBinding.label.setText(getString(R.string.loading_fmt, mUploadedPhoto,
                                                                    mPhotosList.size()));
                                                        }
                                                    });
                                        }
                                    });



                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public interface OnUploadCompleteListener {
        void onUploadComplete();
    }

}
