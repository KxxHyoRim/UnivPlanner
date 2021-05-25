package edu.sungshin.univplanner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

/**
 * Created by amagr on 2018-01-01.
 */

public class Setting extends PreferenceFragment {

    SharedPreferences prefs;

    //데이터베이스 값 가져오기
    private FirebaseDatabase myFirebaseDatabase;
    private DatabaseReference myDatabaseReference;
    private ChildEventListener myChildEventListener;
    private FirebaseAuth mAuth;

    ListView lecture_list;
    ListViewAdapter listview_adapter;
    String lecture_fullList;
    int totalLectureNum;
    String[] lectureName_array;

    CheckBoxPreference subjPreference;
    PreferenceScreen lectureListScreen;
    PreferenceScreen assignmentListScreen;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.setting);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(prefListener1);

        lectureListScreen = (PreferenceScreen)findPreference("optional_subject_lecture_in");

        subjPreference = (CheckBoxPreference)findPreference("lecture_subject1");
        String tmp = (String) subjPreference.getTitle();
        Log.e("preference_get", tmp + "");


        /*--------------크롤링 데이터 DB에서 가져오기-------------------*/

        //로그인한 유저의 정보 가져오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userInfo = user.getUid();
        Log.e("fb uid in setting", userInfo);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://univp-1db5d-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("User").child(userInfo).child("lectureName");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                lecture_fullList = snapshot.getValue(String.class);
                //Log.e("load letureName:", lecture_fullList + "");

                lectureName_array = lecture_fullList.split("\n");
                totalLectureNum = Integer.parseInt(lectureName_array[0]);
                Log.e("total_lecture_num", totalLectureNum + "");


                for(int i=1; i<totalLectureNum+1;i++){
                    String lectureName = lectureName_array[i];
                    Log.e("lecure_name_setting", lectureName + "");
                }

                subjPreference.setTitle(lectureName_array[1]);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });



    }// onCreate

    SharedPreferences.OnSharedPreferenceChangeListener prefListener1 = new SharedPreferences.OnSharedPreferenceChangeListener() {

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


        }
    };


}