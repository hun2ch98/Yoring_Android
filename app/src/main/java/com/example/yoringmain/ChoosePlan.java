package com.example.yoringmain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ChoosePlan extends AppCompatActivity {

    private List<SubscriptionPlan> subscriptionPlan = new ArrayList<>();
    ListView lvFilteringSub;
    Toolbar toolbar;
    ImageButton btnTelecom, btnData, btnSpeed, btnPrice;
    Spinner spnMainTelecom, spnExtraTelecom;
    LinearLayout linearLayout4_1, linearLayout4_2;
    RelativeLayout relativeLayout4_1, relativeLayout4_2, relativeLayout4_3, relativeLayout4_4, relativeLayout4_5;
    SeekBar seekBarDataUsage;
    RadioButton rdbPrice1, rdbPrice2, rdbPrice3, rdbPrice4, rdbLimit1, rdbLimit2, rdbLimit3, rdbLimit4;
    TextView tvDataUsage0, tvDataUsage7, tvDataUsage15, tvDataUsage71, tvDataUsage100, tvDataUsageUnlimited;
    private int dataUsageProgress = 0;
    String[] itemsSpinner1 = {"선택하세요", "SKT", "KT", "LG"};
    String[] itemsSpinner2 = {"선택하세요", "프리티", "스마텔", "SK 세븐모바일", "헬로모바일", "T Plus"};

    private static final String DATA_USAGE_PROGRESS_KEY = "data_usage_progress";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_plan);

        toolbar = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        btnData = findViewById(R.id.btn_data);
        btnPrice = findViewById(R.id.btn_price);
        btnSpeed = findViewById(R.id.btn_speed);
        btnTelecom = findViewById(R.id.btn_telecom);

        spnMainTelecom = findViewById(R.id.spn_main);
        spnExtraTelecom = findViewById(R.id.spn_extra);

        linearLayout4_1 = findViewById(R.id.linear4_1);
        linearLayout4_2 = findViewById(R.id.linear4_2);

        relativeLayout4_1 = findViewById(R.id.relative4_1);
        relativeLayout4_2 = findViewById(R.id.relative4_2);
        relativeLayout4_3 = findViewById(R.id.relative4_3);
        relativeLayout4_4 = findViewById(R.id.relative4_4);
        relativeLayout4_5 = findViewById(R.id.relative4_5);

        rdbPrice1 = findViewById(R.id.rdb_price_1);
        rdbPrice2 = findViewById(R.id.rdb_price_2);
        rdbPrice3 = findViewById(R.id.rdb_price_3);
        rdbPrice4 = findViewById(R.id.rdb_price_4);
        rdbLimit1 = findViewById(R.id.rdb_limit_1);
        rdbLimit2 = findViewById(R.id.rdb_limit_2);
        rdbLimit3 = findViewById(R.id.rdb_limit_3);
        rdbLimit4 = findViewById(R.id.rdb_limit_4);

        lvFilteringSub = findViewById(R.id.lv_filtering_sub);

        seekBarDataUsage = findViewById(R.id.seekBar_data_usage);

        tvDataUsage0 = findViewById(R.id.tv_data_usage_0);
        tvDataUsage7 = findViewById(R.id.tv_data_usage_7);
        tvDataUsage15 = findViewById(R.id.tv_data_usage_15);
        tvDataUsage71 = findViewById(R.id.tv_data_usage_71);
        tvDataUsage100 = findViewById(R.id.tv_data_usage_100);
        tvDataUsageUnlimited = findViewById(R.id.tv_data_usage_unlimited);

        spnMainTelecom = findViewById(R.id.spn_main);
        spnExtraTelecom = findViewById(R.id.spn_extra);

        if (spnMainTelecom != null && spnExtraTelecom != null) {
            ArrayAdapter<String> adapterSpinner1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemsSpinner1);
            adapterSpinner1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnMainTelecom.setAdapter(adapterSpinner1);

            ArrayAdapter<String> adapterSpinner2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemsSpinner2);
            adapterSpinner2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnExtraTelecom.setAdapter(adapterSpinner2);
        } else {
            Log.e("SpinnerError", "Spinner object is null");
        }


        ArrayAdapter<String> adapterSpinner1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemsSpinner1);
        adapterSpinner1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMainTelecom.setAdapter(adapterSpinner1);

        ArrayAdapter<String> adapterSpinner2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemsSpinner2);
        adapterSpinner2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnExtraTelecom.setAdapter(adapterSpinner2);


        btnTelecom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (relativeLayout4_1.getVisibility() == View.VISIBLE || relativeLayout4_2.getVisibility() == View.VISIBLE) {
                    relativeLayout4_1.setVisibility(View.GONE);
                    relativeLayout4_2.setVisibility(View.GONE);
                } else {
                    relativeLayout4_1.setVisibility(View.VISIBLE);
                    relativeLayout4_2.setVisibility(View.VISIBLE);
                    relativeLayout4_3.setVisibility(View.GONE);
                    relativeLayout4_4.setVisibility(View.GONE);
                    relativeLayout4_5.setVisibility(View.GONE);
                }
            }
        });

        btnPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                if (relativeLayout4_3.getVisibility() == View.VISIBLE) {
                    relativeLayout4_3.setVisibility(View.GONE);
                } else {
                    relativeLayout4_3.setVisibility(View.VISIBLE);
                    relativeLayout4_1.setVisibility(View.GONE);
                    relativeLayout4_2.setVisibility(View.GONE);
                    relativeLayout4_4.setVisibility(View.GONE);
                    relativeLayout4_5.setVisibility(View.GONE);
                }
            }
        });

        btnData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (relativeLayout4_4.getVisibility() == View.VISIBLE) {
                    relativeLayout4_4.setVisibility(View.GONE);
                } else {
                    relativeLayout4_4.setVisibility(View.VISIBLE);
                    relativeLayout4_1.setVisibility(View.GONE);
                    relativeLayout4_2.setVisibility(View.GONE);
                    relativeLayout4_3.setVisibility(View.GONE);
                    relativeLayout4_5.setVisibility(View.GONE);
                    updateDataUsageTextViews(0);
                }
            }
        });

        btnSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (relativeLayout4_5.getVisibility() == View.VISIBLE) {
                    relativeLayout4_5.setVisibility(View.GONE);
                } else {
                    relativeLayout4_5.setVisibility(View.VISIBLE);
                    relativeLayout4_1.setVisibility(View.GONE);
                    relativeLayout4_2.setVisibility(View.GONE);
                    relativeLayout4_3.setVisibility(View.GONE);
                    relativeLayout4_4.setVisibility(View.GONE);
                }
            }
        });

        seekBarDataUsage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateDataUsageTextViews(progress);
                dataUsageProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                loadFilteredSubPlans();
            }
        });

        lvFilteringSub.setOnItemClickListener((parent, view, position, id) -> {
            SubscriptionPlan selectedPlan = subscriptionPlan.get(position);
            String userToken = getUserToken();

            if (userToken != null) {
                DatabaseReference userSubscriptionsRef = FirebaseDatabase.getInstance().getReference("UserSubscriptions").child(userToken);
                userSubscriptionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            clearSelectedSubscription(userToken);
                            updateSelectedSubscription(userToken, selectedPlan.getSub_name());
                        } else {
                            updateSelectedSubscription(userToken, selectedPlan.getSub_name());
                        }
                        Intent intent = new Intent(ChoosePlan.this, MainPage.class);
                        intent.putExtra("selected_subscription", selectedPlan.getSub_name());
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Firebase", "사용자의 요금제를 읽어오는 데 실패했습니다: " + databaseError.getMessage());
                    }
                });
            } else {
                Log.e("Firebase", "사용자의 토큰 값을 가져오는 데 실패했습니다.");
            }
        });



        if (savedInstanceState != null) {
            dataUsageProgress = savedInstanceState.getInt(DATA_USAGE_PROGRESS_KEY);
            seekBarDataUsage.setProgress(dataUsageProgress);
            updateDataUsageTextViews(dataUsageProgress);
        }

        //대형 삼사 통신사 스피너
        spnMainTelecom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    spnExtraTelecom.setSelection(0);
                    loadFilteredSubPlans();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //알뜰폰 통신사 스피너
        spnExtraTelecom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    spnMainTelecom.setSelection(0);
                    loadFilteredSubPlans();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        setupRadioButtons();
        loadFilteredSubPlans();

    }

    //파이어베이스 값 불러오기 위한 class
    public static class SubscriptionPlan implements Comparable<SubscriptionPlan> {
        private String sub_name, telecom_name, usage_network, data, message, sale_price, call;
        private Long price;
        private Double speed_limit;

        public String getSub_name() {
            return sub_name;
        }

        public void setSub_name(String sub_name) {
            this.sub_name = sub_name;
        }

        public String getTelecom_name() {
            return telecom_name;
        }

        public void setTelecom_name(String telecom_name) {
            this.telecom_name = telecom_name;
        }

        public String getUsage_network() {
            return usage_network;
        }

        public void setUsage_network(String usage_network) {
            this.usage_network = usage_network;
        }

        public Double getSpeed_limit() {
            return speed_limit;
        }

        public void setSpeed_limit(Double speed_limit) {
            this.speed_limit = speed_limit;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getSale_price() {
            return sale_price;
        }

        public void setSale_price(String sale_price) {
            this.sale_price = sale_price;
        }

        public String getCall() {
            return call;
        }

        public void setCall(String call) {
            this.call = call;
        }

        public Long getPrice() {
            return price;
        }

        public void setPrice(Long price) {
            this.price = price;
        }

        @Override
        public int compareTo(SubscriptionPlan o) {
            return Long.compare(this.price, o.price);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(DATA_USAGE_PROGRESS_KEY, dataUsageProgress);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearPriceRadioButtons() {
        rdbPrice1.setChecked(false);
        rdbPrice2.setChecked(false);
        rdbPrice3.setChecked(false);
        rdbPrice4.setChecked(false);
    }

    private void clearLimitRadioButtons() {
        rdbLimit1.setChecked(false);
        rdbLimit2.setChecked(false);
        rdbLimit3.setChecked(false);
        rdbLimit4.setChecked(false);
    }

    private void setupRadioButtons() {
        rdbPrice1.setOnClickListener(view -> {
            clearPriceRadioButtons();
            rdbPrice1.setChecked(true);
            loadFilteredSubPlans();
        });

        rdbPrice2.setOnClickListener(view -> {
            clearPriceRadioButtons();
            rdbPrice2.setChecked(true);
            loadFilteredSubPlans();
        });

        rdbPrice3.setOnClickListener(view -> {
            clearPriceRadioButtons();
            rdbPrice3.setChecked(true);
            loadFilteredSubPlans();
        });

        rdbPrice4.setOnClickListener(view -> {
            clearPriceRadioButtons();
            rdbPrice4.setChecked(true);
            loadFilteredSubPlans();
        });

        rdbLimit1.setOnClickListener(view -> {
            clearLimitRadioButtons();
            rdbLimit1.setChecked(true);
            loadFilteredSubPlans();
        });

        rdbLimit2.setOnClickListener(view -> {
            clearLimitRadioButtons();
            rdbLimit2.setChecked(true);
            loadFilteredSubPlans();
        });

        rdbLimit3.setOnClickListener(view -> {
            clearLimitRadioButtons();
            rdbLimit3.setChecked(true);
            loadFilteredSubPlans();
        });

        rdbLimit4.setOnClickListener(view -> {
            clearLimitRadioButtons();
            rdbLimit4.setChecked(true);
            loadFilteredSubPlans();
        });
    }

    //Seekbar progress 별 텍스트 전환 함수
    private void updateDataUsageTextViews(int progress) {
        hideAllTextViews();

        switch (progress) {
            case 0:
                setTextViewVisibility(tvDataUsage0);
                break;
            case 1:
                setTextViewVisibility(tvDataUsage7);
                break;
            case 2:
                setTextViewVisibility(tvDataUsage15);
                break;
            case 3:
                setTextViewVisibility(tvDataUsage71);
                break;
            case 4:
                setTextViewVisibility(tvDataUsage100);
                break;
            case 5:
                setTextViewVisibility(tvDataUsageUnlimited);
                break;
        }
    }

    private void hideAllTextViews() {
        hideTextViews(tvDataUsage0, tvDataUsage7, tvDataUsage15, tvDataUsage71, tvDataUsage100, tvDataUsageUnlimited);
    }

    private void setTextViewVisibility(TextView... textViews) {
        for (TextView textView : textViews) {
            textView.setVisibility(View.VISIBLE);
        }
    }

    private void hideTextViews(TextView... textViews) {
        for (TextView textView : textViews) {
            textView.setVisibility(View.GONE);
        }
    }

    private void updateListView() {
        ChoosePlan.CustomAdapter adapter = new CustomAdapter(this, R.layout.list_item, subscriptionPlan);
        lvFilteringSub.setAdapter(adapter);
    }

    private class CustomAdapter extends ArrayAdapter<SubscriptionPlan> {
        private DatabaseReference favRef;
        private int favoriteCount = 0;
        private HashMap<String, Boolean> favoritesMap = new HashMap<>();

        public CustomAdapter(@NonNull Context context, int resource, @NonNull List<SubscriptionPlan> objects) {
            super(context, resource, objects);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                favRef = FirebaseDatabase.getInstance().getReference("UserFavorites").child(user.getUid());
                favRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        favoriteCount = (int) dataSnapshot.getChildrenCount();
                        favoritesMap.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String subName = snapshot.getKey();
                            String decodedSubName = decodeFromFirebaseKey(subName);
                            favoritesMap.put(decodedSubName, true);
                        }
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Firebase", "Error fetching favorite count");
                    }
                });
            }
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            }

            TextView tvPlanName = convertView.findViewById(R.id.tvPlanName);
            TextView tvTelecomName = convertView.findViewById(R.id.tvTelecomName);
            TextView tvPrice = convertView.findViewById(R.id.tvPrice);
            TextView tvData = convertView.findViewById(R.id.tvData);
            TextView tvSpeedLimit = convertView.findViewById(R.id.tvSpeedLimit);
            ImageButton imbEmptyHeart = convertView.findViewById(R.id.imb_empty_heart);

            SubscriptionPlan plan = getItem(position);

            tvPlanName.setText(plan.getSub_name());
            tvTelecomName.setText(plan.getTelecom_name());
            tvPrice.setText(String.valueOf(plan.getPrice()) + "원");
            tvData.setText(plan.getData());
            if (plan.getSpeed_limit() != null) {
                tvSpeedLimit.setText(String.valueOf(plan.getSpeed_limit()) + "Mbps");
            } else {
                tvSpeedLimit.setText("");
            }

            Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.nanumsbold_e);
            tvPlanName.setTypeface(typeface);
            tvTelecomName.setTypeface(typeface);
            tvPrice.setTypeface(typeface);
            tvData.setTypeface(typeface);
            tvSpeedLimit.setTypeface(typeface);

            String encodedSubName = encodeForFirebaseKey(plan.getSub_name());
            if (favoritesMap.containsKey(encodedSubName)) {
                imbEmptyHeart.setImageResource(R.drawable.full_heart);
                imbEmptyHeart.setTag("full");
            } else {
                imbEmptyHeart.setImageResource(R.drawable.empty_heart);
                imbEmptyHeart.setTag("empty");
            }

            imbEmptyHeart.setOnClickListener(v -> {
                boolean isFavorited = "full".equals(imbEmptyHeart.getTag());
                if (!isFavorited) {
                    if (favoriteCount >= 3) {
                        Toast.makeText(getContext(), "찜 목록이 가득 찼습니다. 비우고 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    imbEmptyHeart.setImageResource(R.drawable.full_heart);
                    imbEmptyHeart.setTag("full");
                    favRef.child(encodedSubName).setValue(true);
                    favoriteCount++;
                } else {
                    imbEmptyHeart.setImageResource(R.drawable.empty_heart);
                    imbEmptyHeart.setTag("empty");
                    favRef.child(encodedSubName).removeValue();
                    favoriteCount--;
                }
            });

            imbEmptyHeart.setFocusable(false);
            imbEmptyHeart.setClickable(true);

            return convertView;
        }
    }


    private void loadFilteredSubPlans() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("SubscriptionPlan");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                subscriptionPlan.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SubscriptionPlan plan = snapshot.getValue(SubscriptionPlan.class);
                    if (plan != null) {
                        boolean telecomFilter = !isSpinnerDefault(spnMainTelecom) || !isSpinnerDefault(spnExtraTelecom) ? filterByTelecomName(plan) : true;
                        boolean priceFilter = isAnyPriceRadioButtonChecked() ? filterByPrice(plan.getPrice()) : true;
                        boolean dataFilter = dataUsageProgress > 0 ? filterByDataUsage(plan.getData()) : true;
                        boolean speedLimitFilter = isAnySpeedLimitRadioButtonChecked() ? filterBySpeedLimit(plan.getSpeed_limit()) : true;

                        if (telecomFilter && priceFilter && dataFilter && speedLimitFilter) {
                            subscriptionPlan.add(plan);
                        }
                    }
                }
                updateListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("DatabaseError", "Fetching subscription plans failed", databaseError.toException());
            }
        });
    }

    private boolean filterByPrice(Long priceLong) {
        try {
            if (rdbPrice1.isChecked() && priceLong <= 20000) return true;
            else if (rdbPrice2.isChecked() && priceLong > 20000 && priceLong <= 50000) return true;
            else if (rdbPrice3.isChecked() && priceLong > 50000 && priceLong <= 70000) return true;
            else if (rdbPrice4.isChecked() && priceLong > 70000) return true;
        } catch (NumberFormatException e) {
            Log.e("NumberFormat", "파싱 실패", e);
        } return false;
    }

    //속도 제한 데이터로 필터링하는 함수
    private boolean filterBySpeedLimit(Double limit) {
        if (limit == null) {
            return false;
        }
        try {
            if (rdbLimit1.isChecked() && limit <= 1) return true;
            else if (rdbLimit2.isChecked() && limit <= 3) return true;
            else if (rdbLimit3.isChecked() && limit <= 5) return true;
            else if (rdbLimit4.isChecked() && limit > 5) return true;
        } catch (NumberFormatException e) {
            Log.e("NumberFormat", "파싱 실패", e);
        }
        return false;
    }


    //데이터 값 중 무제한 일 경우 숫자 값으로 변경하는 함수
    private double parseData(String dataStr) {
        if (dataStr.equals("무제한")) {
            return 301.0;
        } else {
            return Double.parseDouble(dataStr.replaceAll("[^0-9.]", ""));
        }
    }

    //데이터 사용량으로 필터링하는 함수
    private boolean filterByDataUsage(String dataStr) {
        double dataAmount = parseData(dataStr);
        switch (dataUsageProgress) {
            case 0:
                return true;
            case 1:
                return dataAmount <= 7.0;
            case 2:
                return dataAmount <= 15.0;
            case 3:
                return dataAmount <= 71.0;
            case 4:
                return dataAmount <= 100.0;
            case 5:
                return dataAmount > 100.0;
            default:
                return false;
        }
    }

    private void updateSelectedSubscription(String token, String subName) {
        DatabaseReference userSubscriptionsRef = FirebaseDatabase.getInstance().getReference("UserSubscriptions").child(token);

        userSubscriptionsRef.setValue(subName)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "요금제가 성공적으로 저장되었습니다.");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "요금제 저장 중 오류 발생: " + e.getMessage());
                });
    }

    private void clearSelectedSubscription(String token) {
        DatabaseReference userSubscriptionsRef = FirebaseDatabase.getInstance().getReference("UserSubscriptions").child(token);

        userSubscriptionsRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "요금제가 성공적으로 삭제되었습니다.");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "요금제 삭제 중 오류 발생: " + e.getMessage());
                });
    }

    private String getUserToken() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return null;
        }
    }

    //통신사 이름으로 필터링하는 함수
    private boolean filterByTelecomName(SubscriptionPlan plan) {
        String selectedMainTelecom = spnMainTelecom.getSelectedItem().toString();
        String selectedExtraTelecom = spnExtraTelecom.getSelectedItem().toString();

        String telecomName = plan.getTelecom_name();
        if (telecomName != null) {
            if (!"선택하세요".equals(selectedMainTelecom) && telecomName.equals(selectedMainTelecom)) {
                return true;
            }
            if (!"선택하세요".equals(selectedExtraTelecom) && telecomName.equals(selectedExtraTelecom)) {
                return true;
            }
        }
        return false;
    }

    // 스피너가 기본값에 설정되어 있는지 확인하는 함수
    private boolean isSpinnerDefault(Spinner spinner) {
        return spinner.getSelectedItemPosition() == 0;
    }

    // 가격 관련 라디오 버튼이 하나라도 선택되었는지 확인하는 함수
    private boolean isAnyPriceRadioButtonChecked() {
        return rdbPrice1.isChecked() || rdbPrice2.isChecked() || rdbPrice3.isChecked() || rdbPrice4.isChecked();
    }

    //속도 제한 데이터 라디오 버튼 클릭됐는지 확인하는 함수
    private boolean isAnySpeedLimitRadioButtonChecked() {
        return rdbLimit1.isChecked() || rdbLimit2.isChecked() || rdbLimit3.isChecked() || rdbLimit4.isChecked();
    }

    private String encodeForFirebaseKey(String key) {
        if (key == null) return "";
        return key.replace('.', ',')
                .replace('#', '-')
                .replace('$', '+')
                .replace('[', '(')
                .replace(']', ')');
    }

    private String decodeFromFirebaseKey(String encodedKey) {
        if (encodedKey == null) return "";
        return encodedKey.replace(',', '.')
                .replace('-', '#')
                .replace('+', '$')
                .replace('(', '[')
                .replace(')', ']');
    }
}