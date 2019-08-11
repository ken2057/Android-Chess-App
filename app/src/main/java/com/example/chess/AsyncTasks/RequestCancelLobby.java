package com.example.chess.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class RequestCancelLobby extends AsyncTask<String, String, String>{

    Context context;
    Button btnStart;
    String url_API;
    public RequestCancelLobby(Context context, Button btnStart, String url) {
        this.context = context.getApplicationContext();
        this.btnStart = btnStart;
        this.url_API = url;
    }

    @Override
    protected String doInBackground(String... value) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(url_API + "CancelLobby/");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            JSONObject postData = new JSONObject();
            try {
                postData.put("androidId", MainActivity.androidId);
                postData.put("matchId", MainActivity.matchId);
            }catch (Exception e){}

            SetPostRequestContent.set(conn, postData);

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

        MainActivity.isJoining = false;
        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if(result.contains("Done")){
            MainActivity.matchId = "";
            btnStart.setText("Play");
            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(MainActivity.isConnected) {
                        try {
                            new RequestJoin(context, btnStart, url_API).execute().get();
                        }catch (Exception e){
                            //
                        }
                    }
                    else
                        Toast.makeText(context, "No internet acceess", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
