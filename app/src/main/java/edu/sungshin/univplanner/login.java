package edu.sungshin.univplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class login extends AppCompatActivity {
    Button button;
    EditText idEditText, pwEditText;
    String idText, pwText;

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
                    Log.e("receive", "Success");
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }

                else {
                    Log.e("receive", "failed");
                }
            }

            catch (Exception e) {
                Log.e("sck", "socket fail");
                e.printStackTrace();
            }
        }
    }
}