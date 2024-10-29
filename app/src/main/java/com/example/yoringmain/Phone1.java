package com.example.yoringmain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.view.MenuItem;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Phone1 extends AppCompatActivity {
    private ImageButton imbApple, img15ProMax, img15Pro, img15Plus, img15, img16ProMax, img16Pro, img16Plus, img16, imgSE,
            imbSamsung, imbGalaxyS24Ultra, imbGalaxyS24Plus, imbGalaxyS24, imbGalaxyS23Ultra, imbGalaxyS23Plus,imbGalaxyS23, imbGalaxyS23FE,
            imbGalaxyZFold5, imbGalaxyZFlip5, imbGalaxyZFold6, imbGalaxyZFlip6, imbGalaxyQuantum4,imbGalaxyA15, imbGalaxyA25, imbGalaxyA24;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private LinearLayout linearLayout1, linearLayout2, linearLayout3, linearSamsung1, linearSamsung2, linearSamsung3, linearSamsung4, linearSamsung5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone1);

        toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(" ");


        linearLayout1 = findViewById(R.id.Linear1);
        linearLayout2 = findViewById(R.id.Linear2);
        linearLayout3 = findViewById(R.id.Linear3);
        linearSamsung1 = findViewById(R.id.linear_samsung1);
        linearSamsung2 = findViewById(R.id.linear_samsung2);
        linearSamsung3 = findViewById(R.id.linear_samsung3);
        linearSamsung4 = findViewById(R.id.linear_samsung4);
        linearSamsung5 = findViewById(R.id.linear_samsung5);

        imbApple = findViewById(R.id.imb_apple);
        imbSamsung = findViewById(R.id.imb_samsung);
        img16ProMax = findViewById(R.id.imb_iphone1);
        img16Pro = findViewById(R.id.imb_iphone2);
        img16Plus = findViewById(R.id.imb_iphone3);
        img16 = findViewById(R.id.imb_iphone4);
        img15ProMax = findViewById(R.id.imb_iphone5);
        img15Pro = findViewById(R.id.imb_iphone6);
        img15Plus = findViewById(R.id.imb_iphone7);
        img15 = findViewById(R.id.imb_iphone8);
        imgSE = findViewById(R.id.imb_iphone9);

        imbGalaxyS24Ultra = findViewById(R.id.imb_galaxy_s24_ultra);
        imbGalaxyS24Plus = findViewById(R.id.imb_galaxy_s24_plus);
        imbGalaxyS24 = findViewById(R.id.imb_galaxy_s24);
        imbGalaxyS23Ultra = findViewById(R.id.imb_galaxy_s23_ultra);
        imbGalaxyS23Plus = findViewById(R.id.imb_galaxy_s23_plus);
        imbGalaxyS23 = findViewById(R.id.imb_galaxy_s23);
        imbGalaxyS23FE = findViewById(R.id.imb_galaxy_s23_fe);
        imbGalaxyZFold5 = findViewById(R.id.imb_galaxy_z_fold5);
        imbGalaxyZFold6 = findViewById(R.id.imb_galaxy_z_fold6);
        imbGalaxyZFlip5 = findViewById(R.id.imb_galaxy_z_flip5);
        imbGalaxyZFlip6 = findViewById(R.id.imb_galaxy_z_flip6);
        imbGalaxyA15 = findViewById(R.id.imb_galaxy_a15);
        imbGalaxyA24 = findViewById(R.id.imb_galaxy_a24);
        imbGalaxyA25 = findViewById(R.id.imb_galaxy_a25);
        imbGalaxyQuantum4 = findViewById(R.id.imb_galaxy_quantum4);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_chain);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_chain:
                        return true;
                    case R.id.nav_all_menu:
                        startActivity(new Intent(Phone1.this, MenuScript.class));
                        return true;
                    case R.id.nav_home:
                        startActivity(new Intent(Phone1.this, MainPage.class));
                        return true;
                    case R.id.nav_telsearch:
                        startActivity(new Intent(Phone1.this, AllPlan.class));
                        return true;
                    case R.id.nav_mypage:
                        startActivity(new Intent(Phone1.this, MyPage.class));
                        return true;
                    default:
                        return false;
                }
            }
        });

        imbApple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout1.setVisibility(View.VISIBLE);
                linearLayout2.setVisibility(View.VISIBLE);
                linearLayout3.setVisibility(View.VISIBLE);
                linearSamsung1.setVisibility(View.GONE);
                linearSamsung2.setVisibility(View.GONE);
                linearSamsung3.setVisibility(View.GONE);
                linearSamsung4.setVisibility(View.GONE);
                linearSamsung5.setVisibility(View.GONE);
                changeImage(imbApple);
            }
        });

        imbSamsung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout1.setVisibility(View.GONE);
                linearLayout2.setVisibility(View.GONE);
                linearLayout3.setVisibility(View.GONE);
                linearSamsung1.setVisibility(View.VISIBLE);
                linearSamsung2.setVisibility(View.VISIBLE);
                linearSamsung3.setVisibility(View.VISIBLE);
                linearSamsung4.setVisibility(View.VISIBLE);
                linearSamsung5.setVisibility(View.VISIBLE);
                changeImage(imbSamsung);
            }
        });

        img15ProMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Phone1.this, Phone2.class);
                intent.putExtra("modelName", "아이폰 15 Pro Max");
                startActivity(intent);
            }
        });

        img15Pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Phone1.this, Phone2.class);
                intent.putExtra("modelName", "아이폰 15 Pro");
                startActivity(intent);
            }
        });

        img15Plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Phone1.this, Phone2.class);
                intent.putExtra("modelName", "아이폰 15 Plus");
                startActivity(intent);
            }
        });

        img15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Phone1.this, Phone2.class);
                intent.putExtra("modelName", "아이폰 15");
                startActivity(intent);
            }
        });

        img16ProMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Phone1.this, Phone2.class);
                intent.putExtra("modelName", "아이폰 16 Pro Max");
                startActivity(intent);
            }
        });

        img16Pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Phone1.this, Phone2.class);
                intent.putExtra("modelName", "아이폰 16 Pro");
                startActivity(intent);
            }
        });

        img16Plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Phone1.this, Phone2.class);
                intent.putExtra("modelName", "아이폰 16 Plus");
                startActivity(intent);
            }
        });

        img16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Phone1.this, Phone2.class);
                intent.putExtra("modelName", "아이폰 16");
                startActivity(intent);
            }
        });

        imgSE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Phone1.this, Phone2.class);
                intent.putExtra("modelName", "아이폰 SE(3세대)");
                startActivity(intent);
            }
        });

        imbGalaxyS24Ultra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Phone1.this, Phone2.class);
                intent.putExtra("modelName", "갤럭시 S24 Ultra");
                startActivity(intent);
            }
        });

        imbGalaxyS24Plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Phone1.this, Phone2.class);
                intent.putExtra("modelName", "갤럭시 S24+");
                startActivity(intent);
            }
        });

        imbGalaxyS24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Phone1.this, Phone2.class);
                intent.putExtra("modelName", "갤럭시 S24");
                startActivity(intent);
            }
        });

        imbGalaxyS23Ultra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Phone1.this, Phone2.class);
                intent.putExtra("modelName", "갤럭시 S23 Ultra");
                startActivity(intent);
            }
        });

        imbGalaxyS23Plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Phone1.this, Phone2.class);
                intent.putExtra("modelName", "갤럭시 S23+");
                startActivity(intent);
            }
        });

        imbGalaxyS23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Phone1.this, Phone2.class);
                intent.putExtra("modelName", "갤럭시 S23");
                startActivity(intent);
            }
        });

        imbGalaxyS23FE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Phone1.this, Phone2.class);
                intent.putExtra("modelName", "갤럭시 S23 FE");
                startActivity(intent);
            }
        });

        imbGalaxyZFlip5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Phone1.this, Phone2.class);
                intent.putExtra("modelName", "갤럭시 Z 플립5");
                startActivity(intent);
            }
        });

        imbGalaxyZFlip6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Phone1.this, Phone2.class);
                intent.putExtra("modelName", "갤럭시 Z 플립6");
                startActivity(intent);
            }
        });

        imbGalaxyZFold5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Phone1.this, Phone2.class);
                intent.putExtra("modelName", "갤럭시 Z 폴드5");
                startActivity(intent);
            }
        });

        imbGalaxyZFold6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Phone1.this, Phone2.class);
                intent.putExtra("modelName", "갤럭시 Z 폴드6");
                startActivity(intent);
            }
        });

        imbGalaxyQuantum4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Phone1.this, Phone2.class);
                intent.putExtra("modelName", "갤럭시 퀀텀4");
                startActivity(intent);
            }
        });

        imbGalaxyA25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Phone1.this, Phone2.class);
                intent.putExtra("modelName", "갤럭시 A25");
                startActivity(intent);
            }
        });

        imbGalaxyA15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Phone1.this, Phone2.class);
                intent.putExtra("modelName", "갤럭시 A15");
                startActivity(intent);
            }
        });

        imbGalaxyA24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Phone1.this, Phone2.class);
                intent.putExtra("modelName", "갤럭시 A24");
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeImage(ImageButton button) {
        if (button == imbApple) {
            if (imbSamsung.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.choosesamsung).getConstantState()) {
                imbSamsung.setImageResource(R.drawable.unsamsung);
            }
            button.setImageResource(R.drawable.chooseapple);
        } else if (button == imbSamsung) {
            if (imbApple.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.chooseapple).getConstantState()) {
                imbApple.setImageResource(R.drawable.unapple);
            }
            button.setImageResource(R.drawable.choosesamsung);
        }
    }

}