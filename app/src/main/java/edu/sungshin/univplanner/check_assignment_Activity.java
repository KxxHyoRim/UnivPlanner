package edu.sungshin.univplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class check_assignment_Activity extends AppCompatActivity {
    private FirebaseDatabase myFirebaseDatabase;
    private DatabaseReference myDatabaseReference;
    private ChildEventListener myChildEventListener;
    private FirebaseAuth mAuth;
    cal[] cals = new cal[50];
    int cal_count = 0;

    String lecture_fullList;
    String full_percentage;
    int totalLectureNum;
    String[] lectureName_array;
    String[] percentage_array;
    int percentage_sum = 0;
    String isDone;

    String full_assignment;
    String[] assignment_array;
    TextView isDone_textView;

    TextView nav_name;
    TextView nav_std_number;
    String name_from_firebase;
    String id_from_firebase;

    Button b;
    Toolbar toolbar;

    ViewPager mViewPager;
    private ArrayList<Fragment> fList;

    ImageView univLogo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_assignment);

        univLogo = (ImageView) findViewById(R.id.assignment_univLogo);
        univLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 스와이프할 뷰페이저를 정의
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        //페이지 갯수지정
        mViewPager.setOffscreenPageLimit(1);

        //어댑터 객체 생성
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());

        // 각 탭에 들어갈 프라그먼트 생성 및 추가
        fragment_assignment fragment2 = new fragment_assignment();
        adapter.addItem(fragment2);

        mViewPager.setAdapter(adapter);

        //toolBar를 통해 App Bar 생성
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
                switch (menuItem.getItemId()) {

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

        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String userInfo = user.getUid();
        Log.e("fb uid", userInfo);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://univp-1db5d-default-rtdb.asia-southeast1.firebasedatabase.app/");

        // firebase에서 학생 이름 가져오기
        DatabaseReference myRef2 = database.getReference("User").
                child(userInfo).child("name");

        // navigation bar에 학생 이름 설정하기
        myRef2.addValueEventListener(new ValueEventListener() {
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
        DatabaseReference myRef3 = database.getReference("User").
                child(userInfo).child("id");

        // navigation bar에 학생 학번 설정하기
        myRef3.addValueEventListener(new ValueEventListener() {
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
