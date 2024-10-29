package com.example.yoringmain;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AllPlan extends AppCompatActivity {
    private ListView lvAllSub;
    private DatabaseReference databaseReference;
    private List<SubscriptionPlan> subscriptionPlans = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_plan);

        Toolbar toolbar = findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(" ");

        RadioButton rbPrice = findViewById(R.id.rb_price);
        rbPrice.setChecked(true);

        lvAllSub = findViewById(R.id.lv_all_sub);
        databaseReference = FirebaseDatabase.getInstance().getReference("SubscriptionPlan");

        Button btnFind = findViewById(R.id.btn_find);
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchSubscriptionPlans();
            }
        });

    }

    private void fetchSubscriptionPlans() {
        Query query = databaseReference.orderByChild("price");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                subscriptionPlans.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SubscriptionPlan plan = snapshot.getValue(SubscriptionPlan.class);
                    if (plan != null) {
                        subscriptionPlans.add(plan);
                    }
                }
                Collections.sort(subscriptionPlans);
                updateListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching data: " + databaseError.getMessage());
            }
        });
    }

    private void updateListView() {
        AllPlan.CustomAdapter adapter = new AllPlan.CustomAdapter(this, R.layout.list_item, subscriptionPlans);
        lvAllSub.setAdapter(adapter);
    }

    private class CustomAdapter extends ArrayAdapter<AllPlan.SubscriptionPlan> {
        private DatabaseReference favRef;
        private int favoriteCount = 0;
        private HashMap<String, Boolean> favoritesMap = new HashMap<>();

        public CustomAdapter(@NonNull Context context, int resource, @NonNull List<AllPlan.SubscriptionPlan> objects) {
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

    private String encodeForFirebaseKey(String key) {
        key = key.replace('.', ',');
        key = key.replace('#', '-');
        key = key.replace('$', '+');
        key = key.replace('[', '(');
        key = key.replace(']', ')');
        return key;
    }
}
