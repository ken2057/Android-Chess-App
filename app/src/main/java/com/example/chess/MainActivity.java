package com.example.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickBtn(View v){
        Toast.makeText(this, "Click on Button", Toast.LENGTH_LONG).show();

        Intent myIntent = new Intent(v.getContext(), BoardActivity.class);
        startActivityForResult(myIntent, 0);
    }

    private void createBoard(View v){


    }
}