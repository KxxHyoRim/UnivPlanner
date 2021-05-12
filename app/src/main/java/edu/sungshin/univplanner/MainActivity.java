package edu.sungshin.univplanner;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
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
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button b;
    Toolbar toolbar;

    private ViewPager mViewPager;
    private FragmentManager fm;
    private ArrayList<Fragment> fList;

    /*---------- 효림 -------*/
    TextView MainCenterText;
    final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36";
    private String loginUrl = "https://lms.sungshin.ac.kr/ilos/main/member/login_form.acl";
    private String htmlPageUrl = "https://lms.sungshin.ac.kr/ilos/main/main_form.acl";
    private String registerListUrl = "https://lms.sungshin.ac.kr/ilos/mp/course_register_list_form.acl";

    private String user_id = "20191012";
    private String user_pw = "";            //push 할떄만 비밀번호 가려서 올릴게~~~~ (효림)

    private String htmlContentInStringFormat = "";

    /******************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 주석해제하면 상태바 없어짐(로그인화면)
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main);


        MainCenterText = (TextView) findViewById(R.id.MainCenterText);
        MainCenterText.setMovementMethod(new ScrollingMovementMethod());

        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();


        // 스와이프할 뷰페이저를 정의
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        // 프라그먼트 매니져 객체 정의
        fm = getSupportFragmentManager();

        // 각 탭에 들어갈 프라그먼트 생성 및 추가
        fList = new ArrayList<Fragment>();
        fList.add(Fragment_lecture.newInstance());
        fList.add(Fragment_assignment.newInstance());

//        // 스와이프로 탭간 이동할 뷰페이저의 리스너 설정
//        mViewPager.setOnPageChangeListener(viewPagerListener);

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
                        break;
                }

                DrawerLayout drawer = findViewById(R.id.drawerLayout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    /**크롤링 하는 코드 : 효림 */

    private class JsoupAsyncTask extends AsyncTask<Void, Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try{

                Connection.Response initial = Jsoup.connect(loginUrl)
//                        .header("Accept", "*/*")
//                        .header("Referer", "https://lms.sungshin.ac.kr/ilos/main/main_form.acl")
//                        .header("Content-Type","application/x-www-form-urlencoded; charset=UTF-8")
//                        .header("Accept-Encoding", "gzip, deflate, br")
//                        .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
//                        .header("sec-ch-ua", "\"Google Chrome\";v=\"90\", \"Not;A Brand\";v=\"99\",\"Chromium\";v=\"90\"")
//                        .header("sec-ch-ua-mobile", "?1")
//                        .header("Sec-Fetch-Dest","document")
//                        .header("Sec-Fetch-Mode", "navigate")
//                        .header("Sec-Fetch-Site", "same-origin")
                        .method(Connection.Method.GET)
                        .execute();

                Map<String, String> initialCookie = initial.cookies();

                Document loginDocument = initial.parse();
                String returnURL = loginDocument.select("input.returnURL").val();
                String challenge = loginDocument.select("input.challenge").val();
                String response = loginDocument.select("input.response").val();
                String RelayState = loginDocument.select("input.RelayState").val();

                Connection.Response login = Jsoup.connect(loginUrl)
                        .userAgent(USER_AGENT)
                        .header("Accept", "*/*")
                        .header("Accept-Encoding", "gzip, deflate, br")
                        .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                        .header("Connection", "keep-alive")
                        .header("Host", "lms.sungshin.ac.kr")
                        .header("Referer", "https://lms.sungshin.ac.kr/ilos/main/main_form.acl")
//                        .header("Content-Type","application/x-www-form-urlencoded; charset=UTF-8")
                        .header("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"90\", \"Google Chrome\";v=\"90\"")
                        .header("sec-ch-ua-mobile", "?0")
                        .header("Sec-Fetch-Dest","empty")
                        .header("Sec-Fetch-Mode", "cors")
                        .header("Sec-Fetch-Site", "same-origin")
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36")
                        .header("X-Requested-With","XMLHttpRequest")
                        .cookies(initialCookie)
//                        .data("login_div", "sso")
                        .data("loginId", user_id)
                        .data("loginPwd", user_pw)
//                        .data("returnURL", returnURL)
//                        .data("target", "")
                        .method(Connection.Method.POST)
                        .execute();



                Map<String, String> coky = login.cookies();

                Document doc = Jsoup.connect(htmlPageUrl)
                        .cookies(coky)
                        .get();


                Elements contents = doc.select("div.m-box2 li");
                for (Element content : contents){
                    System.out.println("check" + content.text());
                    if (content.text().substring(0,3).equals("null")) {
                        String temp = content.text().substring(4);
                        htmlContentInStringFormat += (temp + "\n");
                        continue;
                    }
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
            MainCenterText.setText(htmlContentInStringFormat);
        }
    }

}