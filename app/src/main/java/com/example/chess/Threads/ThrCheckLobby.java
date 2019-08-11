package com.example.chess.Threads;

import android.content.Context;
import android.util.Log;

import com.example.chess.AsyncTasks.RequestLobby;
import com.example.chess.MainActivity;

public class ThrCheckLobby extends Thread {

    Context context;
    String url_API;
    public ThrCheckLobby(Context context, String url_API){
        this.context = context;
        this.url_API = url_API;
    }

    @Override
    public void run() {
        int time = 1;
        try {
            while (true) {
                Log.v("thread check lobby 1", "checking " + time);
                Log.v("thread check lobby 2", "" + MainActivity.json.toString());
                if(MainActivity.isConnected)
                    new RequestLobby(context, url_API).execute().get();
                Thread.sleep(2000);

                time += 1;
            }
        }
        catch (Exception e) {
            //
        }
    }
}
