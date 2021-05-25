package edu.sungshin.univplanner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.BaseAdapter;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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







    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.setting);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(prefListener1);

    }// onCreate

    SharedPreferences.OnSharedPreferenceChangeListener prefListener1 = new SharedPreferences.OnSharedPreferenceChangeListener() {

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


        }
    };


}