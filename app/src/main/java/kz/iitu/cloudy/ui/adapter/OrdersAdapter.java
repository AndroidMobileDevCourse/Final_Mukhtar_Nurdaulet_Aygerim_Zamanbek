package kz.iitu.cloudy.ui.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kz.iitu.cloudy.R;
import kz.iitu.cloudy.databinding.LayoutItemOrderBinding;
import kz.iitu.cloudy.model.Order;

/**
 * Created by 1506k on 5/18/18.
 */

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private List<Order> mOrders;

    private OnCallClickedListener mOnCallClickedListener;

    public OrdersAdapter() {
        mOrders = new ArrayList<>();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemOrderBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.layout_item_order, parent, false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bindOrder(mOrders.get(position));
    }

    @Override
    public int getItemCount() {
        return mOrders.size();
    }

    public void addOrder(Order order) {
        mOrders.add(order);

        notifyDataSetChanged();
    }

    public void setOnCallClickedListener(OnCallClickedListener onCallClickedListener) {
        mOnCallClickedListener = onCallClickedListener;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private LayoutItemOrderBinding mOrderBinding;

        public OrderViewHolder(LayoutItemOrderBinding orderBinding) {
            super(orderBinding.getRoot());

            mOrderBinding = orderBinding;
        }

        public void bindOrder(@NonNull Order order) {
            Picasso.get()
                    .load(order.getPhotoUrl())
                    .into(mOrderBinding.preview);

            mOrderBinding.setOrder(order);

            mOrderBinding.callBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnCallClickedListener.onCallClicked(mOrders.get(getAdapterPosition()).getPhone());
        }
    }

    public interface OnCallClickedListener {
        void onCallClicked(String number);
    }
}
