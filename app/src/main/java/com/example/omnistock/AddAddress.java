package com.example.omnistock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddAddress extends AppCompatActivity {
    MaterialButton saveBtn;
    EditText addressInpt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_address);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        saveBtn = findViewById(R.id.saveBtn);
        addressInpt = findViewById(R.id.addressInpt);

        saveBtn.setOnClickListener(v->{
            SharedPreferences sharedPreferences = getSharedPreferences("OmniStockPrefs", MODE_PRIVATE);
            int userId = sharedPreferences.getInt("user_id", -1);
            String address = addressInpt.getText().toString().trim();
            uploadAddress(userId, address);
        });
    }
    public void uploadAddress(int userId, String address) {
        String url = "http://10.0.2.2/omnistock/addAddress.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            Toast.makeText(this, "Profile Completed!", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(AddAddress.this, ShopLayout.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            );
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Connection Error", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("address", address);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}