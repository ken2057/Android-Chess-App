package com.example.chess.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.chess.R;

import org.json.JSONObject;

import java.util.ArrayList;

public class ChatAdapter extends BaseAdapter {
    Context context;
    JSONObject json;
    LayoutInflater inflter;
    ArrayList<String> chatId = new ArrayList<String>();
    ArrayList<String> chatMsg = new ArrayList<String>();

    public ChatAdapter(Context context){
        this.context = context;
        inflter = (LayoutInflater.from(context));
    }
    public ChatAdapter(Context context, ArrayList<String> chatId, ArrayList<String> chatMsg){
        this.context = context;
        inflter = (LayoutInflater.from(context));
        this.chatId = chatId;
        this.chatMsg = chatMsg;
    }

    @Override
    public int getCount() {
        return chatId.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflter.inflate(R.layout.content_chat, null);
        TextView chatId = view.findViewById(R.id.chatId);
        TextView content = view.findViewById(R.id.chatContent);

        chatId.setText(this.chatId.get(i).equals("")? this.chatId.get(i): this.chatId.get(i)+":");
        content.setText(this.chatMsg.get(i));

        view.setClickable(false);
        view.setOnClickListener(null);

        return view;
    }
}
