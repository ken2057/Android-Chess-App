package com.example.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;

import com.example.chess.AsyncTasks.RequestCancelLobby;
import com.example.chess.AsyncTasks.RequestCheckRec;
import com.example.chess.AsyncTasks.RequestLobby;
import com.example.chess.Data.DbHelper;
import com.example.chess.Threads.ThrCheckLobby;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    public static String response = "";
    public static Thread thrCheckPlayerInLobby;
    public static JSONObject json = new JSONObject();
    public static String matchId = "";
    DbHelper db;

    public static boolean isConnected = false;
    public static boolean isJoining = false;
    public static String androidId;

    BroadcastReceiver networkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DbHelper(this);
        networkReceiver = new NetworkChangeReceiver();
        thrCheckPlayerInLobby = new ThrCheckLobby(this, getString(R.string.url_API));

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkReceiver, filter);

        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.v("id", androidId);

        try {
            new RequestCheckRec(this, (Button) findViewById(R.id.buttonStart), getString(R.string.url_API)).execute().get();
        }
        catch (Exception e){
            //
        }
    }

    private void delReceiver(){
        unregisterReceiver(networkReceiver);
        if(thrCheckPlayerInLobby.isInterrupted()) {

            if(!matchId.equals(""))
                new RequestCancelLobby(this, (Button) findViewById(R.id.buttonStart), getString(R.string.url_API)).execute();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
//        delReceiver();
    }

    @Override
    public void onPause(){
        super.onPause();
//        delReceiver();
//        unregisterReceiver(networkReceiver);
    }

}