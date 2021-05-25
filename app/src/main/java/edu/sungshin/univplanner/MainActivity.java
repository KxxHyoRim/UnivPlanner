package edu.sungshin.univplanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
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
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 주석해제하면 상태바 없어짐(로그인화면)
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main);


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
                        Toast.makeText(getApplicationContext(), "강의 수강도 클릭", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.assignment:
                        Toast.makeText(getApplicationContext(), "과제 확인 클릭", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.my_todo:
                        Toast.makeText(getApplicationContext(), "나의 일정 클릭", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.setting:
                        Toast.makeText(getApplicationContext(), "환경 설정 클릭", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.logout:
                        Toast.makeText(getApplicationContext(), "로그아웃 클릭", Toast.LENGTH_SHORT).show();
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
                        dialog.show();
                        int x = (int)(size.x * 0.8f);
                        int y = (int)(size.y * 0.7f);
                        window.setAttributes(lp);
                        window.setLayout(x, y);
                    }
                }
            });
        }

        for(int i=0;i<42;i++) {
            final int index;
            index = i;
            sche[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickable[index] == true) {
                        /*
                        day[unchecked].setBackgroundResource(0);
                        unchecked = index;
                        day[index].setBackgroundResource(R.drawable.textview_border);
                        */
                        sche[index].setBackgroundResource(R.drawable.underline);
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
            clickable[index] = true;
            day[index].setText(Integer.toString(j));
            day[index].setBackgroundResource(R.drawable.click);
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
    }
    public void initialize(){
        //day[unchecked].setBackgroundResource(0);
        for(int i=0;i<42;i++){
            final int index;
            index = i;
            day[index].setText("");
            clickable[index] = false;
            day[index].setBackgroundResource(0);
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

}