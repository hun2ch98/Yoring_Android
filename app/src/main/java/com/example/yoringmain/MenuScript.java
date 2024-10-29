package com.example.yoringmain;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.widget.Toolbar;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MenuScript extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_script);

        toolbar = findViewById(R.id.toolbar_a);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(" ");

        // Apply WindowInsets to avoid overlap with status bar
        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(view.getPaddingLeft(), insets.top, view.getPaddingRight(), view.getPaddingBottom());
            return WindowInsetsCompat.CONSUMED;
        });

        // 간단한 추천 버튼
        Button btn_simple_recommand = (Button) findViewById(R.id.btn_simple_recommand);
        btn_simple_recommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SimepleRecommend.class);
                startActivity(intent);
            }
        });

        // 자세한 추천 버튼
        Button btn_detail_recommand = (Button) findViewById(R.id.btn_detail_recommand);
        btn_detail_recommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DetailRecommend1.class);
                startActivity(intent);
            }
        });

        // SKT 버튼
        Button btn_skt = (Button) findViewById(R.id.btn_skt);
        btn_skt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.tworld.co.kr/membership/benefit/brand"));
                startActivity(browserIntent);
            }
        });

        // LG 버튼
        Button btn_lg = (Button) findViewById(R.id.btn_lg);
        btn_lg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.lguplus.com/benefit-membership?urcMbspDivsCd=01&urcMbspBnftDivsCd=02"));
                startActivity(browserIntent);
            }
        });

        // KT 버튼
        Button btn_kt = (Button) findViewById(R.id.btn_kt);
        btn_kt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.membership.kt.com/discount/partner/s_PartnerList.do"));
                startActivity(browserIntent);
            }
        });

        // 요금제 전체 조회 버튼
        Button btn_all_search = (Button) findViewById(R.id.btn_all_search);
        btn_all_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AllPlan.class);
                startActivity(intent);
            }
        });

        // 현재 요금제 이동 버튼
        Button btn_my_choice_list = (Button) findViewById(R.id.btn_my_choice_list);
        btn_my_choice_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChoosePlan.class);
                startActivity(intent);
            }
        });
    }
}
