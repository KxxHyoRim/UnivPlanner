package edu.sungshin.univplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class login extends AppCompatActivity {
    Button button;
    EditText idEditText, pwEditText;
    String idText, pwText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        button = (Button) findViewById(R.id.login_btn);
        idEditText = (EditText) findViewById(R.id.login_id);
        pwEditText = (EditText) findViewById(R.id.login_pw);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idText = idEditText.getText().toString();
                pwText = pwEditText.getText().toString();
                button.setText("로그인 정보 확인중");
                button.setEnabled(false);
                Log.e("btn", "click");
                ClientThread thread = new ClientThread();
                thread.start();
            }
        });
    }

    protected class ClientThread extends Thread {
        public void run() {
            String host = "ec2-13-209-76-12.ap-northeast-2.compute.amazonaws.com";
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
                //Log.e("send", pwText);

                String rev = in.readLine();
                Log.e("receive", rev);

                if (rev.equals("Success")) {
                    rev = in.readLine();
                    Log.e("receive", rev);
                    firebaseSignUp(rev);
                }

                else {
                    button.setText("로그인");
                    button.setEnabled(true);

                    login.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(login.this,
                                    "잘못된 로그인 정보입니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            catch (Exception e) {
                button.setText("로그인");
                button.setEnabled(true);

                login.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(login.this,
                                "잘못된 로그인 정보입니다.", Toast.LENGTH_SHORT).show();
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

                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://univp-1db5d-default-rtdb.asia-southeast1.firebasedatabase.app/");
                    DatabaseReference myRef = database.getReference("User").child(userInfo).child("id");
                    myRef.setValue(idText);

                    myRef = database.getReference("User").child(userInfo).child("name");
                    myRef.setValue(userName);
                    Toast.makeText(login.this,
                            userName + "님, 반갑습니다.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }

                else {
                    // If sign in fails, display a message to the user.
                    Log.e("firebase", "signupFail", task.getException());
                }
            }
        });
    }

    @Override public void onBackPressed() {
        //super.onBackPressed();
    }
}