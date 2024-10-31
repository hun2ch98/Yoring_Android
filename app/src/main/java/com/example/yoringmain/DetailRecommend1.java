package com.example.yoringmain;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetailRecommend1 extends AppCompatActivity {
    Toolbar toolbar;
    Button btnHandOperated, btnAutomatic, btnFind;
    TextView tvPrintData;
    SeekBar seekBarDataUsage;
    private Spinner spinnerFamily1, spinnerFamily2, spinnerFamily3, spinnerFamily4;
    private int sktCount = 0, ktCount = 0, lgCount = 0;
    ImageButton imbNetflix, imbTving, imbWavve, imbDisneyPlus, imbYoutube;
    private boolean isNetflixPicked = false, isTvingPicked = false, isWavvePicked = false, isDisneyPlusPicked = false, isYoutubePicked = false;
    private static final float TEXT_SIZE_SELECTED = 12f;
    private static final float TEXT_SIZE_DEFAULT = 10f;
    List<String> telecomCompanies = new ArrayList<>();
    private int selectedCount = 0;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_recommend1);

        toolbar = findViewById(R.id.toolbar7);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("자세한 요금제 추천");

        btnAutomatic = findViewById(R.id.btn_automatic);
        btnHandOperated = findViewById(R.id.btn_hand_operated);
        btnFind = findViewById(R.id.btn_find);
        tvPrintData = findViewById(R.id.tv_print_data);
        seekBarDataUsage = findViewById(R.id.seekBar_data_usage);

        imbNetflix = findViewById(R.id.imb_netflix);
        imbDisneyPlus = findViewById(R.id.imb_disney_plus);
        imbTving = findViewById(R.id.imb_tving);
        imbWavve = findViewById(R.id.imb_wavve);
        imbYoutube = findViewById(R.id.imb_youtube);

        dbRef = FirebaseDatabase.getInstance().getReference("https://yoring2024-659ed-default-rtdb.firebaseio.com/");

        seekBarDataUsage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateDataUsageText(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailRecommend1.this, DetailRecommend2.class);
                int currentDataUsage = seekBarDataUsage.getProgress();
                intent.putExtra("dataUsage", currentDataUsage);
                intent.putExtra("isDisneyPlusPicked", isDisneyPlusPicked);
                intent.putExtra("isTvingPicked", isTvingPicked);
                intent.putExtra("isNetflixPicked", isNetflixPicked);
                intent.putExtra("isWavvePicked", isWavvePicked);
                Log.d("IntentData", "Sending dataUsage: " + currentDataUsage +
                        ", DisneyPlusPicked: " + isDisneyPlusPicked +
                        ", TvingPicked: " + isTvingPicked +
                        ", NetflixPicked: " + isNetflixPicked +
                        ", WavvePicked: " + isWavvePicked);
                startActivity(intent);
            }
        });


        btnAutomatic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long dataUsageBytes = getDataUsage(DetailRecommend1.this);
                int dataUsageGB = (int) (dataUsageBytes / (1024 * 1024 * 1024));
                seekBarDataUsage.setProgress(dataUsageGB);
                updateDataUsageText(dataUsageGB);
                Toast.makeText(DetailRecommend1.this, "자동 데이터 사용량 설정: " + dataUsageGB + " GB", Toast.LENGTH_SHORT).show();
                btnAutomatic.setTextSize(TEXT_SIZE_SELECTED);
                btnHandOperated.setTextSize(TEXT_SIZE_DEFAULT);
            }
        });

        imbNetflix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleServiceSelection("Netflix");
            }
        });

        imbTving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleServiceSelection("Tving");
            }
        });

        imbWavve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleServiceSelection("Wavve");
            }
        });

        imbDisneyPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleServiceSelection("DisneyPlus");
            }
        });

        imbYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleServiceSelection("Youtube");
            }
        });

        initSpinners();
    }

    private void toggleNetflixImage() {
        if (isNetflixPicked) {
            imbNetflix.setImageResource(R.drawable.netflix_pick);
        } else {
            imbNetflix.setImageResource(R.drawable.netflix_not_pick);
        }
        imbNetflix.invalidate();
    }

    private void toggleTvingImage() {
        if (isTvingPicked) {
            imbTving.setImageResource(R.drawable.tving_pick);
        } else {
            imbTving.setImageResource(R.drawable.tving_not_pick);
        }
        imbTving.invalidate();
    }

    private void toggleWavveImage() {
        if (isWavvePicked) {
            imbWavve.setImageResource(R.drawable.wavve_pick);
        } else {
            imbWavve.setImageResource(R.drawable.wavve_not_pick);
        }
        imbWavve.invalidate();
    }

    private void toggleDisneyPlusImage() {
        if (isDisneyPlusPicked) {
            imbDisneyPlus.setImageResource(R.drawable.disney_pick);
        } else {
            imbDisneyPlus.setImageResource(R.drawable.disney_not_pick);
        }
        imbDisneyPlus.invalidate(); // UI 갱신 강제
    }

    private void toggleYoutubeImage() {
        if (isYoutubePicked) {
            imbYoutube.setImageResource(R.drawable.youtube_pick);
        } else {
            imbYoutube.setImageResource(R.drawable.youtube_not_pick);
        }
        imbYoutube.invalidate(); // UI 갱신 강제
    }



    private void updateDataUsageText(int dataUsage) {
        if (dataUsage == 301) {
            tvPrintData.setText("무제한");
        } else {
            tvPrintData.setText(dataUsage + " GB");
        }
    }

    private long getDataUsage(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(Context.NETWORK_STATS_SERVICE);
            if (networkStatsManager != null) {
                NetworkStats networkStats;
                try {
                    networkStats = networkStatsManager.querySummary(ConnectivityManager.TYPE_MOBILE, null, 0, System.currentTimeMillis());
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
                NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                long totalBytes = 0;
                while (networkStats.hasNextBucket()) {
                    networkStats.getNextBucket(bucket);
                    totalBytes += bucket.getRxBytes() + bucket.getTxBytes();
                }
                networkStats.close();
                return totalBytes;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    private void initSpinners() {
        spinnerFamily1 = findViewById(R.id.spinner_family1);
        spinnerFamily2 = findViewById(R.id.spinner_family2);
        spinnerFamily3 = findViewById(R.id.spinner_family3);
        spinnerFamily4 = findViewById(R.id.spinner_family4);

        List<String> telecomCompanies = new ArrayList<>();
        telecomCompanies.add("선택");
        telecomCompanies.add("SKT");
        telecomCompanies.add("KT");
        telecomCompanies.add("LG");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, telecomCompanies);

        spinnerFamily1.setAdapter(adapter);
        spinnerFamily2.setAdapter(adapter);
        spinnerFamily3.setAdapter(adapter);
        spinnerFamily4.setAdapter(adapter);

        setupSpinnerListener(spinnerFamily1);
        setupSpinnerListener(spinnerFamily2);
        setupSpinnerListener(spinnerFamily3);
        setupSpinnerListener(spinnerFamily4);
    }

    private void setupSpinnerListener(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selected = (String) parent.getItemAtPosition(position);
                    updateTelecomCounts(selected);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void toggleServiceSelection(String service) {
        boolean isSelected = false;

        switch (service) {
            case "Netflix":
                isNetflixPicked = !isNetflixPicked;
                toggleNetflixImage();
                isSelected = isNetflixPicked;
                break;
            case "Tving":
                isTvingPicked = !isTvingPicked;
                toggleTvingImage();
                isSelected = isTvingPicked;
                break;
            case "Wavve":
                isWavvePicked = !isWavvePicked;
                toggleWavveImage();
                isSelected = isWavvePicked;
                break;
            case "DisneyPlus":
                isDisneyPlusPicked = !isDisneyPlusPicked;
                toggleDisneyPlusImage();
                isSelected = isDisneyPlusPicked;
                break;
            case "Youtube":
                isYoutubePicked = !isYoutubePicked;
                toggleYoutubeImage();
                isSelected = isYoutubePicked;
                break;
        }

        // 선택된 상태에 따라 selectedCount 조정
        if (isSelected) {
            selectedCount++;
            if (selectedCount > 2) {
                // 최대 2개 선택만 가능하므로 마지막 선택 해제
                Toast.makeText(this, "최대 2개까지만 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                switch (service) {
                    case "Netflix":
                        isNetflixPicked = !isNetflixPicked;
                        toggleNetflixImage();
                        break;
                    case "Tving":
                        isTvingPicked = !isTvingPicked;
                        toggleTvingImage();
                        break;
                    case "Wavve":
                        isWavvePicked = !isWavvePicked;
                        toggleWavveImage();
                        break;
                    case "DisneyPlus":
                        isDisneyPlusPicked = !isDisneyPlusPicked;
                        toggleDisneyPlusImage();
                        break;
                    case "Youtube":
                        isYoutubePicked = !isYoutubePicked;
                        toggleYoutubeImage();
                        break;
                }
                selectedCount--;
            }
        } else {
            selectedCount--;
        }
    }

    private void updateTelecomCounts(String telecom) {
        switch (telecom) {
            case "SKT":
                sktCount++;
                break;
            case "KT":
                ktCount++;
                break;
            case "LG":
                lgCount++;
                break;
        }
    }

    private void fetchAndFilterPlans() {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<HashMap<String, Object>> filteredPlans = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String, Object> plan = (HashMap<String, Object>) snapshot.getValue();

                    // 요금제 가격 계산 로직
                    float price = Float.parseFloat(plan.get("price").toString());
                    if (sktCount > 0) {
                        price *= 0.9; // SKT 요금제 10% 할인
                    }
                    if (ktCount > 0) {
                        if (ktCount == 1) {
                            price -= 2750; // KT 1명 할인
                        } else if (ktCount >= 2) {
                            price -= 5500; // KT 2명 이상 할인
                        }
                    }
                    if (lgCount >= 3) {
                        price -= 3600; // LG 3명 이상 할인
                    }

                    plan.put("price", price);
                    filteredPlans.add(plan);
                }

                displayFilteredPlans(filteredPlans);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void displayFilteredPlans(List<HashMap<String, Object>> filteredPlans) {
        // 필터링된 요금제를 UI에 표시하는 로직 추가
        StringBuilder displayText = new StringBuilder();
        for (HashMap<String, Object> plan : filteredPlans) {
            displayText.append(plan.get("name")).append(" - ").append(plan.get("price")).append("\n");
        }
        tvPrintData.setText(displayText.toString());
    }
}