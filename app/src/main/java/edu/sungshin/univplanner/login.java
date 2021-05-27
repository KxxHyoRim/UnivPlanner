package edu.sungshin.univplanner;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

public class login extends AppCompatActivity {
    Button button;
    EditText idEditText, pwEditText;
    String idText, pwText;
    CheckBox checkBoxID, checkBoxPW;
    boolean isIDcheckBoxChecked, isPWcheckBoxChecked;
    private FirebaseAuth mAuth;
    boolean isLoginSuccess;
    String lectureNameList;
    Vector<String> lectureNameVec = new Vector<String>();
    Vector<String> lecturePercentVec = new Vector<String>();
    Vector<String> lectureAssignmentVec = new Vector<String>();
    private long backKeyPressedTime = 0;
    private Toast toast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        button = (Button) findViewById(R.id.login_btn);
        idEditText = (EditText) findViewById(R.id.login_id);
        pwEditText = (EditText) findViewById(R.id.login_pw);
        checkBoxID = (CheckBox) findViewById(R.id.login_checkbox_id);
        checkBoxPW = (CheckBox) findViewById(R.id.login_checkbox_pw);

        SharedPreferences pref = getSharedPreferences("saveID",MODE_PRIVATE);
        String saveIDdata = pref.getString("id","");

        pref = getSharedPreferences("savePW",MODE_PRIVATE);
        String savePWdata = pref.getString("pw","");

        pref = getSharedPreferences("isIdSaved",MODE_PRIVATE);
        String isIdSaved = pref.getString("isSaved","");

        pref = getSharedPreferences("isPWSaved",MODE_PRIVATE);
        String isPWSaved = pref.getString("isSaved","");

        checkBoxID.setChecked(false);
        checkBoxPW.setChecked(false);

        isIDcheckBoxChecked = false;
        isPWcheckBoxChecked = false;

        if (isIdSaved.equals("true")) {
            idEditText.setText(saveIDdata);
            checkBoxID.setChecked(true);
            isIDcheckBoxChecked = true;
        }

        if (isPWSaved.equals("true")) {
            pwEditText.setText(savePWdata);
            checkBoxPW.setChecked(true);
            isPWcheckBoxChecked = true;
        }

