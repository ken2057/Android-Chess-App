package com.example.chess.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.chess.BoardActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostSendMove extends AsyncTask<String, String, String> {


    @Override
    protected String doInBackground(String... uri) {
        JSONObject postData = new JSONObject();
        String data = "";
        try {
            postData.put("matchId", BoardActivity.matchId);
            postData.put("androidId", BoardActivity.androidId);
            postData.put("oldPos", BoardActivity.spAct.lastPiecePos[0] + " " + BoardActivity.spAct.lastPiecePos[1]);
            postData.put("newPos", BoardActivity.spAct.currentLastMovePiecePos[0] + " " + BoardActivity.spAct.currentLastMovePiecePos[1]);
            postData.put("killPos", BoardActivity.spAct.getLastKillPos()[0] + " " + BoardActivity.spAct.getLastKillPos()[1]);
            postData.put("castlingNewPos", BoardActivity.spAct.castlingNewPos[0] + " " + BoardActivity.spAct.castlingNewPos[1]);
            postData.put("castlingOldPos", BoardActivity.spAct.castlingOldPos[0] + " " + BoardActivity.spAct.castlingOldPos[1]);
            postData.put("pawnEvolveTo", BoardActivity.pawnEvolveTo.charAt(0)+"");
            postData.put("isWin", BoardActivity.isWin);

            URL u = new URL(BoardActivity.URL + "SendMove/");

            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            SetPostRequestContent.set(conn, postData);

            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                data = data.concat(line);
            }

        } catch (Exception e) {
            Log.v("error 6", e + "");
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.v("Send move", result);
    }
}