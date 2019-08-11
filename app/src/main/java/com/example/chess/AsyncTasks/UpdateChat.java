package com.example.chess.AsyncTasks;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.example.chess.BoardActivity;
import com.example.chess.ChatActivity;

import org.json.JSONArray;

public class UpdateChat extends AsyncTask<String, String, String> {

    JSONArray json;
    public UpdateChat(JSONArray json){
        this.json = json;
    }

    @Override
    protected String doInBackground(String... value){
        try {
            for (int i = 0; i < json.length(); i++) {

                Object j = json.get(i);
                String[] chat = j.toString()
                                    .replace("[\"","")
                                    .replace("\"]","")
                                .split("\",\"");

                BoardActivity.chatIdOrder.add(chat[0]);
                BoardActivity.chatMsg.add(chat[1]);
            }
        }
        catch (Exception e){
            Log.v("error update chat", e.getMessage()+"");
        }

        return "ok";
    }

    @Override
    protected void onPostExecute(String result) {
        try{
            ChatActivity.chatAdt.notifyDataSetChanged();
        }
        catch (Exception e){
            //
        }
    }
}
