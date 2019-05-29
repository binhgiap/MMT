package com.giapnguyen086.chatwithme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://172.16.101.215:3000");
        } catch (URISyntaxException e) {}
    }

    private EditText txtName;
    private Button btnDangKi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSocket.on("ketquadkuser", onNewMessage_DKUser);
        mSocket.connect();

        addControlls();
        addEvents();

    }

    private void addEvents() {
        btnDangKi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("client-gui-username", txtName.getText().toString());
            }
        });
    }

    private void addControlls() {
        btnDangKi = findViewById(R.id.btnDangKi);
        txtName = findViewById(R.id.txtName);
    }

    private Emitter.Listener onNewMessage_DKUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String noidung;
                    try {
                        noidung = data.getString("noidung");
                        if(noidung == "true"){
                            Toast.makeText(MainActivity.this, "Đăng kí thành công",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this,ChatActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Tên tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };
}
