package com.example.chess.Threads;

import android.util.Log;

import com.example.chess.AsyncTasks.PostSendCheckMoved;
import com.example.chess.BoardActivity;

public class ThrCheckMove extends Thread {
    @Override
    public void run() {
        try {
            while (true) {
                if(!BoardActivity.isCheckMoved) {
                    BoardActivity.isCheckMoved = true;
                    new PostSendCheckMoved().execute().get();
                }
                Thread.sleep(2000);
            }
        }catch (Exception e){
            Log.v("Thread check moved", e.getMessage()+"");
        }
    }
}
