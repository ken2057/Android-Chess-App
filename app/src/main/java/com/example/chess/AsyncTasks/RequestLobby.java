package com.example.chess.AsyncTasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.chess.BoardActivity;
import com.example.chess.MainActivity;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class RequestLobby extends AsyncTask<String, String, String> {

    Context context;
    String url_API;
    public RequestLobby(Context context, String url) {
        this.context = context.getApplicationContext();
        this.url_API = url;
    }

    @Override
    protected String doInBackground(String... value) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(url_API + "CheckPlayerJoinLobby/" + MainActivity.matchId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if(conn.getResponseCode() == HttpsURLConnection.HTTP_OK){
                // Do normal input or output stream reading
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while((line = reader.readLine()) != null){
                    result.append(line);
                }
            }
            else { MainActivity.response = "FAILED"; }
        } catch (IOException e) { }
        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.v("result", result);
        try {
            JSONObject temp = new JSONObject(result);
            if(temp.getString("2players").equals("true")) {
                MainActivity.thrCheckPlayerInLobby.interrupt();
                Log.v("Kill thread", "Done");
                Log.v("Start game", "...");
                Intent myIntent = new Intent(context, BoardActivity.class);
                Bundle b = new Bundle();
                b.putString("matchId", MainActivity.matchId);
                b.putString("androidId", MainActivity.androidId);
                b.putBoolean("isWhite", temp.getString("white").equals(MainActivity.androidId));
                myIntent.putExtras(b);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(myIntent);
            }
        }
        catch (Exception e) {
            Log.v("error RequestLobby ", e.getMessage()+"");
        }
    }
}
