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

public class AdminLayout extends AppCompatActivity {
    LinearLayout homeBtn, addBtn, accountBtn;
    ImageView homeImg, addImg, accountImg;
    TextView homeTxt, addTxt, accountTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        homeImg = findViewById(R.id.homeImg);
        addImg = findViewById(R.id.addImg);
        accountImg = findViewById(R.id.accountImg);

        homeTxt = findViewById(R.id.homeTxt);
        addTxt = findViewById(R.id.addTxt);
        accountTxt = findViewById(R.id.accountTxt);

        homeBtn = findViewById(R.id.homeBtn);
        addBtn = findViewById(R.id.addBtn);
        accountBtn = findViewById(R.id.accountBtn);

        homeBtn.setOnClickListener(v-> {
            loadFragment(new FragmentCharts());
        });
        addBtn.setOnClickListener(v-> {
            loadFragment(new FragmentAdd());
        });
        accountBtn.setOnClickListener(v-> {
            loadFragment(new FragmentAdmin());
        });
        getSupportFragmentManager().addOnBackStackChangedListener(() -> setButton());
        setButton();
    }
    public void resetButtons(){
        int white = ContextCompat.getColor(this, R.color.white);
        homeImg.setColorFilter(white);
        addImg.setColorFilter(white);
        accountImg.setColorFilter(white);

        homeTxt.setTextColor(white);
        addTxt.setTextColor(white);
        accountTxt.setTextColor(white);
    }
    public void setButton() {
        resetButtons();
        int selectedColor = ContextCompat.getColor(this, R.color.selected);

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

        if (currentFragment instanceof FragmentCharts) {
            homeImg.setColorFilter(selectedColor);
            homeTxt.setTextColor(selectedColor);
        } else if (currentFragment instanceof FragmentAdd) {
            addImg.setColorFilter(selectedColor);
            addTxt.setTextColor(selectedColor);
        } else if (currentFragment instanceof FragmentAdmin) {
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