        checkBoxID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isIDcheckBoxChecked = true;
                }

                else {
                    if (isPWcheckBoxChecked) {
                        Log.e("check", "id x pw o");
                        Toast.makeText(login.this,
                                "비밀번호 저장을 먼저 해제해주세요", Toast.LENGTH_SHORT).show();
                        isIDcheckBoxChecked = true;
                        checkBoxID.setChecked(true);
                    }

                    else {
                        isIDcheckBoxChecked = false;
                    }
                }
            }
        });

        checkBoxPW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!isIDcheckBoxChecked) {
                        Log.e("check", "id x pw o");
                        Toast.makeText(login.this,
                                "아이디 저장을 먼저 선택해주세요", Toast.LENGTH_SHORT).show();
                        isPWcheckBoxChecked = false;
                        checkBoxPW.setChecked(false);
                    }

                    else {
                        isPWcheckBoxChecked = true;
                    }
                }

                else {
                    isPWcheckBoxChecked = false;
                }
            }
        });

        if (!isLoginSuccess) {
            button.setText("로그인");
            button.setEnabled(true);
            idEditText.setEnabled(true);
            pwEditText.setEnabled(true);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoginSuccess) {
                    idText = idEditText.getText().toString();
                    pwText = pwEditText.getText().toString();

                    SharedPreferences prefID = getSharedPreferences("saveID", MODE_PRIVATE);
                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editorID = prefID.edit();
                    editorID.putString("id", idText);
                    editorID.apply();

                    SharedPreferences prefPW = getSharedPreferences("savePW", MODE_PRIVATE);
                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editorPW = prefPW.edit();
                    editorPW.putString("pw", pwText);
                    editorPW.apply();

                    SharedPreferences prefIsIDSaved = getSharedPreferences("isIdSaved", MODE_PRIVATE);
                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editorIsID = prefIsIDSaved.edit();
                    editorIsID.putString("isSaved", Boolean.toString(isIDcheckBoxChecked));
                    editorIsID.apply();

                    SharedPreferences prefIsPWSaved = getSharedPreferences("isPWSaved", MODE_PRIVATE);
                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editorIsPW = prefIsPWSaved.edit();
                    editorIsPW.putString("isSaved", Boolean.toString(isPWcheckBoxChecked));
                    editorIsPW.apply();

                    checkBoxID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isIDcheckBoxChecked) {
                                checkBoxID.setChecked(true);
                            }

                            else {
                                checkBoxID.setChecked(false);
                            }
                        }
                    });

                    checkBoxPW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isPWcheckBoxChecked) {
                                checkBoxPW.setChecked(true);
                            }

                            else {
                                checkBoxPW.setChecked(false);
                            }
                        }
                    });

                    idEditText.setEnabled(false);
                    pwEditText.setEnabled(false);
                    button.setText("로그인 정보 확인중 ..");
                    button.setEnabled(false);
                    Log.e("btn", "click");
                    ClientThread thread = new ClientThread();
                    thread.start();
                }
            }
        });
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

                out.println(idText);
                Log.e("send", idText);

                out.println(pwText);
                Log.e("send", pwText);

                String rev = in.readLine();
                Log.e("receive", rev);

                if (rev.equals("Success")) {
                    login.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(login.this,
                                    "로그인 성공", Toast.LENGTH_SHORT).show();
                        }
                    });

                    button.setText("LMS 동기화중 ..");
                    isLoginSuccess = true;
                    lectureNameList = "";

                    String userName = in.readLine();	//outer Lecture number

                    String totalLectureNumStr = in.readLine();	//outer Lecture number
                    Log.e("total Lecture Num", totalLectureNumStr);
                    int totalLectureNum = Integer.parseInt(totalLectureNumStr);

                    String realTotalLectureNumStr = in.readLine();	//outer Lecture number
                    Log.e("real total Lecture Num", realTotalLectureNumStr);
                    int realTotalLectureNum = Integer.parseInt(realTotalLectureNumStr);

                    for (int i = 0; i < realTotalLectureNum; i++) {
                        String lectureTitle = in.readLine();	// outer lecture title
                        button.setText("LMS 동기화 "
                                + Integer.toString(i) + " / " + realTotalLectureNumStr);


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

                else {
                    isLoginSuccess = false;
                    button.setText("로그인");
                    button.setEnabled(true);

                    login.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(login.this,
                                    "잘못된 로그인 정보입니다.", Toast.LENGTH_SHORT).show();

                            button.setText("로그인");
                            button.setEnabled(true);
                            idEditText.setEnabled(true);
                            pwEditText.setEnabled(true);
                        }
                    });
                }
            }

            catch (Exception e) {
                isLoginSuccess = false;
                //button.setText("로그인");
                //button.setEnabled(true);

                login.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(login.this,
                                "잘못된 로그인 정보입니다.", Toast.LENGTH_SHORT).show();

                        button.setText("로그인");
                        button.setEnabled(true);
                        idEditText.setEnabled(true);
                        pwEditText.setEnabled(true);
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
                    DatabaseReference myRef = database.getReference("User").child(userInfo).child("id");
                    myRef.setValue(idText);

                    myRef = database.getReference("User").child(userInfo).child("name");
                    myRef.setValue(userName);

                    myRef = database.getReference("User").child(userInfo).child("lectureName");
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

                    Toast.makeText(login.this,
                            userName + "님, 반갑습니다.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("crawlingText", "Have to Get Firebase DB");
                    startActivity(intent);
                    finish();
                }

                else {
                    // If sign in fails, display a message to the user.
                    Log.e("firebase", "signupFail", task.getException());
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finish();
            toast.cancel();

            moveTaskToBack(true);
            finishAndRemoveTask();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}