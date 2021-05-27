package edu.sungshin.univplanner;

import android.content.SharedPreferences;
import android.graphics.Camera;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.function.Predicate;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_lecture#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_lecture extends Fragment  {

    //데이터베이스 값 가져오기
    private FirebaseDatabase myFirebaseDatabase;
    private DatabaseReference myDatabaseReference;
    private ChildEventListener myChildEventListener;
    private FirebaseAuth mAuth;

    ListView lecture_list;
    ListViewAdapter listview_adapter;
    String lecture_fullList;
    String full_percentage;
    int totalLectureNum;
    String[] lectureName_array;
    String[] percentage_array;
    int percentage_sum=0;
    String isDone;
    TextView is_done_text;

    Boolean[] lecture_checked = {true};

    String lecture_base_key = "lecture_subject";
    String assignment_base_key = "assignment_subject";



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_lecture() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_lecture.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_lecture newInstance(String param1, String param2) {
        fragment_lecture fragment = new fragment_lecture();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        SharedPreferences prefs2;
        prefs2 = PreferenceManager.getDefaultSharedPreferences(getContext());

        //로그인한 유저의 정보 가져오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userInfo = user.getUid();
        Log.e("fb uid in setting", userInfo);

        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://univp-1db5d-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("User").
                child(userInfo).child("lectureName");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lecture_fullList = snapshot.getValue(String.class);
                // 수강하는 과목 개수 가져오기
                lectureName_array = lecture_fullList.split("\n");
                totalLectureNum = Integer.parseInt(lectureName_array[0]);
                Log.e("hyo_total_lecture_num", totalLectureNum + "");

                lecture_checked = new Boolean[totalLectureNum+1];
//                for(int i=1; i<totalLectureNum+1;i++){
//                    lecture_checked[i] = true;
//                }

                prefs2.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
                    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
                        Log.d("tag","클릭된 Preference의 key는 "+key);
                        char idx =  key.charAt(key.length()-1);
                        int index = idx-'0';
                        Log.d("tag","key 의 마지막 자리 "+index);
                        boolean checked = sp.getBoolean(key, true);
                        Log.d("tag","bool Check "+ checked + " " );

                        lecture_checked[index] = checked;

                        for(int i=1; i<totalLectureNum+1;i++){
                            String lecture_key = lecture_base_key + i;
                            lecture_checked[i] = sp.getBoolean(lecture_key, true);
                            Log.d("tag_checked", lecture_checked[i] +" at " + i);
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        totalLectureNum = Integer.parseInt(lectureName_array[0]);
//        Log.e("Check totalLectureNum", totalLectureNum + "");

    }


    SharedPreferences.OnSharedPreferenceChangeListener prefListener1 = new SharedPreferences.OnSharedPreferenceChangeListener() {

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        }
    };

    //디데이 구하는 함수
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences prefs;
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());



        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_lecture, container, false);
        View listItem = inflater.inflate(R.layout.listview_item, container, false);

        is_done_text = (TextView) listItem.findViewById(R.id.lecture_isdone);
        /*--------------Custom ListView---------------*/
        //Adapter 생성
        listview_adapter = new ListViewAdapter();
        //리스트뷰 참조 및 adapter 달기
        lecture_list = (ListView) rootView.findViewById(R.id.listView_lecture);
        lecture_list.setAdapter(listview_adapter);

        ProgressBar myprogress_bar = (ProgressBar)listItem.findViewById(R.id.progressBar);

        /*--------------크롤링 데이터 DB에서 가져오기-------------------*/
        //로그인한 유저의 정보 가져오기
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


                for (int i = 1 ; i< totalLectureNum + 1 ; i++){
                    String getDfaultKey = lecture_base_key + i;
                    Log.d("tag", "getDfaultKey :  " +getDfaultKey );
                    lecture_checked[i] = prefs.getBoolean(getDfaultKey, true);
                    Log.d("tag", "getDfaultBool " +lecture_checked[i] );
                }

                int percentage_average;

                for(int i=1; i<totalLectureNum+1;i++) {
                    String lectureName = lectureName_array[i];
                    int count_i = i;
                    if (lecture_checked[i]) {
                        DatabaseReference percentageRef = database.getReference("User").child(userInfo).child(lectureName).child("percentage");
                        percentageRef.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NotNull DataSnapshot snapshot) {
                                full_percentage = snapshot.getValue(String.class);
                                Log.e("lecture Name", lectureName);

                                percentage_array = full_percentage.split("\n");
                                int percentage_num = Integer.parseInt(percentage_array[0]);
                                Log.e("percentage_num", percentage_num + "");

                                if (percentage_num != 0) {
                                    String lecture_deadline = percentage_array[1].substring(9, percentage_array[1].length());
                                    Log.e("lecture_deadline", lecture_deadline + "");

                                    String deadline_Date = lecture_deadline.substring(lecture_deadline.lastIndexOf("~") + 2, lecture_deadline.lastIndexOf("~") + 13);
                                    long d_day = Dday(deadline_Date);  //디데이 구하기
                                    Log.e("강의 수강 퍼센트", percentage_array[2] + "");
                                    //수강도 (퍼센트 구하기)
                                    for (int j = 0; j < percentage_num; j++) {
                                        percentage_sum += Integer.parseInt(percentage_array[2].substring(0, percentage_array[2].indexOf("%")));
                                        percentage_array[2] = percentage_array[2].substring(percentage_array[2].indexOf("%") + 2, percentage_array[2].length());
                                    }

                                    int percentage_average = percentage_sum / percentage_num;
                                    myprogress_bar.setIndeterminate(false);
                                    myprogress_bar.setProgress(percentage_average);

                                    Log.e("수강도", percentage_average + "%");

                                    if (percentage_average == 100) {
                                        isDone = "수강완료";
                                    } else {
                                        isDone = "미수강";
                                    }

                                    if (d_day >= 0)
                                        listview_adapter.addItem("D-" + d_day, lectureName, lecture_deadline, isDone, percentage_average, d_day);

                                    if (count_i == totalLectureNum)
                                        listview_adapter.sort_hashMap();
                                    percentage_sum = 0; // 다시 초기화
                                }
                                listview_adapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){}
        });

        return rootView;
    }
}
