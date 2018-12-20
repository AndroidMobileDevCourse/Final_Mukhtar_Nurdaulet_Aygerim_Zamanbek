package kz.iitu.cloudy.ui.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import kz.iitu.cloudy.model.Order;
import kz.iitu.cloudy.ui.adapter.OrdersAdapter;
import kz.iitu.cloudy.model.Photo;
import kz.iitu.cloudy.utils.PreferenceUtils;
import kz.iitu.cloudy.R;
import kz.iitu.cloudy.model.User;
import kz.iitu.cloudy.databinding.FragmentOrdersBinding;

/**
 * Created by 1506k on 5/18/18.
 */

public class OrdersFragment extends Fragment implements OrdersAdapter.OnCallClickedListener {

    private static final String COLLECTION_ORDERS = "orders";
    private static final String COLLECTION_REQUESTS = "requests";
    private static final String COLLECTION_HASHTAGS = "hashtags";
    private static final String COLLECTION_PHOTOS = "photos";

    private FragmentOrdersBinding mOrdersBinding;

    private OrdersAdapter mOrdersAdapter;

    private FirebaseFirestore mFirestore;

    private User mCurrentUser;

    public static OrdersFragment getInstance() {
        return new OrdersFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mOrdersBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_orders, container, false);
        return mOrdersBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFirestore = FirebaseFirestore.getInstance();

        mCurrentUser = PreferenceUtils.getCurrentUser(getContext());

        mOrdersBinding.ordersList.setLayoutManager(new LinearLayoutManager(getContext()));
        mOrdersAdapter = new OrdersAdapter();
        mOrdersAdapter.setOnCallClickedListener(this);
        mOrdersBinding.ordersList.setAdapter(mOrdersAdapter);

        loadOrders();
    }

    private void loadOrders() {
        mFirestore.collection(COLLECTION_ORDERS).document(mCurrentUser.getUsername())
                .collection(COLLECTION_REQUESTS).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        mOrdersBinding.progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot: task.getResult()) {
                                final Order order = documentSnapshot.toObject(Order.class);

                                mFirestore.collection(COLLECTION_HASHTAGS).document(order.getHashtag())
                                        .collection(COLLECTION_PHOTOS).document(order.getPhotoId()).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        Photo photo = document.toObject(Photo.class);
                                                        order.setPhotoUrl(photo.getUrl());

                                                        mOrdersAdapter.addOrder(order);
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    @Override
    public void onCallClicked(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
        startActivity(intent);
    }
}
