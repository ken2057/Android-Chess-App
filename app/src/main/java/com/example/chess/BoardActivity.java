package com.example.chess;

import android.content.ClipData;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class BoardActivity extends AppCompatActivity {
    ImageView[][] arrColorBoard = new ImageView[8][8];
    ImageView[][] pieces = new ImageView[8][8];
    private int[] posHover = new int[] {-1,-1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);

        createEmptyBoard();
        reDraw();
        test();
    }


    public void test() {


        for(int i = 1; i < 4; i++) {
            ImageView img = new ImageView(this);
            if(i == 1)
                img.setBackgroundResource(R.drawable.black_bishop);
            if(i == 2)
                img.setBackgroundResource(R.drawable.black_king);
            if(i == 3)
                img.setBackgroundResource(R.drawable.black_knight);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(105, 115);
            lp.setMargins(10, 2, 10, 2);
            img.setLayoutParams(lp);
            img.setClickable(true);

            img.setOnDragListener(new View.OnDragListener(){
                @Override
                public boolean onDrag(View v, DragEvent e) {
                    switch (e.getAction()) {
                        case DragEvent.ACTION_DROP:
                            ImageView imgView = (ImageView) e.getLocalState();

                            v.setVisibility(View.VISIBLE);
                            imgView.setVisibility(View.VISIBLE);

                            int[] posNew = getPosition(imgView);
                            int[] posOld = getPosition(v);


                            swap(posNew, posOld);
                            reDraw();
                            break;

                        default:
                            break;
                    }

                    return true;
                }
            });

            img.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent event){
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            int[] pos = getPosition(v);
                            Toast.makeText(BoardActivity.this, (char)(pos[0]+65)+""+(pos[1]+1), Toast.LENGTH_SHORT).show();

                            ClipData data = ClipData.newPlainText("", "");
                            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                            v.startDragAndDrop(data, shadowBuilder, v, 0);

//                            v.setVisibility(View.INVISIBLE);
                            break;


                        default:
                            break;
                    }

                    return true;
                }
            });

            pieces[0][i] = img;
        }
        reDraw();
    }

    public void createEmptyBoard(){
        GridLayout gridBoard = findViewById(R.id.gridBoard);
        GridLayout gridChess = findViewById(R.id.gridChess);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ImageView imgView = new ImageView(this);
                if ((i + j) % 2 == 0)
                    imgView.setBackgroundResource(R.drawable.rec_black);
                else
                    imgView.setBackgroundResource(R.drawable.rec_white);
                int size = 125;
                imgView.setMinimumHeight(size);
                imgView.setMinimumWidth(size);
                arrColorBoard[i][j] = imgView;

                ImageView img = new ImageView(this);
                img.setBackgroundResource(R.drawable.empty);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(125, 125);
                img.setLayoutParams(lp);
                img.setClickable(true);

                img.setOnDragListener(new View.OnDragListener(){

                    @Override
                    public boolean onDrag(View v, DragEvent e){
                        switch(e.getAction()) {
                            case DragEvent.ACTION_DROP:
                                ImageView imgView = (ImageView) e.getLocalState();

                                v.setVisibility(View.VISIBLE);
                                imgView.setVisibility(View.VISIBLE);

                                int[] posNew = getPosition(imgView);
                                int[] posOld = getPosition(v);

                                swap(posNew, posOld);
                                reDraw();
                                break;
                            default:
                                break;
                        }

                        return true;
                    }
                });

                pieces[i][j] = img;

                gridBoard.addView(imgView);
                gridChess.addView(img);
            }
        }
    }

    private void reDraw(){
        GridLayout gridBoard = findViewById(R.id.gridBoard);
        GridLayout gridChess = findViewById(R.id.gridChess);
        gridChess.removeAllViewsInLayout();
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(pieces[i][j].getParent() != null) {
                    ((GridLayout) pieces[i][j].getParent()).removeView(pieces[i][j]);
                }
                gridChess.addView(pieces[i][j]);
            }
        }
    }

    private int[] getPosition(View v){
        if(v == null)
            return new int[] {-1, -1};

        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(pieces[i][j] == v)
                    return new int[] {i, j};
            }
        }
        return new int[] {-1,-1};
    }

    private void swap(int[] currentPos, int[] newPos){
        ImageView temp = pieces[currentPos[0]][currentPos[1]];
        pieces[currentPos[0]][currentPos[1]] = pieces[newPos[0]][newPos[1]];
        pieces[newPos[0]][newPos[1]] = temp;
    }
}