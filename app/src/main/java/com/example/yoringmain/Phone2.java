package com.example.yoringmain;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.os.Bundle;


import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Phone2 extends AppCompatActivity {
    private TextView  tv6_1, modelNameTextView, tv6_5, tv6_6;
    private ImageView modelImage;
    private Spinner colorSpinner, storageSpinner, monthSpinner;
    private Button backButton;
    private DatabaseReference databaseReference;
    private ArrayAdapter<String> colorAdapter, storageAdapter, monthAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone2);

        colorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        storageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);

        colorAdapter.add("선택하세요");
        storageAdapter.add("선택하세요");
        monthAdapter.add("선택하세요");
        monthAdapter.addAll("24", "30", "36");

        tv6_1 = findViewById(R.id.tv6_1);
        tv6_5 = findViewById(R.id.tv6_5);
        tv6_6 = findViewById(R.id.tv6_6);
        modelNameTextView = findViewById(R.id.modelNameTextView);
        colorSpinner = findViewById(R.id.colorSpinner);
        storageSpinner = findViewById(R.id.storageSpinner);
        monthSpinner = findViewById(R.id.monthSpinner);
        backButton = findViewById(R.id.backButton);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        String modelName = getIntent().getStringExtra("modelName");
        modelNameTextView.setText(modelName);

        String currentModelName = modelNameTextView.getText().toString(); // modelName을 가져옴
        updateManufacturerText(currentModelName);

        updateModelImage(modelName);
        setupDatabaseListeners();
        setupSpinners();
        setupAdapters();
        setupBackButton();

    }

    private void setupDatabaseListeners() {
        databaseReference.child("SmartPhone").orderByChild("modelName").equalTo(modelNameTextView.getText().toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Object colorData = snapshot.child("color").getValue();
                                List<String> colors;
                                if (colorData instanceof List) {
                                    colors = (List<String>) colorData;
                                }
                                else if (colorData instanceof String) {
                                    colors = new ArrayList<>();
                                    colors.add((String) colorData);
                                }
                                else {
                                    colors = new ArrayList<>();
                                }
                                String storage = snapshot.child("storage").getValue(String.class);
                                if (colors != null) {
                                    for (String color : colors) {
                                        addUniqueItemToAdapter(colorAdapter, color);
                                    }
                                }
                                if (storage != null) {
                                    addUniqueItemToAdapter(storageAdapter, storage);
                                }
                            }
                            setSpinnerAdapter(colorSpinner, colorAdapter);
                            setSpinnerAdapter(storageSpinner, storageAdapter);
                            setSpinnerAdapter(monthSpinner, monthAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        tv6_5.setText("데이터 로드 실패");
                    }
                });
    }

    private void setupSpinners() {
        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isValidSelection()) {
                    updateModelPrice();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        storageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isValidSelection()) {
                    updateModelPrice();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        monthSpinner.setAdapter(monthAdapter);
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    updateMonthlyPrice();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private boolean isValidSelection() {
        return !colorSpinner.getSelectedItem().toString().equals("선택하세요") &&
                !storageSpinner.getSelectedItem().toString().equals("선택하세요");
    }

    private boolean isValidFullSelection() {
        return isValidSelection() && !monthSpinner.getSelectedItem().toString().equals("선택하세요");
    }

    private void updateModelPrice() {
        String selectedColor = colorSpinner.getSelectedItem().toString();
        String selectedStorage = storageSpinner.getSelectedItem().toString();
        String selectedModel = modelNameTextView.getText().toString();

        if (!isValidSelection()) {
            tv6_5.setText("모든 옵션을 선택해주세요.");
            return;
        }

        databaseReference.child("SmartPhone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String modelName = snapshot.child("modelName").getValue(String.class);
                    String color = snapshot.child("color").getValue(String.class);
                    String storage = snapshot.child("storage").getValue(String.class);

                    if (modelName != null && modelName.equals(selectedModel) &&
                            color != null && color.equals(selectedColor) &&
                            storage != null && storage.equals(selectedStorage)) {

                        String modelPriceStr = snapshot.child("modelPrice").getValue(String.class);
                        if (modelPriceStr != null) {
                            try {
                                int modelPrice = Integer.parseInt(modelPriceStr);

                                NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.KOREA);
                                String formattedPrice = numberFormat.format(modelPrice);

                                tv6_5.setText(formattedPrice + "원");
                            } catch (NumberFormatException e) {
                                tv6_5.setText("변환오류");
                            }
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tv6_5.setText("데이터 로드 실패");
            }
        });
    }

    private void updateMonthlyPrice() {
        if (!isValidFullSelection()) {
            tv6_6.setText("모든 옵션을 선택해주세요.");
            return;
        }

        String selectedColor = colorSpinner.getSelectedItem().toString();
        String selectedStorage = storageSpinner.getSelectedItem().toString();
        int selectedMonths = Integer.parseInt(monthSpinner.getSelectedItem().toString());

        databaseReference.child("SmartPhone").orderByChild("color").equalTo(selectedColor)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String storage = snapshot.child("storage").getValue(String.class);
                            if (storage != null && storage.equals(selectedStorage)) {
                                String modelPriceStr = snapshot.child("modelPrice").getValue(String.class);
                                if (modelPriceStr != null) {
                                    try {
                                        double modelPrice = Double.parseDouble(modelPriceStr.replaceAll("[^\\d.]", ""));
                                        int monthlyPrice = (int) Math.round(modelPrice / selectedMonths);

                                        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.KOREA);
                                        String formattedPrice = numberFormat.format(monthlyPrice);

                                        tv6_6.setText(formattedPrice + "원");
                                    } catch (NumberFormatException e) {
                                        tv6_6.setText("변환 오류");
                                    }
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        tv6_6.setText("데이터 로드 실패");
                    }
                });
    }

    private void addUniqueItemToAdapter(ArrayAdapter<String> adapter, String item) {
        if (item != null && adapter.getPosition(item) == -1) {
            adapter.add(item);
        }
    }

    private void setSpinnerAdapter(Spinner spinner, ArrayAdapter<String> adapter) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setupAdapters() {
        monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        monthAdapter.addAll("선택하세요", "24", "30", "36");
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public void updateModelImage(String modelName) {
        int imageResId;

        switch (modelName) {
            case "아이폰 15":
            case "아이폰 16":
                imageResId = getResources().getIdentifier("black15", "drawable", getPackageName());
                break;
            case "아이폰 15 Plus":
                imageResId = getResources().getIdentifier("plus15black", "drawable", getPackageName());
                break;
            case "아이폰 16 Plus":
                imageResId = getResources().getIdentifier("plus15marine", "drawable", getPackageName());
                break;
            case "아이폰 15 Pro":
            case "아이폰 16 Pro":
                imageResId = getResources().getIdentifier("problue", "drawable", getPackageName());
                break;
            case "아이폰 15 Pro Max":
            case "아이폰 16 Pro Max":
                imageResId = getResources().getIdentifier("promaxwhite", "drawable", getPackageName());
                break;
            case "아이폰 SE(3세대)":
                imageResId = getResources().getIdentifier("semidnight", "drawable", getPackageName());
                break;
            case "갤럭시 Z 플립6":
            case "갤럭시 Z 플립5":
                imageResId = getResources().getIdentifier("zfold6navy", "drawable", getPackageName());
                break;
            case "갤럭시 Z 폴드6":
            case "갤럭시 Z 폴드5":
                imageResId = getResources().getIdentifier("zflip6silvershadow", "drawable", getPackageName());
                break;
            case "갤럭시 S24 Ultra":
            case "갤럭시 S24":
                imageResId = getResources().getIdentifier("galaxy_s24", "drawable", getPackageName());
                break;
            case "갤럭시 S24+":
                imageResId = getResources().getIdentifier("galaxy_s24_plus", "drawable", getPackageName());
                break;
            case "갤럭시 S23 Ultra":
                imageResId = getResources().getIdentifier("galaxy_s23_ultra", "drawable", getPackageName());
                break;
            case "갤럭시 S23+":
                imageResId = getResources().getIdentifier("galaxy_s23_plus", "drawable", getPackageName());
                break;
            case "갤럭시 S23":
            case "갤럭시 퀀텀4":
            case "갤럭시 A24":
            case "갤럭시 A15":
            case "갤럭시 A25":
                imageResId = getResources().getIdentifier("galaxy_s23", "drawable", getPackageName());
                break;
            case "갤럭시 S23 FE":
                imageResId = getResources().getIdentifier("galaxy_s23_fe", "drawable", getPackageName());
                break;
            default:
                imageResId = getResources().getIdentifier("default_image", "drawable", getPackageName());
                break;
        }

        ImageView modelImage = findViewById(R.id.modelImage);
        modelImage.setImageResource(imageResId);
    }

    private void updateManufacturerText(String modelName) {

        if (modelName.contains("아이폰")) {
            tv6_1.setText("Apple");
        } else {
            tv6_1.setText("SamSung");
        }
    }

    private void setupBackButton() {
        backButton.setOnClickListener(v -> onBackPressed());
    }

}