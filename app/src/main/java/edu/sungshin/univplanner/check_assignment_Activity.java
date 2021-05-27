package edu.sungshin.univplanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.GregorianCalendar;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_assignment);

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userInfo = user.getUid();
        Log.e("fb uid", userInfo);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://univp-1db5d-default-rtdb.asia-southeast1.firebasedatabase.app/");
        /*---------수강과목 리스트 먼저 가져오기-------------*/
        DatabaseReference myRef = database.getReference("User").child(userInfo).child("lectureName");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot){
                lecture_fullList = snapshot.getValue(String.class);
                //Log.e("load letureName:", lecture_fullList + "");

                lectureName_array = lecture_fullList.split("\n");
                totalLectureNum = Integer.parseInt(lectureName_array[0]);
                Log.e("total_lecture_num", totalLectureNum + "");

                for(int i=1; i<totalLectureNum+1;i++){
                    String lectureName = lectureName_array[i];


                    DatabaseReference percentageRef = database.getReference("User").child(userInfo).child(lectureName).child("percentage");
                    percentageRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NotNull DataSnapshot snapshot){
                            full_percentage = snapshot.getValue(String.class);
                            Log.e("lecture Name", lectureName);

                            percentage_array = full_percentage.split("\n");
                            int percentage_num = Integer.parseInt(percentage_array[0]);
                            Log.e("percentage_num", percentage_num + "");

                            if (percentage_num!=0) {
                                String lecture_deadline = percentage_array[1].substring(9,percentage_array[1].length());
                                Log.e("lecture_deadline", lecture_deadline + "");

                                String deadline_Date = lecture_deadline.substring(lecture_deadline.lastIndexOf("~")+2,lecture_deadline.lastIndexOf("~")+12);
                                String year = deadline_Date.substring(0, 4);
                                String month = deadline_Date.substring(5, 7);
                                String day = deadline_Date.substring(8, 10);
                                int ass_or_lec = 1;
                                long d_day = Dday(deadline_Date);  //디데이 구하기
                                Log.e("강의 수강 퍼센트", percentage_array[2] + "");
                                //수강도 (퍼센트 구하기)
                                for(int j=0; j<percentage_num; j++){
                                    percentage_sum += Integer.parseInt(percentage_array[2].substring(0,percentage_array[2].indexOf("%")));
                                    percentage_array[2] = percentage_array[2].substring(percentage_array[2].indexOf("%")+2,percentage_array[2].length());
                                }

                                int percentage_average = percentage_sum/percentage_num;
                                //myprogress_bar.setIndeterminate(false);
                                //myprogress_bar.setProgress(percentage_average);

                                Log.e("수강도", percentage_average + "%");

                                if(percentage_average==100)
                                    isDone = "수강완료";
                                else
                                    isDone = "미수강";

                                if(d_day>=0)
                                    cals[cal_count++] = new cal(day, month, year, ass_or_lec, lectureName, isDone);
                                percentage_sum =0; // 다시 초기화
                            }
                            //listview_adapter.notifyDataSetChanged();
                            /*
                            initialize();
                            setLast_day(last_month);
                            month(start_day);
                             */
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error){}
                    });

                    DatabaseReference percentageRef2 = database.getReference("User").child(userInfo).child(lectureName).child("assignment");
                    percentageRef2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NotNull DataSnapshot snapshot){
                            full_assignment = snapshot.getValue(String.class);
                            Log.e("lecture Name", lectureName);

                            assignment_array = full_assignment.split("\n");
                            int assignment_num = Integer.parseInt(assignment_array[0]);
                            Log.e("percentage_num", assignment_num + "");

                            if (assignment_num!=0) {
                                String assignment_name = assignment_array[1];
                                Log.e("assignment_name", assignment_name + "");

                                String isDone_assignment = assignment_array[2];
                                Log.e("isDone_assignment", isDone_assignment + "");

                                String assignment_deadline = assignment_array[3];
                                String deadline_Date = assignment_deadline.substring(0,10);
                                String year = deadline_Date.substring(0, 4);
                                String month = deadline_Date.substring(5, 7);
                                String day = deadline_Date.substring(8, 10);
                                int ass_or_lec = 0;
                                long d_day = Dday(deadline_Date);  //디데이 구하기

                                if(d_day>=0)
                                    cals[cal_count++] = new cal(day, month, year, ass_or_lec, lectureName, isDone_assignment);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error){}
                    });
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){}
        });

        NavigationView navView= (NavigationView) findViewById(R.id.navigationView);
        View nav_view=navView.getHeaderView(0);
        nav_name = (TextView) nav_view.findViewById(R.id.nav_name);
        nav_std_number = (TextView) nav_view.findViewById(R.id.nav_std_number);

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

    public static long Dday(String mday){
        if(mday==null)
            return 0;
        mday = mday.trim();
        int first = mday.indexOf(".");
        int last = mday.lastIndexOf(".");
        int year = Integer.parseInt(mday.substring(0,first));
        int month = Integer.parseInt(mday.substring(first+1,last));
        int day = Integer.parseInt(mday.substring(last+1,mday.length()));

        GregorianCalendar cal = new GregorianCalendar();
        long currentTime = cal.getTimeInMillis() / (1000*60*60*24);
        cal.set(year,month-1,day);
        long birthTime = cal.getTimeInMillis() / (1000*60*60*24);
        int interval = (int)(birthTime-currentTime);

        return interval;
    }

}
