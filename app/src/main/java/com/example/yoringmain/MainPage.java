package com.example.yoringmain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainPage extends AppCompatActivity {
    ImageButton imbRecommend, imbDetailRecommend, imbNews1, imbNews2, imbNews3, imbSktMembership, imbKtMembership, imbLgMembership;
    TextView tvSubYet, tvCurrentSub, tvCurrentSubName;
    DatabaseReference userSubscriptionsRef;
    ImageView imgAd;

    private BottomNavigationView bottomNavigationView;

    private int currentIndex = 0;
    private final String[] adImages = {"ad1", "ad2", "ad3"}; // Drawable resource names
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        imgAd = findViewById(R.id.img_ad);
        ViewPager2 viewPager2 = findViewById(R.id.viewPager);

        startImageSlider();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        return true;
                    case R.id.nav_all_menu:
                        startActivity(new Intent(MainPage.this, MenuScript.class));
                        return true;
                    case R.id.nav_telsearch:
                        startActivity(new Intent(MainPage.this, AllPlan.class));
                        return true;
                    case R.id.nav_chain:
                        startActivity(new Intent(MainPage.this, Phone1.class));
                        return true;
                    case R.id.nav_mypage:
                        startActivity(new Intent(MainPage.this, MyPage.class));
                        return true;
                    default:
                        return false;
                }
            }
        });

        imbRecommend = findViewById(R.id.btn_recommend);
        imbDetailRecommend = findViewById(R.id.btn_detail_recommend);
        imbKtMembership = findViewById(R.id.imb_kt_membership);
        imbSktMembership = findViewById(R.id.imb_skt_membership);
        imbLgMembership = findViewById(R.id.imb_lg_membership);
        imbNews1 = findViewById(R.id.imb_news1);
        imbNews2 = findViewById(R.id.imb_news2);
        imbNews3 = findViewById(R.id.imb_news3);

        tvSubYet = findViewById(R.id.tv_sub_yet);
        tvCurrentSub = findViewById(R.id.tv_current_sub);
        tvCurrentSubName = findViewById(R.id.tv_current_sub_name);

        Intent intent = getIntent();
        if (intent != null) {
            String selectedSubscription = intent.getStringExtra("selected_subscription");
            if (selectedSubscription != null) {
                tvSubYet.setVisibility(View.GONE);
                tvCurrentSub.setVisibility(View.VISIBLE);
                tvCurrentSubName.setVisibility(View.VISIBLE);
                tvCurrentSubName.setText(selectedSubscription);
            }
        }

        String userToken = getUserToken();
        if (userToken != null) {
            userSubscriptionsRef = FirebaseDatabase.getInstance().getReference("UserSubscriptions").child(userToken);
            userSubscriptionsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String selectedSubscription = dataSnapshot.getValue(String.class);
                        if (selectedSubscription != null) {
                            tvSubYet.setVisibility(View.GONE);
                            tvCurrentSub.setVisibility(View.VISIBLE);
                            tvCurrentSubName.setVisibility(View.VISIBLE);
                            tvCurrentSubName.setText(selectedSubscription);
                        }
                    } else {
                        tvSubYet.setVisibility(View.VISIBLE);
                        tvCurrentSub.setVisibility(View.GONE);
                        tvCurrentSubName.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Firebase", "사용자의 요금제를 읽어오는 데 실패했습니다: " + databaseError.getMessage());
                }
            });
        } else {
            Log.e("Firebase", "사용자의 토큰 값을 가져오는 데 실패했습니다.");
        }


        tvSubYet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this, ChoosePlan.class);
                startActivity(intent);
            }
        });

        tvCurrentSubName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this, ChoosePlan.class);
                startActivity(intent);
            }
        });

        imbRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this, SimepleRecommend.class);
                startActivity(intent);
            }
        });


        imbDetailRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this, DetailRecommend1.class);
                startActivity(intent);
            }
        });

        imbSktMembership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.tworld.co.kr/membership/benefit/brand"));
                startActivity(browserIntent);
            }
        });

        imbKtMembership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.membership.kt.com/discount/partner/s_PartnerList.do"));
                startActivity(browserIntent);
            }
        });

        imbLgMembership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.lguplus.com/benefit-membership?urcMbspDivsCd=01&urcMbspBnftDivsCd=02"));
                startActivity(browserIntent);
            }
        });

        imbNews1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://n.news.naver.com/article/028/0002685521?sid=101"));
                startActivity(browserIntent);
            }
        });

        imbNews2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://n.news.naver.com/article/003/0012493379?sid=105"));
                startActivity(browserIntent);
            }
        });

        imbNews3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://n.news.naver.com/article/015/0004965728?sid=105"));
                startActivity(browserIntent);
            }
        });

    }

    private void startImageSlider() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Change image
                int resId = getResources().getIdentifier(adImages[currentIndex], "drawable", getPackageName());
                imgAd.setImageResource(resId);
                currentIndex = (currentIndex + 1) % adImages.length;
                handler.postDelayed(this, 3000);
            }
        }, 3000);
    }

    private String getUserToken() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return null;
        }
    }

}