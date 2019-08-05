package com.example.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {
    String response = "";
    Thread checkPlayerInLobby;
    JSONObject json = new JSONObject();
    String matchId = "";

    boolean isConnected = false;
    boolean isJoining = false;
    String androidId;

    BroadcastReceiver testReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isConnected = isConnected(context);
//            if(isConnected) Toast.makeText(context, "Connected.", Toast.LENGTH_SHORT).show();
//            else Toast.makeText(context, "Lost connect.", Toast.LENGTH_SHORT).show();
        }

        public boolean isConnected(Context context){
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Button pop = findViewById(R.id.button_popup);
//        pop.setVisibility(View.INVISIBLE);
//        pop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (isConnected) {
//                    if(matchId.equals("") && !isJoining) {
//                        isJoining =true;
//                        new RequestJoin().execute(getString(R.string.url_API));
//                    }
//                    else{
//                        Toast.makeText(MainActivity.this, "Joined", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                else
//                    Toast.makeText(MainActivity.this, "No internet acceess", Toast.LENGTH_SHORT).show();
//            }
//        });

        Intent myIntent = new Intent(MainActivity.this, BoardActivity.class);
        Bundle b = new Bundle();
        b.putString("matchId", "abc");
        b.putString("androidId", androidId);
        b.putBoolean("isWhite", true);
        myIntent.putExtras(b);
        startActivity(myIntent);


//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        registerReceiver(testReceiver, filter);
//
//        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//        Log.v("id", androidId);
//
//        new RequestCheckRec().execute(getString(R.string.url_API));
    }

    class RequestJoin extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... uri) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(uri[0]+"JoinLobby/");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                JSONObject postData = new JSONObject();
                try {
                    postData.put("androidId", androidId);
                }catch (Exception e){}

                setPostRequestContent(conn, postData);

                if(conn.getResponseCode() == HttpsURLConnection.HTTP_OK){
                    // Do normal input or output stream reading
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while((line = reader.readLine()) != null){
                        try { json = new JSONObject(line); }
                        catch (JSONException e){ Log.v("CHESS JSON", "get json error", e); }
                        result.append(line);
                    }
                }
                else { response = "FAILED"; }
            } catch (IOException e) { }

            isJoining = false;
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {matchId = json.getString("matchId"); }
            catch (Exception e) {Log.e("error", e.getMessage()+""); }

            Log.v("json", json.toString());
            if(!json.toString().equals("")){

                Button btn = findViewById(R.id.buttonStart);
                btn.setText("Finding Player");
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkPlayerInLobby.interrupt();
                        new RequestCancelLobby().execute(getString(R.string.url_API));
                    }
                });
                createThreadCheckLobby();
            }
        }
    }

    class RequestLobby extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... uri) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(uri[0]+"CheckPlayerJoinLobby/"+matchId);
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
                else { response = "FAILED"; }
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
                    checkPlayerInLobby.interrupt();
                    Log.v("Kill thread", "Done");
                    Log.v("Start game", "...");
                    Intent myIntent = new Intent(MainActivity.this, BoardActivity.class);
                    Bundle b = new Bundle();
                    b.putString("matchId", matchId);
                    b.putString("androidId", androidId);
                    b.putBoolean("isWhite", temp.getString("white").equals(androidId));
                    myIntent.putExtras(b);
                    startActivity(myIntent);
                }
            }
            catch (Exception e) {
                Log.v("error", e.getMessage()+"");
            }
        }
    }

    class RequestCheckRec extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... uri) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(uri[0]+"CheckIsInLobby/"+androidId);
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
                else { response = "FAILED"; }
            } catch (IOException e) { }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.v("result", result);
            try {
                Button btn = findViewById(R.id.buttonStart);
                final JSONObject temp = new JSONObject(result);
                if(!temp.getString("result").equals("-1")) {
                    btn.setText("Reconnect");
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.v("game", "Reconnect");
                            try{ matchId = temp.getString("result");}
                            catch (Exception e) {Log.v("error 4", e.getMessage());}
                        }
                    });
                }else{
                    btn.setText("Play");
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(isConnected) {
                                new RequestJoin().execute(getString(R.string.url_API));
                            }
                            else
                                Toast.makeText(MainActivity.this, "No internet acceess", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            catch (Exception e) {
                Log.v("error 3", e.getMessage()+"");
            }
        }
    }

    class RequestCancelLobby extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... uri) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(uri[0]+"CancelLobby/");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                JSONObject postData = new JSONObject();
                try {
                    postData.put("androidId", androidId);
                    postData.put("matchId", matchId);
                }catch (Exception e){}

                setPostRequestContent(conn, postData);

                if(conn.getResponseCode() == HttpsURLConnection.HTTP_OK){
                    // Do normal input or output stream reading
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while((line = reader.readLine()) != null){
                        result.append(line);
                    }
                }
                else { response = "FAILED"; }
            } catch (IOException e) { }

            isJoining = false;
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(result.contains("Done")){
                matchId = "";
                Button btn = findViewById(R.id.buttonStart);
                btn.setText("Play");
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isConnected) {
                            new RequestJoin().execute(getString(R.string.url_API));
                        }
                        else
                            Toast.makeText(MainActivity.this, "No internet acceess", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void createThreadCheckLobby(){
        checkPlayerInLobby = new Thread(new Runnable() {
            @Override
            public void run() {
                int time = 1;
                try {
                    while (true) {
                        Log.v("thread check lobby", "checking " + time);
                        if(isConnected)
                            new RequestLobby().execute(getString(R.string.url_API));
                        Thread.sleep(2000);

                        time += 1;
                    }
                }
                catch (Exception e) {  }
            }
        });
        checkPlayerInLobby.start();
    }

    private void delReciver(){
        unregisterReceiver(testReceiver);
    }

    private void setPostRequestContent(HttpURLConnection conn, JSONObject jsonObject) throws IOException {
        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject.toString());
        Log.i(MainActivity.class.toString(), jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();
    }
}