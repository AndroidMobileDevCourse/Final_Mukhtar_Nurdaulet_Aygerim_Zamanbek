package kz.iitu.cloudy.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import kz.iitu.cloudy.model.Hashtag;
import kz.iitu.cloudy.model.Photo;
import kz.iitu.cloudy.ui.adapter.PhotosAdapter;
import kz.iitu.cloudy.ui.fragment.DeletingDialogFragment;
import kz.iitu.cloudy.utils.PreferenceUtils;
import kz.iitu.cloudy.R;
import kz.iitu.cloudy.model.User;
import kz.iitu.cloudy.databinding.ActivityPhotoListBinding;

public class PhotoListActivity extends AppCompatActivity implements
        PhotosAdapter.OnPhotoClickListener, View.OnClickListener,
        DeletingDialogFragment.OnDeleteCompleteListener {

    private static final String ARG_HASHTAG = "hashtag";
    private static final String ARG_COUNT = "count";
    private static final String ARG_USER_HASHTAGS = "user_hashtags";

    private static final String COLLECTION_DATA = "data";
    private static final String COLLECTION_HASHTAGS = "hashtags";
    private static final String COLLECTION_PHOTOS = "photos";

    private ActivityPhotoListBinding mPhotoListBinding;

    private Hashtag mHashtag;
    private int mCount;
    private boolean mUserHashtags;

    private PhotosAdapter mPhotosAdapter;

    private FirebaseFirestore mFirestore;

    private User mCurrentUser;

    public static void start(Context context, Hashtag hashtag, int count, boolean userHashtags) {
        Intent starter = new Intent(context, PhotoListActivity.class);
        starter.putExtra(ARG_HASHTAG, hashtag);
        starter.putExtra(ARG_COUNT, count);
        starter.putExtra(ARG_USER_HASHTAGS, userHashtags);

        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPhotoListBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_photo_list);

        mFirestore = FirebaseFirestore.getInstance();

        mHashtag = getIntent().getParcelableExtra(ARG_HASHTAG);
        mCount = getIntent().getIntExtra(ARG_COUNT, 0);
        mUserHashtags = getIntent().getBooleanExtra(ARG_USER_HASHTAGS, false);

        mCurrentUser = PreferenceUtils.getCurrentUser(this);

        setupViews();
    }

    private void setupViews() {
        if (!PreferenceUtils.isLoggedIn(this)) {
            mPhotoListBinding.icDelete.setVisibility(View.GONE);
        }

        mPhotoListBinding.titleView.setText(getString(R.string.hashtag_fmt, mHashtag.getName()));
        mPhotoListBinding.countLabel.setText(getString(R.string.count_fmt, mCount));

        mPhotosAdapter = new PhotosAdapter();
        mPhotosAdapter.setOnPhotoClickListener(this);
        mPhotoListBinding.photosList.setLayoutManager(new GridLayoutManager(this,
                3));
        mPhotoListBinding.photosList.setAdapter(mPhotosAdapter);

        mPhotoListBinding.icDelete.setOnClickListener(this);
        mPhotoListBinding.deleteButton.setOnClickListener(this);
        mPhotoListBinding.closeBtn.setOnClickListener(this);

        mPhotoListBinding.backIcon.setOnClickListener(this);

        loadPhotos();
    }


    private void loadPhotos() {
        final List<Photo> photos = new ArrayList<>();
        CollectionReference reference = mUserHashtags ? mFirestore.collection(COLLECTION_DATA)
                .document(mCurrentUser.getUsername())
                .collection(COLLECTION_HASHTAGS)
                .document(mHashtag.getName())
                .collection(COLLECTION_PHOTOS)
                : mFirestore.collection(COLLECTION_HASHTAGS).document(mHashtag.getName())
                .collection(COLLECTION_PHOTOS);

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        mPhotoListBinding.progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                                Photo photo = documentSnapshot.toObject(Photo.class);
                                photo.setId(documentSnapshot.getId());

                                photos.add(photo);
                            }

                            mPhotoListBinding.countLabel.setText(getString(R.string.count_fmt, task.getResult().size()));
                            mPhotosAdapter.setPhotos(photos);
                        }
                    }
                });
    }

    @Override
    public void onPhotoClicked(@NonNull Photo photo) {
        if (!PreferenceUtils.isLoggedIn(this)) {
            PhotoActivity.start(this, photo.getUrl(), mHashtag.getAuthor(),
                    photo.getId(), mHashtag.getName());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_icon:
                finish();
                break;
            case R.id.ic_delete:
                mPhotosAdapter.changeMode();
                mPhotoListBinding.closeBtn.setVisibility(View.VISIBLE);
                mPhotoListBinding.deleteButton.setVisibility(View.VISIBLE);
                return;
            case R.id.delete_button:
                deletePhotos();
                break;
            case R.id.close_btn:
                mPhotosAdapter.changeMode();
                mPhotoListBinding.closeBtn.setVisibility(View.GONE);
                mPhotoListBinding.deleteButton.setVisibility(View.GONE);
        }
    }

    private void deletePhotos() {
        DeletingDialogFragment deletingDialogFragment = DeletingDialogFragment.getInstance(mHashtag.getName(),
                mPhotosAdapter.getSelectedPhotoIds());
        deletingDialogFragment.setOnDeleteCompleteListener(this);
        deletingDialogFragment.show(getSupportFragmentManager(), deletingDialogFragment.getTag());
    }

    @Override
    public void onDeleteComplete() {
        mPhotosAdapter.changeMode();
        loadPhotos();
    }
}
