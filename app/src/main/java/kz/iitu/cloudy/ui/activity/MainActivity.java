package kz.iitu.cloudy.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import kz.iitu.cloudy.model.Hashtag;
import kz.iitu.cloudy.utils.PreferenceUtils;
import kz.iitu.cloudy.R;
import kz.iitu.cloudy.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, FloatingSearchView.OnQueryChangeListener,
        FloatingSearchView.OnSearchListener {

    private static final String COLLECTION_HASHTAGS = "hashtags";

    private ActivityMainBinding mActivityMainBinding;

    private FirebaseFirestore mFirestore;

    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PreferenceUtils.isLoggedIn(this)) {
            HomeActivity.start(this);
            finish();
        }

        mActivityMainBinding
                = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mFirestore = FirebaseFirestore.getInstance();

        mActivityMainBinding.photographButton.setOnClickListener(this);
        mActivityMainBinding.searchView.setOnQueryChangeListener(this);
        mActivityMainBinding.searchView.setOnSearchListener(this);
    }

    @Override
    public void onClick(View view) {
        AuthActivity.start(this);
    }

    @Override
    public void onSearchTextChanged(String oldQuery, final String newQuery) {
        if (TextUtils.isEmpty(newQuery)) return;

        final List<Hashtag> hashtagList = new ArrayList<>();

        mActivityMainBinding.searchView.showProgress();

        mFirestore.collection(COLLECTION_HASHTAGS).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        mActivityMainBinding.searchView.hideProgress();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot: task.getResult()) {
                                hashtagList.add(snapshot.toObject(Hashtag.class));
                            }

                            List<Hashtag> filtered = new ArrayList<>();

                            for (Hashtag hashtag: hashtagList) {
                                Log.d("taaag", hashtag.toString());
                                if (hashtag.getName().toLowerCase().contains(newQuery.toLowerCase())) {
                                    filtered.add(hashtag);
                                }
                            }

                            mActivityMainBinding.searchView.swapSuggestions(filtered);
                        }
                    }
                });
    }

    @Override
    public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
        hideKeyboard();

        Hashtag hashtag = (Hashtag) searchSuggestion;
        PhotoListActivity.start(this, hashtag, 0, false);
    }

    @Override
    public void onSearchAction(String currentQuery) {

    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
