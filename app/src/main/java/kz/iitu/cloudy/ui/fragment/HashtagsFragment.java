package kz.iitu.cloudy.ui.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import kz.iitu.cloudy.model.Hashtag;
import kz.iitu.cloudy.ui.adapter.HashtagsAdapter;
import kz.iitu.cloudy.utils.PreferenceUtils;
import kz.iitu.cloudy.R;
import kz.iitu.cloudy.model.User;
import kz.iitu.cloudy.databinding.FragmentHashtagsBinding;
import kz.iitu.cloudy.ui.activity.PhotoListActivity;

/**
 * Created by 1506k on 5/17/18.
 */

public class HashtagsFragment extends Fragment implements HashtagsAdapter.OnHashtagClickedListener {

    private static final String COLLECTION_DATA = "data";
    private static final String COLLECTION_HASHTAGS = "hashtags";
    private static final String COLLECTION_PHOTOS = "photos";

    private FragmentHashtagsBinding mHashtagsBinding;

    private HashtagsAdapter mHashtagsAdapter;
    private FirebaseFirestore mFirestore;

    private User mCurrentUser;

    public static HashtagsFragment getInstance() {
        return new HashtagsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mHashtagsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_hashtags, container, false);
        return mHashtagsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCurrentUser = PreferenceUtils.getCurrentUser(getContext());

        mFirestore = FirebaseFirestore.getInstance();

        mHashtagsAdapter = new HashtagsAdapter();
        mHashtagsAdapter.setListener(this);
        mHashtagsBinding.hashtagList.setLayoutManager(new LinearLayoutManager(getContext()));
        mHashtagsBinding.hashtagList.setAdapter(mHashtagsAdapter);

        loadHashTags();
    }

    private void loadHashTags() {
        final CollectionReference reference = mFirestore.collection(COLLECTION_DATA)
                .document(mCurrentUser.getUsername())
                .collection(COLLECTION_HASHTAGS);

                reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                mHashtagsBinding.progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot snapshot: task.getResult()) {
                        reference.document(snapshot.getId()).collection(COLLECTION_PHOTOS).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    mHashtagsAdapter.addHashtag(new Hashtag(snapshot.getId(), task.getResult().size()));
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onHashtagClicked(@NonNull Hashtag hashtag) {
        PhotoListActivity.start(getContext(), hashtag, hashtag.getPhotoCount(), true);
    }
}
