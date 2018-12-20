package kz.iitu.cloudy.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.redmadrobot.inputmask.MaskedTextChangedListener;

import kz.iitu.cloudy.model.Order;
import kz.iitu.cloudy.R;
import kz.iitu.cloudy.databinding.ActivityOrderPhotoBinding;

public class OrderPhotoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String ARG_USERNAME = "username";
    private static final String ARG_PHOTO_ID = "photo_id";
    private static final String ARG_HASHTAG = "hashtag";

    private static final String COLLECTION_ORDERS = "orders";
    private static final String COLLECTION_REQUESTS = "requests";

    private ActivityOrderPhotoBinding mOrderPhotoBinding;

    private String mUsername;
    private String mPhotoId;

    private FirebaseFirestore mFirestore;

    public static void start(Context context, String username,
                             String photoId, String hashtag) {
        Intent starter = new Intent(context, OrderPhotoActivity.class);
        starter.putExtra(ARG_USERNAME, username);
        starter.putExtra(ARG_PHOTO_ID, photoId);
        starter.putExtra(ARG_HASHTAG, hashtag);

        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOrderPhotoBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_order_photo);

        mFirestore = FirebaseFirestore.getInstance();

        mUsername = getIntent().getStringExtra(ARG_USERNAME);
        mPhotoId = getIntent().getStringExtra(ARG_PHOTO_ID);

        mOrderPhotoBinding.sendButton.setOnClickListener(this);
        mOrderPhotoBinding.returnButton.setOnClickListener(this);
        mOrderPhotoBinding.backIcon.setOnClickListener(this);

        final MaskedTextChangedListener listener = new MaskedTextChangedListener(
                "+7 ([000]) [000] [00] [00]",
                mOrderPhotoBinding.phoneInout,
                new MaskedTextChangedListener.ValueListener() {
                    @Override
                    public void onTextChanged(boolean maskFilled, @NonNull final String extractedValue) {

                    }
                }
        );

        mOrderPhotoBinding.phoneInout.addTextChangedListener(listener);
        mOrderPhotoBinding.phoneInout.setHint(listener.placeholder());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_icon:
                finish();
                break;
            case R.id.send_button:
                makeOrder();
                return;
            case R.id.return_button:
                finish();
        }
    }

    private void makeOrder() {
        mOrderPhotoBinding.shimmerLayout.startShimmerAnimation();

        String name = mOrderPhotoBinding.nameInput.getText().toString();
        String phone = mOrderPhotoBinding.phoneInout.getText().toString();

        DocumentReference reference = mFirestore.collection(COLLECTION_ORDERS).document(mUsername);
        reference.set(new Order(name, phone, mPhotoId, getIntent().getStringExtra(ARG_HASHTAG)));

        reference.collection(COLLECTION_REQUESTS).add(new Order(name, phone, mPhotoId,
                getIntent().getStringExtra(ARG_HASHTAG)))
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        mOrderPhotoBinding.shimmerLayout.stopShimmerAnimation();
                        if (task.isSuccessful()) {
                            mOrderPhotoBinding.inputContainer.setVisibility(View.GONE);
                            mOrderPhotoBinding.successContainer.setVisibility(View.VISIBLE);
                        }
                    }
                });

    }
}
