package edu.sungshin.univplanner;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class navHeaderActivity extends AppCompatActivity {

    TextView nav_name;
    TextView nav_std_number;
    String name_from_firebase;
    String id_from_firebase;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_navheader);
        Log.e("nav_java_start", "nav_java_start");


        nav_name = (TextView) findViewById(R.id.nav_name);
        nav_std_number = (TextView) findViewById(R.id.nav_std_number);

        /** firebase 연동 */

        //로그인한 유저의 정보 가져오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String userInfo = user.getUid();
        Log.e("fb uid in setting", userInfo);

        Log.e("fb_check1", "fb_check1");

        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://univp-1db5d-default-rtdb.asia-southeast1.firebasedatabase.app/");

        Log.e("fb_check2", "fb_check2");


        // firebase에서 학생 이름 가져오기
        DatabaseReference myRef = database.getReference("User").
                child(userInfo).child("name");

        // navigation bar에 학생 이름 설정하기
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name_from_firebase =  snapshot.getValue(String.class);
                Log.e("nav_std_name", id_from_firebase + "");
                nav_name.setText(name_from_firebase);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {  }
        });

        // firebase에서 학생 학번(id) 가져오기
        DatabaseReference myRef2 = database.getReference("User").
                child(userInfo).child("id");

        // navigation bar에 학생 학번 설정하기
        myRef2.addValueEventListener(new ValueEventListener() {
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
}
