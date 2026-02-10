package com.example.omnistock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.omnistock.model.CartItem;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductInfo extends AppCompatActivity {
    TextView nameView, priceView, stockView, descView;
    ImageView productImageView, cartBtn;
    MaterialButton backBtn, buyBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String name = getIntent().getStringExtra("p_name");
        String price = getIntent().getStringExtra("p_price");
        String desc = getIntent().getStringExtra("p_desc");
        String imgBase64 = getIntent().getStringExtra("p_image");
        int stock = getIntent().getIntExtra("p_stock", 0);
        int productId = getIntent().getIntExtra("p_id", 0);

        nameView = findViewById(R.id.name);
        priceView = findViewById(R.id.price);
        stockView = findViewById(R.id.stock);
        descView = findViewById(R.id.description);
        productImageView = findViewById(R.id.productImgView);
        backBtn = findViewById(R.id.backBtn);
        cartBtn = findViewById(R.id.cartBtn);
        buyBtn = findViewById(R.id.buyBtn);

        nameView.setText(name);
        priceView.setText(price);
        descView.setText(desc);
        stockView.setText(stock + " Stocks");

        cartBtn.setOnClickListener(v -> addToCart(productId));
        
        buyBtn.setOnClickListener(v -> {
            // Parse price string (remove "PHP " and commas if present)
            double priceValue = 0.0;
            try {
                String cleanPrice = price.replace("PHP", "").replace(",", "").trim();
                priceValue = Double.parseDouble(cleanPrice);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Create a CartItem for this product
            CartItem item = new CartItem(0, productId, name, priceValue, imgBase64, 1);
            
            // Create ArrayList and add the item
            ArrayList<CartItem> items = new ArrayList<>();
            items.add(item);
            
            // Navigate to ConfirmOrder
            Intent intent = new Intent(ProductInfo.this, ConfirmOrder.class);
            intent.putParcelableArrayListExtra("cart_items", items);
            startActivity(intent);
        });

        if (imgBase64 != null && !imgBase64.isEmpty()) {
            byte[] decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            productImageView.setImageBitmap(decodedByte);
        }

        backBtn.setOnClickListener(v -> finish());
    }
    private void addToCart(int productId) {
        String url = "http://10.0.2.2/omnistock/addToCart.php";
        SharedPreferences sharedPreferences = getSharedPreferences("OmniStockPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Added to Cart!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ProductInfo.this, ShopLayout.class);
                    intent.putExtra("target_fragment", "cart");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                },
                error -> Toast.makeText(this, "Error adding to cart", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("product_id", String.valueOf(productId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}