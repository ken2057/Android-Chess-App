package com.example.chess.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.chess.Board;
import com.example.chess.BoardActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostSendCheckMoved extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... value) {
        JSONObject postData = new JSONObject();
        String data = "";
        try{
            postData.put("matchId", BoardActivity.matchId);
            postData.put("androidId", BoardActivity.androidId);
            postData.put("numMove", BoardActivity.numMove);
            postData.put("numChat", BoardActivity.numChat);

            URL u = new URL(BoardActivity.URL+"CheckIsMoved/");

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
            Log.v("error 6", e+"");
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            Log.v("Check moved and online", result);

            JSONObject j = new JSONObject(result);

            String lastMove = j.getString("result");
            JSONArray lastChat = j.getJSONArray("chat");
            int totalMove = j.getInt("totalMove");

            //check have new chat
            if(!lastChat.isNull(0)){
                BoardActivity.numChat += lastChat.length();
                new UpdateChat(lastChat).execute().get();
            }
            //check have new move
            if(!lastMove.equals("")) {
                BoardActivity.numMove = totalMove;
                new OpponentMove().execute(lastMove).get();
            }else{
                BoardActivity.isCheckMoved = false;
            }
        }catch (Exception e){
            Log.v("Error check moved", e.getMessage()+"");
            BoardActivity.isCheckMoved = false;
        }
    }
}