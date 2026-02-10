package com.example.omnistock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.omnistock.adapter.OrderAdapter;
import com.example.omnistock.model.CartItem;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfirmOrder extends AppCompatActivity implements OrderAdapter.OnCartUpdateListener {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<CartItem> orderItems = new ArrayList<>();
    private TextView totalTextView, subtotalTextView, shippingFeeTextView;
    private TextView shipName, shipAddress;
    private MaterialButton placeOrderBtn;
    private double currentTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        totalTextView = findViewById(R.id.totalPrice);
        subtotalTextView = findViewById(R.id.subtotalPrice);
        shippingFeeTextView = findViewById(R.id.shippingFee);
        shipName = findViewById(R.id.shipName);
        shipAddress = findViewById(R.id.shipAddress);
        placeOrderBtn = findViewById(R.id.placeOrderBtn);

        ArrayList<CartItem> cartItems = getIntent().getParcelableArrayListExtra("cart_items");
        if (cartItems != null) {
            orderItems.addAll(cartItems);
        }

        adapter = new OrderAdapter(orderItems, this);
        recyclerView.setAdapter(adapter);

        loadUserData();

        calculateTotal();

        placeOrderBtn.setOnClickListener(v -> placeOrder());
    }

    private void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("OmniStockPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        String url = "http://10.0.2.2/omnistock/getUser.php?user_id=" + userId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            String userName = data.getString("name");
                            shipName.setText(userName);
                            shipAddress.setText(data.getString("address"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error loading shipping info", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void calculateTotal() {
        double subtotal = 0;
        for (CartItem item : orderItems) {
            subtotal += (item.getPrice() * item.getQty());
        }
        double shippingFee = 45.00;
        currentTotal = subtotal + shippingFee;
        
        subtotalTextView.setText("PHP " + String.format("%.2f", subtotal));
        shippingFeeTextView.setText("PHP " + String.format("%.2f", shippingFee));
        totalTextView.setText("PHP " + String.format("%.2f", currentTotal));
    }

    private void placeOrder() {
        SharedPreferences sharedPreferences = getSharedPreferences("OmniStockPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        if (orderItems.isEmpty()) {
            Toast.makeText(this, "No items to order", Toast.LENGTH_SHORT).show();
            return;
        }

        placeOrderBtn.setEnabled(false);
        placeOrderBtn.setText("Processing...");

        JSONArray itemsArray = new JSONArray();
        try {
            for (CartItem item : orderItems) {
                JSONObject itemObj = new JSONObject();
                itemObj.put("product_id", item.getProductId());
                itemObj.put("qty", item.getQty());
                itemObj.put("price", item.getPrice());
                itemsArray.put(itemObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "http://10.0.2.2/omnistock/placeOrder.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        if (status.equals("success")) {
                            Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            placeOrderBtn.setEnabled(true);
                            placeOrderBtn.setText("Place Order");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        placeOrderBtn.setEnabled(true);
                    }
                },
                error -> {
                    Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    placeOrderBtn.setEnabled(true);
                    placeOrderBtn.setText("Place Order");
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("total_amount", String.valueOf(currentTotal));
                params.put("items", itemsArray.toString());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    public void onUpdateQty(int cartId, String action, int position) {
        CartItem item = orderItems.get(position);
        if (action.equals("increase")) {
            item.setQty(item.getQty() + 1);
        } else if (action.equals("decrease") && item.getQty() > 1) {
            item.setQty(item.getQty() - 1);
        }
        adapter.notifyItemChanged(position);
        calculateTotal();
    }
}