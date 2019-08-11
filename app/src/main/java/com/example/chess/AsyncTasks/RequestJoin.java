package com.example.chess.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.chess.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class RequestJoin extends AsyncTask<String, String, String> {

    Context context;
    Button btnStart;
    String url_API;
    public RequestJoin(Context context, Button btnStart, String url) {
        this.context = context.getApplicationContext();
        this.btnStart = btnStart;
        this.url_API = url;
    }

    @Override
    protected String doInBackground(String... value) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(url_API + "JoinLobby/");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            JSONObject postData = new JSONObject();
            try {
                postData.put("androidId", MainActivity.androidId);
            }catch (Exception e){
                //
            }

            SetPostRequestContent.set(conn, postData);

            if(conn.getResponseCode() == HttpsURLConnection.HTTP_OK){
                // Do normal input or output stream reading
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while((line = reader.readLine()) != null){
                    try { MainActivity.json = new JSONObject(line); }
                    catch (JSONException e){ Log.v("CHESS JSON", "get json error", e); }
                    result.append(line);
                }
            }
            else { MainActivity.response = "FAILED"; }
        } catch (IOException e) {
            Log.v("RequestJoin", e.getMessage()+"");
        }

        MainActivity.isJoining = false;
        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        try {MainActivity.matchId = MainActivity.json.getString("matchId"); }
        catch (Exception e) {Log.e("error", e.getMessage()+""); }

        Log.v("json", MainActivity.json.toString());
        if(!MainActivity.json.toString().equals("")){

            btnStart.setText("Finding Player");
            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.thrCheckPlayerInLobby.interrupt();
                    try {
                        new RequestCancelLobby(context, btnStart, url_API).execute().get();
                    }catch (Exception e){
                        //
                    }
                }
            });
            MainActivity.thrCheckPlayerInLobby.start();
        }
    }
}
