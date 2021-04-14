package edu.sungshin.univplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpSuccessActivity extends AppCompatActivity {
    public String userName = "김성신";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 주석해제하면 상태바 없어짐(로그인화면)
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.sign_up_success);

        TextView congratTextView = (TextView)findViewById(R.id.SignUpSuccCongratText);
        Button startButton = (Button)findViewById(R.id.SignUpSuccStartButton);

        String congratStr = userName + "님,\n가입을 축하드립니다";
        congratTextView.setText(congratStr);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
