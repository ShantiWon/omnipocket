package com.example.omnistock.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.omnistock.ProductInfo;
import com.example.omnistock.R;
import com.example.omnistock.model.Product;

import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ProductViewHolder> {

    private List<Product> productList;

    public ShopAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.name.setText(product.getName());
        holder.price.setText(product.getPriceFormatted());

        if (product.getImageBase64() != null && !product.getImageBase64().isEmpty()) {
            byte[] decodedString = Base64.decode(product.getImageBase64(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.img.setImageBitmap(decodedByte);
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductInfo.class);
            intent.putExtra("p_id", product.getId());
            intent.putExtra("p_name", product.getName());
            intent.putExtra("p_price", product.getPriceFormatted());
            intent.putExtra("p_image", product.getImageBase64());
            intent.putExtra("p_desc", product.getDescription());
            intent.putExtra("p_stock", product.getStockQty());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        ImageView img;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.productName);
            price = itemView.findViewById(R.id.productPrice);
            img = itemView.findViewById(R.id.productImg);
        }
    }
}