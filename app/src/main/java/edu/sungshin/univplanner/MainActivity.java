package edu.sungshin.univplanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

class cal{
    public String day;
    public String month;
    public String year;
    public int ass_or_lec;
    public String name;
    public String isDone;

    public cal(String day, String month, String year, int ass_or_lec, String name, String isDone){
        this.day = day;
        this.month = month;
        this.year = year;
        this.ass_or_lec = ass_or_lec;
        this.name = name;
        this.isDone = isDone;
    }
    public boolean check_ym(String year, String month){
        return (this.year.equals(year) && this.month.equals(month));
    }
    public boolean check_ymd(String year, String month, String day){
        return (this.year.equals(year) && this.month.equals(month) && this.day.equals(day));
    }
}

public class MainActivity extends AppCompatActivity {
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

    /*---------- 효림 -------*/
    //TextView MainCenterText;
    final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36";
    private String loginUrl = "https://lms.sungshin.ac.kr/ilos/main/member/login_form.acl";
    private String actionLoginUrl = "https://lms.sungshin.ac.kr/ilos/sso/CreateRequestAuth.jsp";
    private String htmlPageUrl = "https://lms.sungshin.ac.kr/ilos/main/main_form.acl";
    private String registerListUrl = "https://lms.sungshin.ac.kr/ilos/mp/course_register_list_form.acl";

    private String user_id = "20191012";
    private String user_pw = "";            //push 할떄만 비밀번호 가려서 올릴게~~~~ (효림)

    private String htmlContentInStringFormat = "";

    /******************************************/

    //달력
    TextView[] day = new TextView[42];
    Integer[] dayid = {R.id.day1, R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6, R.id.day7,
            R.id.day8, R.id.day9, R.id.day10, R.id.day11, R.id.day12, R.id.day13, R.id.day14,
            R.id.day15, R.id.day16, R.id.day17, R.id.day18, R.id.day19, R.id.day20, R.id.day21,
            R.id.day22, R.id.day23, R.id.day24, R.id.day25, R.id.day26, R.id.day27, R.id.day28,
            R.id.day29, R.id.day30, R.id.day31, R.id.day32, R.id.day33, R.id.day34, R.id.day35,
            R.id.day36, R.id.day37, R.id.day38, R.id.day39, R.id.day40, R.id.day41, R.id.day42};
    TextView[] sche = new TextView[42];
    Integer[] scheid = {R.id.sche1, R.id.sche2, R.id.sche3, R.id.sche4, R.id.sche5, R.id.sche6, R.id.sche7,
            R.id.sche8, R.id.sche9, R.id.sche10, R.id.sche11, R.id.sche12, R.id.sche13, R.id.sche14,
            R.id.sche15, R.id.sche16, R.id.sche17, R.id.sche18, R.id.sche19, R.id.sche20, R.id.sche21,
            R.id.sche22, R.id.sche23, R.id.sche24, R.id.sche25, R.id.sche26, R.id.sche27, R.id.sche28,
            R.id.sche29, R.id.sche30, R.id.sche31, R.id.sche32, R.id.sche33, R.id.sche34, R.id.sche35,
            R.id.sche36, R.id.sche37, R.id.sche38, R.id.sche39, R.id.sche40, R.id.sche41, R.id.sche42};
    Integer[] nameid = {R.id.name1, R.id.name2, R.id.name3, R.id.name4, R.id.name5, R.id.name6, R.id.name7,
            R.id.name8, R.id.name9, R.id.name10, R.id.name11};
    Integer[] isDoneid = {R.id.isDone1, R.id.isDone2, R.id.isDone3, R.id.isDone4, R.id.isDone5, R.id.isDone6, R.id.isDone7,
            R.id.isDone8, R.id.isDone9, R.id.isDone10, R.id.isDone11};
    int unchecked = 0;
    int start_day = 0;
    boolean[] clickable = new boolean[42];

    int last_year;
    String last_month;
    int last_day = 0;

    int today_day;
    String today_month;
    String today_year;

    Button btn_previous;
    Button btn_next;
    TextView year_month;

