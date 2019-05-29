package com.giapnguyen086.chatwithme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://172.16.101.215:3000");
        } catch (URISyntaxException e) {}
    }
    ListView lvChat;
    ArrayList<String> ChatArr;
    ArrayAdapter<String> adapter;
    EditText txtChat;
    Button btnChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mSocket.on("server-gui-tinchat", onNewMessage_DsChat);
        mSocket.connect();

        addControlls();
        addEvents();
    }

    private void addControlls() {
        btnChat = findViewById(R.id.btnChat);
        txtChat = findViewById(R.id.txtChat);

        lvChat = findViewById(R.id.lvChat);
        ChatArr = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(ChatActivity.this,android.R.layout.simple_list_item_1,ChatArr);
        lvChat.setAdapter(adapter);
    }

    private void addEvents() {
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("client-gui-tinchat", txtChat.getText().toString());
                txtChat.setText("");
            }
        });
    }

    private Emitter.Listener onNewMessage_DsChat = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String noidung;
                    try {
                        noidung = data.getString("tinchat");
                        ChatArr.add(noidung);
                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };
}
