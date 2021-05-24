package edu.sungshin.univplanner;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_lecture#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_lecture extends Fragment {

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_lecture, container, false);

        /*--------------Custom ListView---------------*/
        //Adapter 생성
        listview_adapter = new ListViewAdapter();
        //리스트뷰 참조 및 adapter 달기
        lecture_list = (ListView) rootView.findViewById(R.id.listView_lecture);
        lecture_list.setAdapter(listview_adapter);

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

                            String lecture_deadline = percentage_array[1];

                            listview_adapter.addItem("D-4",lectureName,lecture_deadline,"미수강");
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