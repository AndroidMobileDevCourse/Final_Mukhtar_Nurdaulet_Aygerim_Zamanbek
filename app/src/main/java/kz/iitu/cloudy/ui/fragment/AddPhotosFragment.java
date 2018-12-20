package kz.iitu.cloudy.ui.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import kz.iitu.cloudy.ui.adapter.PhotoGridAdapter;
import kz.iitu.cloudy.utils.PreferenceUtils;
import kz.iitu.cloudy.R;
import kz.iitu.cloudy.model.User;
import kz.iitu.cloudy.databinding.FragmentAddPhotosBinding;
import kz.iitu.cloudy.ui.activity.HomeActivity;

import static android.app.Activity.RESULT_OK;

/**
 * Created by 1506k on 5/17/18.
 */

public class AddPhotosFragment extends Fragment
        implements View.OnClickListener, TextWatcher, LoadingDialogFragment.OnUploadCompleteListener {

    private static final String COLLECTION_DATA = "data";
    private static final String COLLECTION_HASHTAGS = "hashtags";
    private static final String COLLECTION_PHOTOS = "photos";

    private static final int REQUEST_CODE_PICKER = 1;

    private PhotoGridAdapter mAdapter;
    private List<Image> mImageList;

    private FragmentAddPhotosBinding mAddPhotosBinding;

    private StorageReference mStorageReference;
    private FirebaseFirestore mFirestore;

    private User mCurrentUser;

    public static AddPhotosFragment getInstance() {
        return new AddPhotosFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mAddPhotosBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_photos, container, false);
        return mAddPhotosBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCurrentUser = PreferenceUtils.getCurrentUser(getContext());

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mFirestore = FirebaseFirestore.getInstance();

        mAddPhotosBinding.imageGrid.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mAdapter = new PhotoGridAdapter();
        mAddPhotosBinding.imageGrid.setAdapter(mAdapter);

        mAddPhotosBinding.addPhotoButton.setOnClickListener(this);
        mAddPhotosBinding.uploadButton.setOnClickListener(this);

        mAddPhotosBinding.uploadButton.setEnabled(false);

        mAddPhotosBinding.hashtagInput.addTextChangedListener(this);
    }

    private void upload() {
        final String mHashtag = mAddPhotosBinding.hashtagInput.getText().toString();

        if (TextUtils.isEmpty(mHashtag)) {
            Toast.makeText(getContext(), "Укажите хештег", Toast.LENGTH_SHORT).show();

            return;
        }

        if (mImageList.isEmpty()) {
            Toast.makeText(getContext(), "Выберите фотографии", Toast.LENGTH_SHORT).show();

            return;
        }

        LoadingDialogFragment loadingDialogFragment
                = LoadingDialogFragment.getInstance(mHashtag, mImageList);
        loadingDialogFragment.setOnUploadCompleteListener(this);
        loadingDialogFragment.show(getChildFragmentManager(), loadingDialogFragment.getTag());
    }

    private void openPicker() {
        ImagePicker.create(getActivity())
                .folderMode(true)
                .toolbarFolderTitle("Папки")
                .toolbarImageTitle("Нажмите для выбора")
                .single()
                .multi()
                .limit(10)
                .imageDirectory("Camera")
                .start(REQUEST_CODE_PICKER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            mImageList = ImagePicker.getImages(data);

            if (!mImageList.isEmpty()) {
                mAddPhotosBinding.uploadButton.setEnabled(true);
            }

            for (int i = 0, l = mImageList.size(); i < l; i++) {
                mAdapter.addPhoto(mImageList.get(i).getPath());
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_photo_button:
                openPicker();
                break;
            case R.id.upload_button:
                upload();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.toString().isEmpty()) {
            mAddPhotosBinding.statusIcon.setVisibility(View.GONE);
            return;
        }

        DocumentReference documentReference = mFirestore.collection(COLLECTION_HASHTAGS)
                .document(editable.toString());

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    mAddPhotosBinding.statusIcon.setVisibility(View.VISIBLE);
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (documentSnapshot.exists()) {
                        mAddPhotosBinding.statusIcon
                                .setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_cross));
                        mAddPhotosBinding.statusLabel.setVisibility(View.VISIBLE);
                    } else {
                        mAddPhotosBinding.statusIcon
                                .setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_check));
                        mAddPhotosBinding.statusLabel.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @Override
    public void onUploadComplete() {
        ((HomeActivity) getActivity()).goHome();
    }
}