    boolean isLoginSuccess, isSychronizedDone;
    String lectureNameList;
    Vector<String> lectureNameVec = new Vector<String>();
    Vector<String> lecturePercentVec = new Vector<String>();
    Vector<String> lectureAssignmentVec = new Vector<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 주석해제하면 상태바 없어짐(로그인화면)
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main);

        isSychronizedDone = true;
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.e("Swipe", "!!");
                isSychronizedDone = false;
                Toast.makeText(MainActivity.this,
                        "동기화를 시작합니다", Toast.LENGTH_LONG).show();
                MainActivity.ClientThread thread = new MainActivity.ClientThread();
                thread.start();
            }
        });

        if (isSychronizedDone) {
            swipeRefreshLayout.setRefreshing(false);
        }

        //MainCenterText = (TextView) findViewById(R.id.MainCenterText);

        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();


        // 스와이프할 뷰페이저를 정의
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        //페이지 갯수지정
        mViewPager.setOffscreenPageLimit(2);

        //어댑터 객체 생성
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());

        // 각 탭에 들어갈 프라그먼트 생성 및 추가
        fragment_lecture fragment1 = new fragment_lecture();
        adapter.addItem(fragment1);
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
                        Intent intent3 = new Intent(getApplicationContext(), check_lecture_Activity.class);
                        startActivity(intent3);
                        break;
                    case R.id.my_todo:
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
                            //listview_adapter.notifyDataSetChanged();
                            initialize();
                            setLast_day(last_month);
                            month(start_day);
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

        btn_previous = (Button) findViewById(R.id.previous);
        btn_next = (Button) findViewById(R.id.next);
        year_month = (TextView) findViewById(R.id.year_month);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

        String getTime = sdf.format(date);
        today_year = getTime.substring(0,4);
        today_month = getTime.substring(5,7);
        today_day = Integer.parseInt(getTime.substring(8, 10));

        last_year = Integer.parseInt(getTime.substring(0,4));
        last_month = getTime.substring(5,7);
        year_month.setText(getTime.substring(0,7));

        DateFormat dateFormat = new SimpleDateFormat("yyyy.MMdd");
        try {
            String inputdate = getTime.substring(0, 7) + "01";
            Date date2 = dateFormat.parse(inputdate);
            Calendar c = Calendar.getInstance();
            c.setTime(date2);
            start_day = c.get(Calendar.DAY_OF_WEEK);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for(int i=0;i<42;i++){
            day[i] = (TextView) findViewById(dayid[i]);
        }

        for(int i=0;i<42;i++){
            sche[i] = (TextView) findViewById(scheid[i]);
        }

        for(int i=0;i<42;i++){
            final int index;
            index = i;

            day[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*if(clickable[index]==true)
                        sche[41].setText("true");
                    else
                        sche[41].setText("false");*/
                    if(clickable[index] == true){
                        //day[index].setBackgroundResource(R.drawable.textview_border);
                        /*
                        day[unchecked].setBackgroundResource(0);
                        unchecked = index;
                        day[index].setBackgroundResource(R.drawable.textview_border);*/

                        Display display = getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);

                        View dialog_view = (View) View.inflate(MainActivity.this, R.layout.dialog, null);
                        TextView text_day = (TextView) dialog_view.findViewById(R.id.clickday);
                        TextView text_day2 = (TextView) dialog_view.findViewById(R.id.clickday2);
                        text_day.setText(day[index].getText()+"일");
                        //text_day.setTextColor(day[index].getTextColors());
                        String[] day_list = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};

                        text_day2.setText(day_list[index % 7]);
                        //text_day2.setTextColor(day[index].getTextColors());
                        if(index % 7 == 0){
                            text_day.setTextColor(Color.parseColor("#ED0000"));
                            text_day2.setTextColor(Color.parseColor("#ED0000"));
                        }
                        else if(index % 7 == 6){
                            text_day.setTextColor(Color.parseColor("#0042ED"));
                            text_day2.setTextColor(Color.parseColor("#0042ED"));
                        }
                        else{
                            text_day.setTextColor(Color.BLACK);
                            text_day2.setTextColor(Color.BLACK);
                        }

                        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                        dlg.setView(dialog_view);
                        Dialog dialog = dlg.create();
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                        lp.dimAmount = 0.5f;
                        Window window = dialog.getWindow();
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        dialog.show();
                        int x = (int)(size.x * 0.8f);
                        int y = (int)(size.y * 0.49f);
                        window.setAttributes(lp);
                        window.setLayout(x, y);

                        int sche_count = 0;
                        String year = year_month.getText().toString().substring(0,4);
                        String month = year_month.getText().toString().substring(5,7);
                        String day_s = day[index].getText().toString();

                        if(day_s.length() == 1)
                            day_s = "0" + day_s;

                        for(int j=0;j<cal_count;j++){
                            final int index2;
                            index2 = j;
                            if(cals[index2].check_ymd(year, month, day_s)){
                                TextView name = (TextView) dialog_view.findViewById(nameid[sche_count]);
                                TextView isDone = (TextView) dialog_view.findViewById(isDoneid[sche_count]);

                                String name_1 = cals[index2].ass_or_lec == 1 ? "[강의] " : "[과제] ";
                                String name_2 = cals[index2].name;
                                name.setText(name_1 + name_2);
                                name.setTextColor(Color.BLACK);
                                String isDone_1 = cals[index2].isDone;

                                if(isDone_1.equals("수강완료") || isDone_1.equals("제출"))
                                    isDone.setTextColor(Color.parseColor("#0B7903"));
                                else if(isDone_1.equals("미수강") || isDone_1.equals("미제출"))
                                    isDone.setTextColor(Color.parseColor("#B71C1C"));
                                isDone.setText(isDone_1);
                                sche_count++;
                            }
                        }
                    }
                }
            });
        }

        initialize();
        setLast_day(last_month);
        month(start_day);


        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = Integer.parseInt(year_month.getText().toString().substring(0,4));
                int month = Integer.parseInt(year_month.getText().toString().substring(5,7));
                if(month != 1)
                    month -= 1;
                else{
                    year -= 1;
                    month = 12;
                }
                String date;
                if(month == 10 || month == 11 || month == 12)
                    date = Integer.toString(year) + "." + Integer.toString(month);
                else
                    date = Integer.toString(year) + ".0" + Integer.toString(month);
                year_month.setText(date);
                last_year = Integer.parseInt(year_month.getText().toString().substring(0,4));
                last_month = year_month.getText().toString().substring(5,7);
                setStart_day(date);
                initialize();
                setLast_day(last_month);
                month(start_day);
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = Integer.parseInt(year_month.getText().toString().substring(0,4));
                int month = Integer.parseInt(year_month.getText().toString().substring(5,7));
                if(month != 12)
                    month += 1;
                else{
                    year += 1;
                    month = 1;
                }
                String date;
                if(month == 10 || month == 11 || month == 12)
                    date = Integer.toString(year) + "." + Integer.toString(month);
                else
                    date = Integer.toString(year) + ".0" + Integer.toString(month);
                year_month.setText(date);
                last_year = Integer.parseInt(year_month.getText().toString().substring(0,4));
                last_month = year_month.getText().toString().substring(5,7);
                setStart_day(date);
                initialize();
                setLast_day(last_month);
                month(start_day);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            finish();
        }
    }

    /**크롤링 하는 코드 : 효림 */

    private class JsoupAsyncTask extends AsyncTask<Void, Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try{

                // 1. Go to login page
                Connection.Response initial = Jsoup.connect(loginUrl)
                        .method(Connection.Method.GET)
                        .userAgent(USER_AGENT)
                        .execute();

                FormElement loginForm = (FormElement) initial.parse().selectFirst("#myform");

                Element idField = loginForm.selectFirst("#usr_id");
                idField.val(user_id);

                Element pwField = loginForm.selectFirst("#usr_pwd");
                pwField.val(user_pw);

                System.out.println("====loginForm / after ======");
                System.out.println(loginForm);


                Connection.Response loginActionResponse = loginForm.submit()
                        .cookies(initial.cookies())
//                        .data(formData)
                        .userAgent(USER_AGENT)
                        .execute();

                Document d = loginActionResponse.parse();
                System.out.println("url==========");
                System.out.println(d.location());
                System.out.println("cookies==========");
                System.out.println(loginActionResponse.cookies());

                Document doc = Jsoup.connect("https://lms.sungshin.ac.kr/ilos/mp/course_register_list_form.acl")
                        .cookie("JSESSIONID",loginActionResponse.cookie("JSESSIONID") )
                        .userAgent(USER_AGENT)
                        .post();

                htmlContentInStringFormat += doc.text();


                Elements contents = doc.select("div.m-box2 li");
                for (Element content : contents){
                    htmlContentInStringFormat += (content.text().trim() + "\n");
                }

            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //MainCenterText.setText(htmlContentInStringFormat);
            // 호연 크롤링부분 login에서 intent로 string 받아옴
            Intent loginIntent = getIntent();
            String crawlingText = loginIntent.getStringExtra("crawlingText");
            //MainCenterText.setText(crawlingText);
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

    public void setStart_day(String date){
        String inputdate = date + "01";
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MMdd");
        try {
            Date date3 = dateFormat.parse(inputdate);
            Calendar c = Calendar.getInstance();
            c.setTime(date3);
            start_day = c.get(Calendar.DAY_OF_WEEK);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void month(int start_day2){
        /*for(int i=0;i<start_day2-1;i++){
            final int index;
            index = i;
            day[index].setEnabled(false);
        }*/
        String year = year_month.getText().toString().substring(0,4);
        String month = year_month.getText().toString().substring(5,7);
        for(int i=start_day2-1, j = 1;j<=last_day;i++, j++){
            final int index;
            index = i;
            day[index].setText(Integer.toString(j));
            /*
            clickable[index] = true;
            day[index].setBackgroundResource(R.drawable.click);
             */
            if(year.equals(today_year) && month.equals(today_month) && j == today_day){
                //Spannable span = (Spannable) day[index].getText();
                //span.setSpan(new BackgroundColorSpan(Color.rgb(187, 134, 252)), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                day[index].setBackgroundResource(R.drawable.today);
                day[index].setTextColor(Color.WHITE);
            }
        }
        /*
        for(int i=start_day2+last_day-1;i<42;i++){
            final int index;
            index = i;
            day[index].setEnabled(false);
        }*/
        //year_month.setText(Integer.toString(start_day2-2)+" "+ Integer.toString(start_day2+last_day-1));

        for(int i=0;i<cal_count;i++){
            final int index;
            index = i;
            int index2 = Integer.parseInt(cals[index].day) + start_day - 2;
            if(cals[index].check_ym(year, month)){
                day[index2].setBackgroundResource(R.drawable.click);
                clickable[index2] = true;
                sche[index2].setBackgroundResource(R.drawable.underline);
                if(cals[index].check_ymd(today_year, today_month, Integer.toString(today_day)))
                    day[index2].setBackgroundResource(R.drawable.today_click);
            }
        }
    }

    public void initialize(){
        //day[unchecked].setBackgroundResource(0);
        for(int i=0;i<42;i++){
            final int index;
            index = i;
            day[index].setText("");
            clickable[index] = false;
            day[index].setBackgroundResource(0);
            sche[index].setBackgroundResource(0);
            if(index % 7 == 0)
                day[index].setTextColor(Color.parseColor("#ED0000"));
            else if(index % 7 == 6)
                day[index].setTextColor(Color.parseColor("#0042ED"));
            else
                day[index].setTextColor(Color.BLACK);
        }
    }

    public void setLast_day(String last_day2) {
        switch (last_day2){
            case "01":
            case "03":
            case "05":
            case "07":
            case "08":
            case "10":
            case "12":
                last_day = 31;
                break;
            case "02":
                if(last_year % 4 == 0 && last_year % 100 != 0 || last_year % 400 == 0)
                    last_day = 29;
                else
                    last_day = 28;
                break;
            case "04":
            case "06":
            case "09":
            case "11":
                last_day = 30;
                break;
        }
    }

    protected class ClientThread extends Thread {
        public void run() {
            String host = "13.124.79.16";
            int port = 8080;

            try {
                Log.e("sck", "start");
                Socket socket = new Socket(host, port);
                Log.e("sck", "suc");

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                SharedPreferences pref = getSharedPreferences("saveID",MODE_PRIVATE);
                String saveIDdata = pref.getString("id","");

                pref = getSharedPreferences("savePW",MODE_PRIVATE);
                String savePWdata = pref.getString("pw","");

                out.println(saveIDdata);
                Log.e("send", saveIDdata);

                out.println(savePWdata);
                Log.e("send", savePWdata);

                String rev = in.readLine();
                Log.e("receive", rev);

                if (rev.equals("Success")) {
                    String userName = in.readLine();	//outer Lecture number

                    String totalLectureNumStr = in.readLine();	//outer Lecture number
                    Log.e("total Lecture Num", totalLectureNumStr);
                    int totalLectureNum = Integer.parseInt(totalLectureNumStr);

                    String realTotalLectureNumStr = in.readLine();	//outer Lecture number
                    Log.e("real total Lecture Num", realTotalLectureNumStr);
                    int realTotalLectureNum = Integer.parseInt(realTotalLectureNumStr);

                    lectureNameList = "";

                    for (int i = 0; i < realTotalLectureNum; i++) {
                        String lectureTitle = in.readLine();	// outer lecture title

                        if (lectureTitle.equals("LectureDone")) {   // if 비정규과목, break
                            break;
                        }

                        lectureNameList += (lectureTitle + "\n");
                        lectureNameVec.add(lectureTitle);

                        Log.e("outer lecture title", lectureTitle);

                        String innerLectureNumStr = in.readLine();	// inner lecture number
                        Log.e("inner lecture num: ", innerLectureNumStr);
                        int innerLectureNum = Integer.parseInt(innerLectureNumStr);

                        String innerLecturePercentStr = "";
                        innerLecturePercentStr += (innerLectureNum + "\n");

                        if (innerLectureNum > 0) {
                            String innerLecturePeriod = in.readLine();	// inner lecture number
                            innerLecturePercentStr += (innerLecturePeriod + "\n");
                            Log.e("inner lecture period: ", innerLecturePeriod);
                        }

                        for (int j = 0; j < innerLectureNum; j++) {
                            String innerLecturePer = in.readLine();	// inner lecture percentage text
                            innerLecturePercentStr += (innerLecturePer + " ");
                            Log.e("inner lecture percent: ", innerLecturePer);
                        }

                        String innerAssignmentStr = "";
                        String innerAssignmentNumStr = in.readLine();	// inner lecture number
                        Log.e("total assignment num: ", innerAssignmentNumStr);

                        if (!innerAssignmentNumStr.equals("AssignmentDone")) {
                            int innerAssignmentNum = Integer.parseInt(innerAssignmentNumStr);
                            int realAssignNum = 0;

                            for (int j = 0; j < innerAssignmentNum; j++) {
                                String assignmentName = in.readLine();	// inner lecture percentage text

                                if (assignmentName.equals("AssignmentDone")) {
                                    break;
                                }

                                innerAssignmentStr += (assignmentName + "\n");
                                Log.e("inner assign name:", assignmentName);

                                String isAssignmentSubmitted = in.readLine();	// inner lecture percentage text
                                innerAssignmentStr += (isAssignmentSubmitted + "\n");
                                Log.e("inner assign submitted:", isAssignmentSubmitted);

                                String assignmentPeriod = in.readLine();	// inner lecture percentage text
                                innerAssignmentStr += (assignmentPeriod + "\n");
                                Log.e("inner assign period:", assignmentPeriod);
                                realAssignNum++;
                            }

                            innerAssignmentStr = realAssignNum + "\n" + innerAssignmentStr;
                        }

                        else {
                            innerAssignmentStr += "0\n";
                        }

                        lecturePercentVec.add(innerLecturePercentStr);
                        lectureAssignmentVec.add(innerAssignmentStr);
                    }

                    String realLectureNumStr = in.readLine();	// inner lecture number
                    Log.e("Real Lecture Num", realLectureNumStr);

                    Log.e("lectureNameVec", lectureNameVec.size() + "");
                    Log.e("lecturePercentVec", lecturePercentVec.size() + "");
                    Log.e("lectureAssignmentVec", lectureAssignmentVec.size() + "");

                    firebaseSignUp(userName);
                }
            }

            catch (Exception e) {
                isLoginSuccess = false;

                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this,
                                "동기화를 실패했습니다", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("sck", "fail");
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }

    private void firebaseSignUp(String userName) {
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.e("firebase", "signup", task.getException());
                    FirebaseUser user = mAuth.getCurrentUser();
                    assert user != null;
                    String userInfo = user.getUid();
                    Log.e("fb uid", userInfo);

                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://univp-1db5d-default-rtdb.asia-southeast1.firebasedatabase.app/");

                    DatabaseReference myRef = database.getReference("User").child(userInfo).child("lectureName");
                    myRef.setValue(lectureNameVec.size() + "\n" + lectureNameList);

                    for (int i = 0; i < lectureNameVec.size(); i++) {
                        String lectureName = lectureNameVec.get(i);
                        myRef = database.getReference("User").child(userInfo).child(lectureName).child("percentage");
                        String lecturePercent = lecturePercentVec.get(i);
                        myRef.setValue(lecturePercent);
                        Log.e("setFB percent", i + "");

                        myRef = database.getReference("User").child(userInfo).child(lectureName).child("assignment");
                        String lectureAssignment = lectureAssignmentVec.get(i);
                        myRef.setValue(lectureAssignment);
                        Log.e("setFB assign", i + "");
                    }

                    Toast.makeText(MainActivity.this,
                            "동기화에 성공했습니다", Toast.LENGTH_SHORT).show();

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }

                else {
                    // If sign in fails, display a message to the user.
                    Log.e("firebase", "signupFail", task.getException());
                }
            }
        });
    }
}