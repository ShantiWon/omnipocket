package com.example.omnistock.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.omnistock.R;
import com.example.omnistock.model.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartList;
    private OnCartUpdateListener listener;

    public interface OnCartUpdateListener {
        void onTotalChanged();
        void onUpdateQty(int cartId, String action, int position);
        void onDeleteItem(int cartId, int position);
    }

    public CartAdapter(List<CartItem> cartList, OnCartUpdateListener listener) {
        this.cartList = cartList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartList.get(position);

        holder.productName.setText(item.getName());
        holder.productPrice.setText("PHP " + String.format("%.2f", item.getPrice()));
        holder.count.setText(String.valueOf(item.getQty()));

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(item.isChecked());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setChecked(isChecked);
            listener.onTotalChanged();
        });

        if (item.getImageBase64() != null) {
            byte[] decodedString = Base64.decode(item.getImageBase64(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.productImg.setImageBitmap(decodedByte);
        }

        holder.increaseBtn.setOnClickListener(v -> {
            listener.onUpdateQty(item.getCartId(), "increase", holder.getAdapterPosition());
        });

        holder.decreaseBtn.setOnClickListener(v -> {
            if (item.getQty() > 1) {
                listener.onUpdateQty(item.getCartId(), "decrease", holder.getAdapterPosition());
            }
        });

        holder.deleteBtn.setOnClickListener(v -> {
            listener.onDeleteItem(item.getCartId(), holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, count, increaseBtn, decreaseBtn;
        ImageView productImg, deleteBtn;
        CheckBox checkBox;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            count = itemView.findViewById(R.id.count);
            increaseBtn = itemView.findViewById(R.id.increaseBtn);
            decreaseBtn = itemView.findViewById(R.id.decreaseBtn);
            productImg = itemView.findViewById(R.id.productImg);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}