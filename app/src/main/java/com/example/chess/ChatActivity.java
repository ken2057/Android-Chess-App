package com.example.chess;

import android.content.Context;
import android.os.Bundle;

import com.example.chess.Adapters.ChatAdapter;
import com.example.chess.AsyncTasks.PostSendChat;
import com.example.chess.AsyncTasks.PostSendCheckMoved;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ChatActivity extends AppCompatActivity {
    ListView chatView;
    public static ChatAdapter chatAdt;
    Thread thrRefreshChat = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                while (true) {
                    Log.v("???", "thread refresh chat");
//                    resetChat();
                    chatAdt.notifyDataSetChanged();
                    Thread.sleep(2000);
                }
            }catch (Exception e){
                //
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        addBtnEvent();

        chatView = findViewById(R.id.chatList);

        chatView.setDivider(null);
        chatView.setDividerHeight(0);


        chatAdt = new ChatAdapter(this, BoardActivity.chatIdOrder, BoardActivity.chatMsg);
        chatView.setAdapter(chatAdt);

//        thrRefreshChat.start();

    }

    private void addBtnEvent(){
        Button btnSend = findViewById(R.id.btnChatSend);

        btnSend.setOnClickListener(view -> {
            EditText txt = findViewById(R.id.txtChat);
            if(!txt.getText().toString().equals("")){
                try {
                    String text = txt.getText().toString();
                    txt.setText("");

                    String msg = text.replace("\"","\'").replace("\n","");
                    new PostSendChat().execute(msg).get();
                    BoardActivity.chatIdOrder.add(BoardActivity.isWhite? "White": "Black");
                    BoardActivity.chatMsg.add(text);
                    BoardActivity.numChat += 1;
//                        resetChat();

                    chatAdt.notifyDataSetChanged();
                }
                catch (Exception e){
                    //
                }


            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
//        if(thrRefreshChat.isInterrupted())
//            thrRefreshChat.start();
    }

    @Override
    public void onPause(){
        super.onPause();
//        thrRefreshChat.interrupt();
    }
}
