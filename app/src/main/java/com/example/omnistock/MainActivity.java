package com.example.omnistock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    MaterialButton startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        startBtn = findViewById(R.id.startBtn);
        startBtn.setOnClickListener(v->{
            startActivity(new Intent(this, AccountLayout.class));
            finish();
        });
        SharedPreferences sharedPreferences = getSharedPreferences("OmniStockPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);
        int isAdmin = sharedPreferences.getInt("is_admin", -1);

        // 3. If logged in (userId is NOT -1), redirect to your Dashboard or Home
        if (userId != -1) {
            if (isAdmin == 1) {
                startActivity(new Intent(this, AdminLayout.class));
            } else {
                startActivity(new Intent(this, ShopLayout.class));
            }
            finish();
        }
    }
}