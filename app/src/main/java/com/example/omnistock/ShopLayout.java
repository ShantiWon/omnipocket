package com.example.omnistock;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class ShopLayout extends AppCompatActivity {
    LinearLayout homeBtn, cartBtn, accountBtn;
    ImageView homeImg, cartImg, accountImg;
    TextView homeTxt, cartTxt, accountTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        homeImg = findViewById(R.id.homeImg);
        cartImg = findViewById(R.id.cartImg);
        accountImg = findViewById(R.id.accountImg);

        homeTxt = findViewById(R.id.homeTxt);
        cartTxt = findViewById(R.id.cartTxt);
        accountTxt = findViewById(R.id.accountTxt);

        homeBtn = findViewById(R.id.homeBtn);
        cartBtn = findViewById(R.id.cartBtn);
        accountBtn = findViewById(R.id.accountBtn);

        homeBtn.setOnClickListener(v-> {
            loadFragment(new FragmentShop());
        });
        cartBtn.setOnClickListener(v-> {
            loadFragment(new FragmentCart());
        });
        accountBtn.setOnClickListener(v-> {
            loadFragment(new FragmentAccount());
        });
        getSupportFragmentManager().addOnBackStackChangedListener(() -> setButton());
        setButton();
    }
    public void resetButtons(){
        int white = ContextCompat.getColor(this, R.color.white);
        homeImg.setColorFilter(white);
        cartImg.setColorFilter(white);
        accountImg.setColorFilter(white);

        homeTxt.setTextColor(white);
        cartTxt.setTextColor(white);
        accountTxt.setTextColor(white);
    }
    public void setButton() {
        resetButtons();
        int selectedColor = ContextCompat.getColor(this, R.color.selected);

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

        if (currentFragment instanceof FragmentShop) {
            homeImg.setColorFilter(selectedColor);
            homeTxt.setTextColor(selectedColor);
        } else if (currentFragment instanceof FragmentCart) {
            cartImg.setColorFilter(selectedColor);
            cartTxt.setTextColor(selectedColor);
        } else if (currentFragment instanceof FragmentAccount) {
            accountImg.setColorFilter(selectedColor);
            accountTxt.setTextColor(selectedColor);
        }
    }
    private void loadFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commitNow();

        setButton();
    }
}