package com.example.chess;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class InternetReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(isConnected(context)) Toast.makeText(context, "Connected.", Toast.LENGTH_LONG).show();
        else Toast.makeText(context, "Lost connect.", Toast.LENGTH_LONG).show();
    }

    public boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
