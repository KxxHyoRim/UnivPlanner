package edu.sungshin.univplanner;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

import java.util.GregorianCalendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_assignment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_assignment extends Fragment {

    //데이터베이스 값 가져오기
    private FirebaseDatabase myFirebaseDatabase;
    private DatabaseReference myDatabaseReference;
    private ChildEventListener myChildEventListener;
    private FirebaseAuth mAuth;

    ListView assignment_list;
    String lecture_fullList;
    ListViewAdapter_assignment listview_adapter;
    String full_assignment;
    int totalLectureNum;
    String[] lectureName_array;
    String[] assignment_array;
    TextView isDone_textView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_assignment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_assignmnet.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_assignment newInstance(String param1, String param2) {
        fragment_assignment fragment = new fragment_assignment();
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
    }
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_assignment, container, false);
        View listItem = inflater.inflate(R.layout.listview_item_assingment, container, false);

        /*--------------Custom ListView---------------*/
        //Adapter 생성
        listview_adapter = new ListViewAdapter_assignment();
        //리스트뷰 참조 및 adapter 달기
        assignment_list = (ListView) rootView.findViewById(R.id.listView_assignment);
        assignment_list.setAdapter(listview_adapter);

        isDone_textView = (TextView) listItem.findViewById(R.id.assignment_isdone);

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
                Log.e("과제 total_lecture_num", totalLectureNum + "");

                for(int i=1; i<totalLectureNum+1;i++){
                    String lectureName = lectureName_array[i];
                    int count_i=i;
                    DatabaseReference percentageRef = database.getReference("User").child(userInfo).child(lectureName).child("assignment");
                    percentageRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NotNull DataSnapshot snapshot){
                            full_assignment = snapshot.getValue(String.class);
                            Log.e("lecture Name", lectureName);

                            assignment_array = full_assignment.split("\n");
                            int assignment_num = Integer.parseInt(assignment_array[0]);
                            Log.e("assignment_num", assignment_num + "");

                            if (assignment_num!=0) {
                                String assignment_name = assignment_array[1];
                                Log.e("assignment_name", assignment_name + "");

                                String isDone_assignment = assignment_array[2];
                                Log.e("isDone_assignment", isDone_assignment + "");

                                String assignment_deadline = assignment_array[3];
                                String deadline_Date = assignment_deadline.substring(0,10);
                                long d_day = Dday(deadline_Date);  //디데이 구하기

                                if(d_day>=0)
                                    listview_adapter.addItem("D-" + d_day, lectureName, assignment_name, assignment_deadline, isDone_assignment, d_day);

                                if(count_i==totalLectureNum)
                                {
                                    listview_adapter.sort_hashMap();
                                }
                            }
                            //listview_adapter.sort_hashMap();
                            listview_adapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error){}
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){}
        });

        return rootView;
    }
}