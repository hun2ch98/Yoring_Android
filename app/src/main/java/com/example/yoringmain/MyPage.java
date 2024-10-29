package com.example.yoringmain;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.widget.Toast;

import android.app.AlertDialog;
import android.content.DialogInterface;


public class MyPage extends AppCompatActivity {

    private static final String TAG = "MyPage";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private TextView nameTextView;
    private ImageView profileImageView,  heartImageView, btnChangeUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        mAuth = FirebaseAuth.getInstance();
        

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_mypage);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity(new Intent(MyPage.this, MainPage.class));
                        return true;
                    case R.id.nav_all_menu:
                        startActivity(new Intent(MyPage.this, MenuScript.class));
                        return true;
                    case R.id.nav_telsearch:
                        startActivity(new Intent(MyPage.this, AllPlan.class));
                        return true;
                    case R.id.nav_chain:
                        startActivity(new Intent(MyPage.this, Phone1.class));
                        return true;
                    case R.id.nav_mypage:
                        return true;
                    default:
                        return false;
                }
            }
        });

        TextView txt1, txt2;
        Button btnLogout, btn_delete_id, btn_change_use;

        nameTextView = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2); // 현재 로그인 상태를 표시하는 텍스트뷰, 추후에 코드 추가 예정
        profileImageView = findViewById(R.id.profile);
        btnLogout = findViewById(R.id.btn_logout);
        heartImageView = findViewById(R.id.heart); // 'heart' ID의 이미지뷰
        btnChangeUse = findViewById(R.id.btn_change_use); // 'btn_change_use' ID의 버튼
        btn_delete_id = findViewById(R.id.btn_delete_id); // 'btn_delete_id' ID의 버튼

        btnLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MyPage.this, LoginActivity.class));
            finish();
        });

        heartImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPage.this, MyChoice.class);
                startActivity(intent);
            }
        });

        // LoginActivity에서 전달된 사용자 정보 받기
        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        String userPhoto = intent.getStringExtra("userPhoto");

        // 사용자 정보 업데이트
        updateUI(userName, userPhoto);

        // 필요한 경우, 다른 버튼에 대한 클릭 리스너도 추가할 수 있습니다.
        btnChangeUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 'btn_change_use' 클릭 시 동작 추가
            }
        });

        btn_delete_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 'btn_delete_id' 클릭 시 동작 추가
            }
        });


        // MainActivity4에서 전달된 요금제 이름 받기
        // Intent intent = getIntent();
        String selectedPlanName = intent.getStringExtra("selectedPlanName");

        TextView planNameTextView = findViewById(R.id.txt4);
        planNameTextView.setText(selectedPlanName);

        // Google SignIn API에서 정보 가져오기
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    private void revokeAccess() {
        if (mAuth.getCurrentUser() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("정말로 계정을 삭제하시겠습니까?")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mAuth.getCurrentUser().delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                startActivity(new Intent(MyPage.this, LoginActivity.class));
                                                finish();
                                            } else {
                                                // 계정 삭제 실패 처리
                                                Toast.makeText(MyPage.this, "계정 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    })
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // 사용자가 "아니오"를 선택한 경우 처리
                            // 아무런 동작을 하지 않음
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            // 사용자가 로그인되어 있지 않은 경우의 처리
            Toast.makeText(this, "로그인되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 사용자 이름과 사진 URL로 UI 업데이트
    private void updateUI(String userName, String userPhoto) {
        if (userName != null) {
            // 프로필 이름 설정
            nameTextView.setText(userName);
        }

        // 프로필 사진 설정
        if (userPhoto != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(userPhoto);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        final Bitmap bitmap = BitmapFactory.decodeStream(input);

                        // UI 업데이트는 메인 스레드에서 수행되어야 합니다.
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                profileImageView.setImageResource(R.drawable.profile);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                profileImageView.setImageResource(R.drawable.profile);
                            }
                        });
                    }
                }
            }).start();
        } else {
            profileImageView.setImageResource(R.drawable.profile);
        }
    }

    // Google SignIn API에서 가져온 정보로 UI 업데이트하기
    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            // 프로필 이름 설정
            nameTextView.setText(account.getDisplayName());

            // 프로필 사진 설정
            if (account.getPhotoUrl() != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(account.getPhotoUrl().toString());
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setDoInput(true);
                            connection.connect();
                            InputStream input = connection.getInputStream();
                            final Bitmap bitmap = BitmapFactory.decodeStream(input);

                            // UI 업데이트는 메인 스레드에서 수행되어야 합니다.
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    profileImageView.setImageBitmap(bitmap);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    profileImageView.setImageResource(R.drawable.user); // 'user.png'로 기본 이미지 설정
                                }
                            });
                        }
                    }
                }).start();
            } else {
                profileImageView.setImageResource(R.drawable.user); // 'user.png'로 기본 이미지 설정
            }
        }
    }
}
