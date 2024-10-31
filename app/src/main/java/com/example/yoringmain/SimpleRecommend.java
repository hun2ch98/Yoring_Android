package com.example.yoringmain;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

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

public class SimpleRecommend extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView tvWriteData, tvChangeData;
    private ImageButton imbFinder;
    private SeekBar seekBarDataUsage;
    private EditText etMaxPrice;
    private ListView lvPlans;
    private Button btnHandOperated, btnAutomatic;
    private List<SubscriptionPlan> subscriptionPlan = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ArrayList<String> planList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private static final float TEXT_SIZE_SELECTED = 12f;
    private static final float TEXT_SIZE_DEFAULT = 10f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_recommend);



        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(" ");

        tvWriteData = findViewById(R.id.tv_write_data);
        imbFinder = findViewById(R.id.imb_finder);
        seekBarDataUsage = findViewById(R.id.seekBar_data_usage);
        tvChangeData = findViewById(R.id.tv_change_data);
        etMaxPrice = findViewById(R.id.et_max_price);
        lvPlans = findViewById(R.id.lv_plans);
        btnHandOperated = findViewById(R.id.btn_hand_operated);
        btnAutomatic = findViewById(R.id.btn_automatic);

        updateDataUsageText(seekBarDataUsage.getProgress());

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

        databaseReference = FirebaseDatabase.getInstance().getReference().child("SubscriptionPlan");

        btnAutomatic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long dataUsageBytes = getDataUsage(SimpleRecommend.this);
                int dataUsageGB = (int) (dataUsageBytes / (1024 * 1024 * 1024));
                seekBarDataUsage.setProgress(dataUsageGB);
                updateDataUsageText(dataUsageGB);
                Toast.makeText(SimpleRecommend.this, "자동 데이터 사용량 설정: " + dataUsageGB + " GB", Toast.LENGTH_SHORT).show();
                btnAutomatic.setTextSize(TEXT_SIZE_SELECTED);
                btnHandOperated.setTextSize(TEXT_SIZE_DEFAULT);
            }
        });

        btnHandOperated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SimpleRecommend.this, "수동 모드", Toast.LENGTH_SHORT).show();
                btnHandOperated.setTextSize(TEXT_SIZE_SELECTED);
                btnAutomatic.setTextSize(TEXT_SIZE_DEFAULT);
            }
        });

        imbFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchAndFilterPlans();
                Toast.makeText(SimpleRecommend.this, "버튼 클릭됨", Toast.LENGTH_SHORT).show();
            }
        });
        setupListView();

    }

    private void updateDataUsageText(int dataUsage) {
        if (dataUsage == 301) {
            tvChangeData.setText("무제한");
        } else {
            tvChangeData.setText(dataUsage + " GB");
        }
    }

    private void fetchAndFilterPlans() {
        String maxPriceStr = etMaxPrice.getText().toString().replaceAll("[^\\d.]", "");
        double maxPrice = maxPriceStr.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxPriceStr);
        int minData = seekBarDataUsage.getProgress();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                planList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Long price = snapshot.child("price").getValue(Long.class);
                    // null 체크 및 double 변환
                    double priceDouble = (price == null) ? 0.0 : price.doubleValue();
                    String dataStr = snapshot.child("data").getValue(String.class);
                    SubscriptionPlan plan = snapshot.getValue(SubscriptionPlan.class);
                    double data;
                    if ("무제한".equals(dataStr)) {
                        data = 301;
                    } else {
                        dataStr = dataStr.replaceAll("[^\\d.]", "");
                        data = dataStr.isEmpty() ? 0.0 : Double.parseDouble(dataStr);
                    }

                    if (price <= maxPrice && (data >= minData || minData == 301)) {
                        subscriptionPlan.add(plan);
                    }
                }
                updateListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SimpleRecommend.this, "데이터 로드 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

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


    private class CustomAdapter extends ArrayAdapter<SimpleRecommend.SubscriptionPlan> {
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

        private String decodeFromFirebaseKey(String encodedKey) {
            return encodedKey.replace(',', '.').replace('-', '#').replace('+', '$').replace('(', '[').replace(')', ']');
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            }

            SubscriptionPlan plan = getItem(position);
            TextView tvPlanName = convertView.findViewById(R.id.tvPlanName);
            TextView tvTelecomName = convertView.findViewById(R.id.tvTelecomName);
            TextView tvPrice = convertView.findViewById(R.id.tvPrice);
            TextView tvData = convertView.findViewById(R.id.tvData);
            TextView tvSpeedLimit = convertView.findViewById(R.id.tvSpeedLimit);
            ImageButton imbEmptyHeart = convertView.findViewById(R.id.imb_empty_heart);

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
            tvSpeedLimit.setTypeface(typeface);
            tvData.setTypeface(typeface);

            String encodedSubName = encodeForFirebaseKey(plan.getSub_name());
            if (favoritesMap.containsKey(encodedSubName)) {
                imbEmptyHeart.setImageResource(R.drawable.full_heart);
                imbEmptyHeart.setTag("full");
            } else {
                imbEmptyHeart.setImageResource(R.drawable.empty_heart);
                imbEmptyHeart.setTag("empty");
            }

            imbEmptyHeart.setOnClickListener(v -> {
                boolean isFavorited = imbEmptyHeart.getTag() != null && imbEmptyHeart.getTag().equals("full");

                if (!isFavorited) {
                    if (favoriteCount >= 3) {
                        Toast.makeText(getContext(), "찜 목록이 가득 찼습니다. 비우고 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    imbEmptyHeart.setImageResource(R.drawable.full_heart);
                    imbEmptyHeart.setTag("full");
                    Toast.makeText(getContext(), plan.getSub_name() + " 찜 목록에 추가!", Toast.LENGTH_SHORT).show();
                    favRef.child(encodedSubName).setValue(true);
                    favoriteCount++;
                } else {
                    imbEmptyHeart.setImageResource(R.drawable.empty_heart);
                    imbEmptyHeart.setTag("empty");
                    Toast.makeText(getContext(), plan.getSub_name() + " 찜 목록에서 제거", Toast.LENGTH_SHORT).show();
                    favRef.child(encodedSubName).removeValue();
                    favoriteCount--;
                }
            });

            return convertView;
        }
    }

    private void updateListView() {
        SimpleRecommend.CustomAdapter adapter = new SimpleRecommend.CustomAdapter(this, R.layout.list_item, subscriptionPlan);
        lvPlans.setAdapter(adapter);
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

    private void setupListView() {
        CustomAdapter adapter = new CustomAdapter(this, R.layout.list_item, subscriptionPlan);
        lvPlans.setAdapter(adapter);
        lvPlans.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SubscriptionPlan plan = subscriptionPlan.get(position);
                Toast.makeText(SimpleRecommend.this, "Item Clicked: " + plan.getSub_name(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String encodeForFirebaseKey(String key) {
        key = key.replace('.', ',');
        key = key.replace('#', '-');
        key = key.replace('$', '+');
        key = key.replace('[', '(');
        key = key.replace(']', ')');
        return key;
    }

}