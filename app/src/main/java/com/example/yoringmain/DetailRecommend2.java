package com.example.yoringmain;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DetailRecommend2 extends AppCompatActivity {

    private TextView tvCurrentSubName, tvPriceText, tvDataText;
    private ImageView imvTelecom;
    private DatabaseReference databaseRef;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_recommend2);

        tvCurrentSubName = findViewById(R.id.tv_current_sub_name);
        tvPriceText = findViewById(R.id.tv_price_text);
        tvDataText = findViewById(R.id.tv_data_text);
        imvTelecom = findViewById(R.id.imv_telecom);

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        if (user != null) {
            String userUid = user.getUid();
            DatabaseReference userSubRef = databaseRef.child("UserSubscriptions").child(userUid);

            // 사용자의 구독 정보 가져오기
            userSubRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String subscriptionName = dataSnapshot.getValue(String.class);
                    tvCurrentSubName.setText(subscriptionName);

                    //구독 계획 데이터 가져오기
                    DatabaseReference subPlanRef = databaseRef.child("SubscriptionPlan");
                    subPlanRef.orderByChild("sub_name").equalTo(subscriptionName)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        SubscriptionPlan plan = snapshot.getValue(SubscriptionPlan.class);
                                        if (plan != null) {
                                            int dataValue = parseData(plan.getData());
                                            displayData(dataValue);
                                            tvPriceText.setText(plan.getPrice() + "원");
                                            setTelecomImage(plan.getTelecom_name());

                                            //사용중인 요금제 가격 정도 설정
                                            TextView tvCurrentUsePrice1 = findViewById(R.id.tv_current_use_price1);
                                            TextView tvCurrentUsePrice2 = findViewById(R.id.tv_current_use_price2);
                                            TextView tvCurrentUsePrice3 = findViewById(R.id.tv_current_use_price3);
                                            TextView tvCurrentUsePrice4 = findViewById(R.id.tv_current_use_price4);
                                            tvCurrentUsePrice1.setText(plan.getPrice() + "원        ->");
                                            tvCurrentUsePrice2.setText(plan.getPrice() + "원        ->");
                                            tvCurrentUsePrice3.setText((plan.getPrice() + 9900) + "원        ->");
                                            tvCurrentUsePrice4.setText((plan.getPrice() + 9900) + "원        ->");
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.w("DBError", "loadPost:onCancelled", databaseError.toException());
                                }
                            });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("DBError", "loadPost:onCancelled", databaseError.toException());
                }
            });

        }

        //데이터 사용량과 멤버십 선택 여부 가져옴
        int dataUsage = getIntent().getIntExtra("dataUsage", 0);
        boolean isDisneyPlusPicked = getIntent().getBooleanExtra("isDisneyPlusPicked", false);
        boolean isTvingPicked = getIntent().getBooleanExtra("isTvingPicked", false);
        boolean isNetflixPicked = getIntent().getBooleanExtra("isNetflixPicked", false);
        boolean isWavvePicked = getIntent().getBooleanExtra("isWavvePicked", false);
        Log.d("ReceivedIntentData", "Received dataUsage: " + dataUsage +
                ", DisneyPlusPicked: " + isDisneyPlusPicked +
                ", TvingPicked: " + isDisneyPlusPicked +
                ", NetfilxPicked: " + isDisneyPlusPicked +
                ", WavvePicked: " + isDisneyPlusPicked);

        findEligiblePlans(dataUsage);

        // 각각의 멤버십이 단독으로 선택되었을 경우를 확인
        boolean isOnlyDisneyPlusPicked = isDisneyPlusPicked && !isTvingPicked && !isNetflixPicked && !isWavvePicked;
        boolean isOnlyTvingPicked = isTvingPicked && !isDisneyPlusPicked && !isNetflixPicked && !isWavvePicked;
        boolean isOnlyNetflixPicked = isNetflixPicked && !isDisneyPlusPicked && !isTvingPicked && !isWavvePicked;
        boolean isOnlyWavvePicked = isWavvePicked && !isDisneyPlusPicked && !isTvingPicked && !isNetflixPicked;

        // 디즈니 플러스 단독 선택 시
        if (isOnlyDisneyPlusPicked) {
            findPlansForDisneyPlus(dataUsage);
        }

        // 티빙 단독 선택 시
        if (isOnlyTvingPicked) {
            findPlansForTving(dataUsage);
        }

        // 넷플릭스 단독 선택 시
        if (isOnlyNetflixPicked) {
            findPlansForNetflix(dataUsage);
        }

        // 웨이브 단독 선택 시
        if (isOnlyWavvePicked) {
            findPlansForWavve(dataUsage);
        }
    }

    public static class SubscriptionPlan implements Comparable<SubscriptionPlan> {
        private String sub_name, telecom_name, usage_network, data, message, sale_price, call, real_data;
        private Long price;
        private Double speed_limit;

        public String getSub_name() {
            return sub_name;
        }

        public void setSub_name(String sub_name) {
            this.sub_name = sub_name;
        }

        public String getReal_data() {
            return real_data;
        }

        public void setReal_data(String real_data) {
            this.real_data = real_data;
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

    private void setTelecomImage(String telecomName, ImageView imageView) {
        if (telecomName != null) {
            int resId = getTelecomResourceId(telecomName);
            imageView.setImageResource(resId);
        }
    }

    private int getTelecomResourceId(String telecomName) {
        switch (telecomName) {
            case "프리티":
                return R.drawable.freet_logo;
            case "SKT":
                return R.drawable.skt_logo;
            case "LG":
                return R.drawable.lg_u_plus_logo;
            case "KT":
                return R.drawable.kt_logo;
            case "스마텔":
                return R.drawable.smt_logo;
            case "T Plus":
                return R.drawable.tplus_logo;
            case "SK 세븐모바일":
                return R.drawable.sk_7mobile_logo;
            case "헬로모바일":
                return R.drawable.hello_mobile_logo;
            default:
                return android.R.color.transparent;
        }
    }

    private void setTelecomImage(String telecomName) {
        ImageView imageView = findViewById(R.id.imv_telecom);
        setTelecomImage(telecomName, imageView);
    }

    //사용자의 사용 데이터를 기반으로 적합한 구독 계획을 찾는 메소드
    private void findEligiblePlans(int userUsageData) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("SubscriptionPlan");
        Query query = ref.orderByChild("data").startAt(userUsageData);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<SubscriptionPlan> eligiblePlans = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SubscriptionPlan plan = snapshot.getValue(SubscriptionPlan.class);
                    if (plan != null && parseData(plan.getData()) >= userUsageData) {
                        eligiblePlans.add(plan);
                    }
                }

                Collections.sort(eligiblePlans, Comparator.comparingLong(SubscriptionPlan::getPrice));
                if (eligiblePlans.size() > 0) {
                    displayPlanDetails(eligiblePlans.get(0), 1);
                    if (eligiblePlans.size() > 1) {
                        displayPlanDetails(eligiblePlans.get(1), 2);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("DBError", "findEligiblePlans:onCancelled", databaseError.toException());
            }
        });
    }

    //사용자가 이용중인 요금제 표시
    private void displayPlanDetails(SubscriptionPlan plan, int planNumber) {
        TextView tvName = findViewById(getResources().getIdentifier("tv_cheap" + planNumber, "id", getPackageName()));
        TextView tvPrice = findViewById(getResources().getIdentifier("tv_cheap_price" + planNumber, "id", getPackageName()));
        TextView tvData = findViewById(getResources().getIdentifier("tv_cheap_data" + planNumber, "id", getPackageName()));
        ImageView imvTelecom = findViewById(getResources().getIdentifier("imv_cheap" + planNumber, "id", getPackageName()));

        tvName.setText(plan.getSub_name());
        tvPrice.setText(plan.getPrice() + "원");
        if (parseData(plan.getData()) == 301) {
            tvData.setText("무제한");
        } else {
            tvData.setText(plan.getData());
        }
        setTelecomImage(plan.getTelecom_name(), imvTelecom);
    }

    //문자열 -> 정수로 파싱하고 반환
    private int parseData(String data) {
        if (data == null) {
            return 0;
        }
        if (data.equals("무제한")) {
            return 301;
        }
        try {
            String numericData = data.replaceAll("[^0-9.]", "");
            return (int) Double.parseDouble(numericData);
        } catch (NumberFormatException e) {
            Log.e("parseData", "Invalid number format: " + data, e);
            return 0;
        }
    }

    private void displayData(int data) {
        TextView tvData = findViewById(R.id.tv_data_text);
        if (data == 301) {
            tvData.setText("무제한");
        } else {
            tvData.setText(data + " GB");
        }
    }

    /// 디즈니 플러스
    private void findPlansForDisneyPlus(int dateUsage) {
        Log.d("DetailRecommend2", "findPlansForDisneyPlus called with dataUsage: " + dateUsage);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("SubscriptionPlan");
        // LG 통신사 관련 부분 제거
        Query query = ref.orderByChild("telecom_name").equalTo("KT");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("DetailRecommend2", "DataSnapshot received, count: " + dataSnapshot.getChildrenCount());

                ArrayList<SubscriptionPlan> eligiblePlans = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SubscriptionPlan plan = snapshot.getValue(SubscriptionPlan.class);
                    if (plan != null && parseData(plan.getData()) >= dateUsage) {
                        eligiblePlans.add(plan);
                        Log.d("DetailRecommend2", "Plan added: " + plan.getSub_name() + ", Price: " + plan.getPrice());
                    }
                }

                for (SubscriptionPlan plan : eligiblePlans) {
                    long totalCost = plan.getPrice();
                    String telecom = plan.getTelecom_name();

                    if ("KT".equals(telecom)) {
                        if (plan.getSub_name().equals("5G 초이스 프리미엄") ||
                                plan.getSub_name().equals("5G 초이스 스페셜") ||
                                plan.getSub_name().equals("5G 초이스 베이직")) {
                            totalCost += 8900;
                        }
                    }

                    displayRecommendedPlanDetails(plan, 1, String.valueOf(totalCost));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DetailRecommend2", "Database error: " + databaseError.getMessage());
            }
        });
    }

    //티빙
    private void findPlansForTving(int dataUsage) { // dataUsage로 수정
        Log.d("DetailRecommend2", "findPlansForTving called with dataUsage: " + dataUsage);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("SubscriptionPlan");
        Query query = ref.orderByChild("telecom_name").equalTo("KT");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("DetailRecommend2", "DataSnapshot received, count: " + dataSnapshot.getChildrenCount());

                ArrayList<SubscriptionPlan> eligiblePlans = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SubscriptionPlan plan = snapshot.getValue(SubscriptionPlan.class);
                    if (plan != null) {
                        // 데이터가 null이 아닌 경우에만 로그 출력
                        long parsedData = parseData(plan.getData());
                        Log.d("DetailRecommend2", "Plan: " + (plan.getSub_name() != null ? plan.getSub_name() : "null") +
                                ", Data: " + (plan.getData() != null ? plan.getData() : "null") +
                                ", Parsed Data: " + parsedData + ", Data Usage: " + dataUsage);

                        // 유효한 데이터 사용량 비교
                        if (parsedData >= dataUsage) {
                            eligiblePlans.add(plan);
                            Log.d("DetailRecommend2", "Plan added: " + plan.getSub_name() + ", Price: " + plan.getPrice());
                        }
                    }
                }

                ArrayList<SubscriptionPlan> recommendedPlans = new ArrayList<>();
                Collections.sort(eligiblePlans, Comparator.comparingLong(SubscriptionPlan::getPrice));

                // 가장 저렴한 두 개의 요금제를 선택
                if (eligiblePlans.size() > 0) {
                    recommendedPlans.add(eligiblePlans.get(0));
                }
                if (eligiblePlans.size() > 1) {
                    recommendedPlans.add(eligiblePlans.get(1));
                }

                for (SubscriptionPlan plan : recommendedPlans) {
                    long totalCost = plan.getPrice();

                    if (plan.getSub_name().equals("5G 초이스 프리미엄") ||
                            plan.getSub_name().equals("5G 초이스 스페셜") ||
                            plan.getSub_name().equals("5G 초이스 베이직")) {
                        totalCost += 5000; // 티빙 요금 5,000원 추가
                    }

                    Log.d("DetailRecommend2", "Total cost for " + plan.getSub_name() + " with Tving: " + totalCost);
                    displayRecommendedPlanDetails(plan, 1, "Tving");
                }

                // 추천할 요금제가 없는 경우
                if (recommendedPlans.isEmpty()) {
                    Toast.makeText(DetailRecommend2.this, "티빙과 함께 이용할 수 있는 요금제가 없습니다.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DetailRecommend2", "Database error: " + databaseError.getMessage());
            }
        });
    }

    // 웨이브
    private void findPlansForWavve(int dataUsage) {
        Log.d("DetailRecommend2", "findPlansForWavve called with dataUsage: " + dataUsage);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("SubscriptionPlan");
        Query query = ref.orderByChild("telecom_name").equalTo("SKT");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("DetailRecommend2", "DataSnapshot received, count: " + dataSnapshot.getChildrenCount());

                ArrayList<SubscriptionPlan> eligiblePlans = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SubscriptionPlan plan = snapshot.getValue(SubscriptionPlan.class);
                    if (plan != null && parseData(plan.getData()) >= dataUsage) {
                        eligiblePlans.add(plan);
                        Log.d("DetailRecommend2", "Plan added: " + plan.getSub_name() + ", Price: " + plan.getPrice());
                    }
                }

                ArrayList<SubscriptionPlan> recommendedPlans = new ArrayList<>();
                Collections.sort(eligiblePlans, Comparator.comparingLong(SubscriptionPlan::getPrice));

                // 가장 저렴한 두 개의 요금제를 선택
                if (eligiblePlans.size() > 0) {
                    recommendedPlans.add(eligiblePlans.get(0));
                }
                if (eligiblePlans.size() > 1) {
                    recommendedPlans.add(eligiblePlans.get(1));
                }

                for (SubscriptionPlan plan : recommendedPlans) {
                    long originalPrice = plan.getPrice();
                    long discountedPrice = applyWavveDiscount(originalPrice, plan.getSub_name());
                    Log.d("DetailRecommend2", "Total cost for " + plan.getSub_name() + " with Wavve: " + discountedPrice);
                    displayRecommendedPlanDetails(plan, 1, "Wavve");
                }

                // 추천할 요금제가 없는 경우
                if (recommendedPlans.size() == 0) {
                    Toast.makeText(DetailRecommend2.this, "웨이브와 함께 이용할 수 있는 요금제가 없습니다.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("DBError", "findEligiblePlans:onCancelled", databaseError.toException());
            }
        });
    }

    private long applyWavveDiscount(long originalPrice, String planName) {
        List<String> hundredWonPlans = Arrays.asList(
                "5GX 플래티넘", "5GX 프라임플러스", "5GX 프라임",
                "T플랜 스페셜",
                "T플랜 시니어 스페셜", "다이렉트5G 69", "다이렉트5G 62"
        );

        List<String> twoThousandWonPlans = Arrays.asList(
                "5GX 레귤러플러스", "5GX 레귤러", "베이직플러스 75GB업", "베이직플러스 50GB업",
                "베이직플러스 30GB업", "베이직플러스 13GB업", "베이직플러스", "슬림",
                "베이직", "컴팩트플러스", "컴팩트", "0 청년 79", "0 청년 69",
                "0 청년 59 100GB업", "0 청년 59 60GB업", "0 청년 59 36GB업",
                "0 청년 59 15GB업", "0 청년 59", "0 청년 49", "0 청년 43",
                "0 청년 37", "다이렉트5G 55", "다이렉트5G 48", "다이렉트5G 42",
                "다이렉트5G 38", "다이렉트5G 34", "다이렉트5G 31", "다이렉트5G 27",
                "0 청년 다이렉트 55", "0 청년 다이렉트 48", "0 청년 다이렉트 42",
                "0 청년 다이렉트 34", "0 청년 다이렉트 30"
        );

        if (hundredWonPlans.contains(planName)) {
            return originalPrice + 100; // 100원으로 이용가능
        }

        if (twoThousandWonPlans.contains(planName)) {
            return originalPrice + 11900; // 2000원 할인된 가격(11900)으로 이용가능
        }

        return originalPrice;
    }

    //넷플릭스
    private void findPlansForNetflix(int dateUsage) {
        Log.d("DetailRecommend2", "findPlansForTving called with dataUsage: " + dateUsage);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("SubscriptionPlan");
        Query query = ref.orderByChild("telecom_name").equalTo("KT");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("DetailRecommend2", "DataSnapshot received, count: " + dataSnapshot.getChildrenCount());
                ArrayList<SubscriptionPlan> eligiblePlans = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SubscriptionPlan plan = snapshot.getValue(SubscriptionPlan.class);
                    if (plan != null && parseData(plan.getData()) >= dateUsage) {
                        eligiblePlans.add(plan);
                        Log.d("DetailRecommend2", "Plan added: " + plan.getSub_name() + ", Price: " + plan.getPrice());
                    }
                }

                ArrayList<SubscriptionPlan> recommendedPlans = new ArrayList<>();
                Collections.sort(eligiblePlans, Comparator.comparingLong(SubscriptionPlan::getPrice));

                // 가장 저렴한 두 개의 요금제를 선택
                if (eligiblePlans.size() > 0) {
                    recommendedPlans.add(eligiblePlans.get(0));
                }
                if (eligiblePlans.size() > 1) {
                    recommendedPlans.add(eligiblePlans.get(1));
                }

                for (SubscriptionPlan plan : recommendedPlans) {
                    long totalCost = plan.getPrice();

                    if (plan.getSub_name().equals("5G 초이스 프리미엄") ||
                            plan.getSub_name().equals("5G 초이스 스페셜") ||
                            plan.getSub_name().equals("5G 초이스 베이직")) {
                        totalCost += 5000;
                    }

                    Log.d("DetailRecommend2", "Total cost for " + plan.getSub_name() + " with Netflix: " + totalCost);
                    displayRecommendedPlanDetails(plan, 1, "Netflix"); // SubscriptionPlan 객체 전달
                }

                // 추천할 요금제가 없는 경우
                if (recommendedPlans.isEmpty()) {
                    Toast.makeText(DetailRecommend2.this, "넷플릭스와 함께 이용할 수 있는 요금제가 없습니다.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DetailRecommend2", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void displayRecommendedPlanDetails(SubscriptionPlan plan, int planNumber, String subscriptionType) {
        TextView tvName = findViewById(getResources().getIdentifier("tv_recommended" + planNumber, "id", getPackageName()));
        TextView tvPrice = findViewById(getResources().getIdentifier("tv_recommended_price" + planNumber, "id", getPackageName()));
        TextView tvData = findViewById(getResources().getIdentifier("tv_recommended_data" + planNumber, "id", getPackageName()));
        ImageView imvTelecom = findViewById(getResources().getIdentifier("imv_recommended" + planNumber, "id", getPackageName()));

        tvName.setText(plan.getSub_name());

        long priceWithDiscount = plan.getPrice();
        switch (subscriptionType) {
            case "Disney+":
                long totalCost = 0;
                priceWithDiscount = totalCost;
                break;
            case "Tving", "Netflix":
                priceWithDiscount = plan.getPrice();
                break;
            case "Wavve":
                priceWithDiscount = applyWavveDiscount(plan.getPrice(), plan.getSub_name());
                break;
        }

        tvPrice.setText(priceWithDiscount + "원");

        // 데이터 용량 설정
        if (parseData(plan.getData()) == 301) {
            tvData.setText("무제한");
        } else {
            tvData.setText(plan.getData());
        }

        // 통신사 이미지 설정
        setTelecomImage(plan.getTelecom_name(), imvTelecom);
    }
}