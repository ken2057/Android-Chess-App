package com.example.chess.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.example.chess.BoardActivity;

import java.util.ArrayList;

public class GetPostMoveAble extends AsyncTask<View, ArrayList<int[]>, String> {

    @Override
    protected String doInBackground(View... uri) {
        BoardActivity.moveAble = BoardActivity.getMoveAble(uri[0]);
        for (int i = 0; i < BoardActivity.moveAble.size(); i++) {
            int[] temp = BoardActivity.moveAble.get(i);
            Log.v("moveAble", temp[0] + " " + temp[1]);
        }
        BoardActivity.addEffectMoveAble();

        return "ok";
    }

    @Override
    protected void onPostExecute(String r) {
        super.onPostExecute(r);
        BoardActivity.reDrawEffect();
    }
}