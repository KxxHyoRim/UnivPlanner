package edu.sungshin.univplanner;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class SettingActivity extends AppCompatActivity {
    private FirebaseDatabase myFirebaseDatabase;
    private DatabaseReference myDatabaseReference;
    private ChildEventListener myChildEventListener;
    private FirebaseAuth mAuth;

    final int GET_GALLERY_IMAGE = 200;
    ImageView imageView;
    String imgName = "profile.png";

    TextView set_name;
    TextView set_std_number;
    String name_from_firebase;
    String id_from_firebase;

    TextView nav_name;
    TextView nav_std_number;

    ImageView univLogo;

    Boolean isClick = false;
    TextView contactUs;
    EditText contactUsText;
    ImageView sendBtn;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        contactUs = findViewById(R.id.contactUs);
        contactUsText = findViewById(R.id.contactUsText);
        sendBtn = findViewById(R.id.sendBtn);

        // 문의하기 Textview
        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 문의하기 EditText랑 Send버튼 숨기기/보이기
                isClick = !isClick;
                if (isClick){
                    contactUsText.setVisibility(View.VISIBLE);
                    sendBtn.setVisibility(View.VISIBLE);
                }else {
                    contactUsText.setVisibility(View.GONE);
                    sendBtn.setVisibility(View.GONE);
                }
            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendBtn.getVisibility() == View.VISIBLE){
                    String complain = String.valueOf(contactUsText.getText());
                    Log.e("complain", complain);

                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.setType("plain/text");
                    String[] address = {"sswu.univplanner@gmail.com"};
                    email.putExtra(Intent.EXTRA_EMAIL, address);
//                    email.putExtra(Intent.EXTRA_SUBJECT, "test@test");
                    email.putExtra(Intent.EXTRA_TEXT, complain);
                    startActivity(email);

                }
            }
        });

        univLogo = (ImageView) findViewById(R.id.assignment_univLogo);
        univLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        imageView = (ImageView) findViewById(R.id.imageView);

        set_name = (TextView) findViewById(R.id.std_name);
        set_std_number = (TextView) findViewById(R.id.std_num);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userInfo = user.getUid();
        Log.e("fb uid", userInfo);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://univp-1db5d-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef2 = database.getReference("User").
                child(userInfo).child("name");

        // navigation bar에 학생 이름 설정하기
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name_from_firebase =  snapshot.getValue(String.class);
                Log.e("nav_std_name", name_from_firebase + "");
                set_name.setText(name_from_firebase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {  }
        });

        // firebase에서 학생 학번(id) 가져오기
        DatabaseReference myRef3 = database.getReference("User").
                child(userInfo).child("id");

        // navigation bar에 학생 학번 설정하기
        myRef3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                id_from_firebase =  snapshot.getValue(String.class);
                Log.e("nav_std_id", id_from_firebase + "");
                set_std_number.setText(id_from_firebase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        try {
            String imgpath = getCacheDir() + "/" + imgName;   // 내부 저장소에 저장되어 있는 이미지 경로
            Bitmap bm = BitmapFactory.decodeFile(imgpath);
            imageView.setImageBitmap(bm);   // 내부 저장소에 저장된 이미지를 이미지뷰에 셋
            if(bm == null)
                imageView.setImageResource(R.drawable.profile);
        } catch (Exception e) {
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] versionArray = new String[] { "프로밀 이미지 삭제", "프로필 이미지 변경"};
                AlertDialog.Builder dlg = new AlertDialog.Builder(SettingActivity.this);
                dlg.setItems(versionArray,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0){
                                    try {
                                        File file = getCacheDir();  // 내부저장소 캐시 경로를 받아오기
                                        File[] flist = file.listFiles();
                                        for (int i = 0; i < flist.length; i++) {    // 배열의 크기만큼 반복
                                            if (flist[i].getName().equals(imgName)) {   // 삭제하고자 하는 이름과 같은 파일명이 있으면 실행
                                                flist[i].delete();  // 파일 삭제
                                            }
                                        }
                                    } catch (Exception e) {
                                    }
                                    imageView.setImageResource(R.drawable.profile);
                                }
                                else if(which==1){
                                    Intent intent = new Intent();
                                    intent.setType("image/*");
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(intent, GET_GALLERY_IMAGE);
                                }
                            }
                        });
                dlg.show();
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //App Bar의 좌측 영영에 Drawer를 Open 하기 위한 Incon 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24);

        DrawerLayout drawLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawLayout,
                toolbar,
                R.string.open,
                R.string.closed
        );


        drawLayout.addDrawerListener(actionBarDrawerToggle);

        //네비게이션 메뉴들 클릭할 때, 동작하는 이벤트
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {

                    case R.id.lecture:
                        Intent intent0 = new Intent(getApplicationContext(), check_lecture_Activity.class);
                        startActivity(intent0);
                        break;
                    case R.id.assignment:
                        Intent intent3 = new Intent(getApplicationContext(), check_assignment_Activity.class);
                        startActivity(intent3);
                        break;
                    case R.id.my_todo:
                        Intent intent4 = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent4);
                        break;
                    case R.id.setting:
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.logout:
                        Intent intent2 = new Intent(getApplicationContext(), login.class);
                        startActivity(intent2);
                        finish();
                        break;
                }

                DrawerLayout drawer = findViewById(R.id.drawerLayout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        NavigationView navView= (NavigationView) findViewById(R.id.navigationView);
        View nav_view=navView.getHeaderView(0);
        nav_name = (TextView) nav_view.findViewById(R.id.nav_name);
        nav_std_number = (TextView) nav_view.findViewById(R.id.nav_std_number);


        // firebase에서 학생 이름 가져오기
        DatabaseReference myRef4 = database.getReference("User").
                child(userInfo).child("name");

        // navigation bar에 학생 이름 설정하기
        myRef4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name_from_firebase =  snapshot.getValue(String.class);
                Log.e("nav_std_name", name_from_firebase + "");
                nav_name.setText(name_from_firebase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {  }
        });

        // firebase에서 학생 학번(id) 가져오기
        DatabaseReference myRef5 = database.getReference("User").
                child(userInfo).child("id");

        // navigation bar에 학생 학번 설정하기
        myRef5.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                id_from_firebase =  snapshot.getValue(String.class);
                Log.e("nav_std_id", id_from_firebase + "");
                nav_std_number.setText(id_from_firebase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE) {
            if (resultCode == RESULT_OK) {
                Uri fileUri = data.getData();
                ContentResolver resolver = getContentResolver();
                try {
                    InputStream instream = resolver.openInputStream(fileUri);
                    Bitmap imgBitmap = BitmapFactory.decodeStream(instream);
                    imageView.setImageBitmap(imgBitmap);    // 선택한 이미지 이미지뷰에 셋
                    instream.close();   // 스트림 닫아주기
                    saveBitmapToJpeg(imgBitmap);    // 내부 저장소에 저장
                } catch (Exception e) {
                }

            }
        }

    }
    public void saveBitmapToJpeg(Bitmap bitmap) {   // 선택한 이미지 내부 저장소에 저장
        File tempFile = new File(getCacheDir(), imgName);    // 파일 경로와 이름 넣기
        try {
            tempFile.createNewFile();   // 자동으로 빈 파일을 생성하기
            FileOutputStream out = new FileOutputStream(tempFile);  // 파일을 쓸 수 있는 스트림을 준비하기
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);   // compress 함수를 사용해 스트림에 비트맵을 저장하기
            out.close();    // 스트림 닫아주기
        } catch (Exception e) {
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}