package com.example.chess.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.chess.BoardActivity;
import com.example.chess.ChatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostSendChat extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... value) {
        JSONObject postData = new JSONObject();
        String data = "";
        try{
            postData.put("matchId", BoardActivity.matchId);
            postData.put("androidId", BoardActivity.androidId);
            postData.put("chatMsg", value[0]);

            URL u = new URL(BoardActivity.URL+"SendChat/");

            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("POST"); conn.setDoOutput(true); conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            SetPostRequestContent.set(conn, postData);

            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = reader.readLine()) != null){
                data = data.concat(line);
            }

        }catch (Exception e){
            Log.v("Error send chat 1", e+"");
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
