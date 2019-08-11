package com.example.chess.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
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

public class RequestCheckRec extends AsyncTask<String, String, String>{

    Context context;
    Button btnStart;
    String url_API;
    public RequestCheckRec(Context context, Button btnStart, String url) {
        this.context = context.getApplicationContext();
        this.btnStart = btnStart;
        this.url_API = url;
    }

    @Override
    protected String doInBackground(String... value) {
        StringBuilder result = new StringBuilder();
        try {

            URL url = new URL(url_API + "CheckIsInLobby/" + MainActivity.androidId);
            Log.v("RequestCheckRec 2","2");
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
        } catch (IOException e) {
            Log.v("RequestCheckRec", e.getMessage()+"");
        }

        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.v("result", result);
        try {
            final JSONObject temp = new JSONObject(result);
            if(!temp.getString("result").equals("-1")) {
                btnStart.setText("Reconnect");
                btnStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.v("game", "Reconnect");
                        try{ MainActivity.matchId = temp.getString("result");}
                        catch (Exception e) {Log.v("error 4", e.getMessage());}
                    }
                });
            }else{
                btnStart.setText("Play");
                btnStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(MainActivity.isConnected) {
                            try {
                                new RequestJoin(context, btnStart, url_API).execute().get();
                            }
                            catch (Exception e){
                                //
                            }
                        }
                        else
                            Toast.makeText(context, "No internet acceess", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        catch (Exception e) {
            Log.v("error RequestCheckRec", e.getMessage()+"");
        }
    }
}