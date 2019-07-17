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

import java.util.ArrayList;
import java.util.Arrays;


public class BoardActivity extends AppCompatActivity {
    ImageView[][] arrColorBoard = new ImageView[8][8];
    ImageView[][] pieces = new ImageView[8][8];

    ArrayList<ImageView> blackKill = new ArrayList<>();
    ArrayList<ImageView> whiteKill = new ArrayList<>();
    ArrayList<int[]> moveAble = new ArrayList<>();

    String lastPieceMove = "";
    int[] lastPiecePos = new int[]{-1, -1};
    int[] currentLastMovePiecePos = new int[]{-1, -1};
    int[] totQuaDuongAt = new int[]{-1, -1};
    //true mean player = white
    Boolean player = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);

        createEmptyBoard();
        reDraw();
        setupBoard();
    }

    private ImageView addCommonEvent(ImageView img) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(105, 121);

        lp.setMargins(10, 2, 10, 2);

        img.setLayoutParams(lp);
        img.setClickable(true);

        img.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent e) {
                if (e.getAction() == DragEvent.ACTION_DROP) {
                    ImageView imgView = (ImageView) e.getLocalState();

                    v.setVisibility(View.VISIBLE);
                    imgView.setVisibility(View.VISIBLE);

                    int[] posNew = getPosition(v);
                    int[] posOld = getPosition(imgView);
                    boolean flag = false;
                    for (int i = 0; i < moveAble.size(); i++) {
                        if (Arrays.equals(posNew, moveAble.get(i))) {
                            flag = true;
                            break;
                        }
                    }
                    Log.v("DROP IN PIECE", posNew[0] + " " + posNew[1]);
                    if (flag) {
                        if (pieces[posNew[0]][posNew[1]].getTag().toString().contains("black"))
                            whiteKill.add(pieces[posNew[0]][posNew[1]]);
                        else
                            blackKill.add(pieces[posNew[0]][posNew[1]]);

                        lastPieceMove = pieces[posOld[0]][posOld[1]].getTag().toString();
                        Log.v("LAST PIECE KILL", lastPieceMove);

                        lastPiecePos = posOld;
                        currentLastMovePiecePos = posNew;

                        pieces[posNew[0]][posNew[1]] = createEmptyView();
                        swap(posNew, posOld);
                        reDraw();
                    }
                }

                return true;
            }
        });

        img.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int[] pos = getPosition(v);
                    Toast.makeText(BoardActivity.this, (char) (pos[0] + 65) + "" + (pos[1] + 1) + " " + v.getTag(), Toast.LENGTH_SHORT).show();

                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                    v.startDragAndDrop(data, shadowBuilder, v, 0);

                    checkMoveAble(v);
                    for (int i = 0; i < moveAble.size(); i++) {
                        int[] temp = moveAble.get(i);
                        Log.v("moveAble", temp[0] + " " + temp[1]);
                    }
                }

                return true;
            }
        });
        return img;
    }

    public void setupBoard() {
        int[] blackOrder = new int[]{R.drawable.black_rook, R.drawable.black_knight, R.drawable.black_bishop, R.drawable.black_queen, R.drawable.black_king,
                R.drawable.black_bishop, R.drawable.black_knight, R.drawable.black_rook};
        int[] whiteOrder = new int[]{R.drawable.white_rook, R.drawable.white_knight, R.drawable.white_bishop, R.drawable.white_queen, R.drawable.white_king,
                R.drawable.white_bishop, R.drawable.white_knight, R.drawable.white_rook};
        String[] nameOrder = new String[]{"root", "knight", "bishop", "queen", "king", "bishop", "knight", "rook"};
        for (int i = 0; i < 8; i++) {
            ImageView img = new ImageView(this);
            img.setBackgroundResource(blackOrder[i]);
            img.setTag(new Piece(nameOrder[i] + " black"));
            img = addCommonEvent(img);
            pieces[0][i] = img;

            ImageView imgPawn = new ImageView(this);
            imgPawn.setBackgroundResource(R.drawable.black_pawn);
            imgPawn.setTag(new Piece("pawn black"));
            imgPawn = addCommonEvent(imgPawn);
            pieces[1][i] = imgPawn;
        }

        for (int i = 0; i < 8; i++) {
            ImageView img = new ImageView(this);
            img.setBackgroundResource(whiteOrder[i]);
            img.setTag(new Piece(nameOrder[i] + " white"));
            img = addCommonEvent(img);
            pieces[7][i] = img;

            ImageView imgPawn = new ImageView(this);
            imgPawn.setBackgroundResource(R.drawable.white_pawn);
            imgPawn.setTag(new Piece("pawn white"));
            imgPawn = addCommonEvent(imgPawn);
            pieces[6][i] = imgPawn;
        }

        reDraw();
    }

    private ImageView createEmptyView() {
        ImageView img = new ImageView(this);
        img.setBackgroundResource(R.drawable.empty);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(125, 125);
        img.setLayoutParams(lp);

        img.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent e) {
                if (e.getAction() == DragEvent.ACTION_DROP) {
                    ImageView imgView = (ImageView) e.getLocalState();

                    int[] posNew = getPosition(v);
                    int[] posOld = getPosition(imgView);
                    boolean flag = false;
                    for (int i = 0; i < moveAble.size(); i++) {
                        if (Arrays.equals(posNew, moveAble.get(i))) {
                            flag = true;
                            break;
                        }
                    }
                    Log.v("DROP EMPTY", posNew[0] + " " + posNew[1]);
                    if (flag) {
                        if(Arrays.equals(totQuaDuongAt, posNew)){

                            if (pieces[currentLastMovePiecePos[0]][currentLastMovePiecePos[1]].getTag().toString().contains("black"))
                                whiteKill.add(pieces[currentLastMovePiecePos[0]][currentLastMovePiecePos[1]]);
                            else
                                blackKill.add(pieces[currentLastMovePiecePos[0]][currentLastMovePiecePos[1]]);
                            pieces[currentLastMovePiecePos[0]][currentLastMovePiecePos[1]] = createEmptyView();

                            totQuaDuongAt = new int[]{-1, -1};
                        }

                        lastPieceMove = pieces[posOld[0]][posOld[1]].getTag().toString();
                        Log.v("LAST PIECE MOVED", lastPieceMove);
                        lastPiecePos = posOld;
                        currentLastMovePiecePos = posNew;

                        swap(posNew, posOld);
                        reDraw();
                    }
                }

                return true;
            }
        });

        return img;
    }

    public void createEmptyBoard() {
        GridLayout gridBoard = findViewById(R.id.gridBoard);
        GridLayout gridChess = findViewById(R.id.gridChess);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ImageView imgView = new ImageView(this);
                if ((i + j) % 2 == 0)
                    imgView.setBackgroundResource(R.drawable.rec_white);
                else
                    imgView.setBackgroundResource(R.drawable.rec_black);

                int size = 125;
                imgView.setMinimumHeight(size);
                imgView.setMinimumWidth(size);
                arrColorBoard[i][j] = imgView;

                ImageView img = createEmptyView();
                pieces[i][j] = img;

                gridBoard.addView(imgView);
                gridChess.addView(img);
            }
        }
    }

    private void reDraw() {
        GridLayout gridBoard = findViewById(R.id.gridBoard);
        GridLayout gridChess = findViewById(R.id.gridChess);
        gridChess.removeAllViewsInLayout();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (pieces[i][j].getParent() != null) {
                    ((GridLayout) pieces[i][j].getParent()).removeView(pieces[i][j]);
                }
                gridChess.addView(pieces[i][j]);
            }
        }
    }

    private int[] getPosition(View v) {
        if (v == null)
            return new int[]{-1, -1};

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (pieces[i][j] == v)
                    return new int[]{i, j};
            }
        }
        return new int[]{-1, -1};
    }

    private void swap(int[] currentPos, int[] newPos) {
        ImageView temp = pieces[currentPos[0]][currentPos[1]];
        pieces[currentPos[0]][currentPos[1]] = pieces[newPos[0]][newPos[1]];
        pieces[newPos[0]][newPos[1]] = temp;
    }

    private void checkMoveAble(View v) {
        moveAble = new ArrayList<>();
        int[] pos = getPosition(v);
        int move = v.getTag().toString().contains("white") ? 1 : -1;

        if (v.getTag().toString().contains("pawn")) {

            //move 2
            if ((pos[0] == 6 && move == 1) || (pos[0] == 1 && move == -1)) {
                if (checkPosEmpty(new int[]{pos[0] - move * 2, pos[1]}))
                    moveAble.add(new int[]{pos[0] - move * 2, pos[1]});
            }
            //move 1
            if (checkPosEmpty(new int[]{pos[0] - move, pos[1]}))
                moveAble.add(new int[]{pos[0] - move, pos[1]});
            //kill left
//            if (pos[1] - move >= 0 && pieces[pos[0] - move][pos[1] - move] != null)
            if (pos[1] - move >= 0 && !checkPosEmpty(new int[]{pos[0] - move, pos[1] - move}))
                moveAble.add(new int[]{pos[0] - move, pos[1] - move});
            //kill right
//            if (pos[1] + move < 8 && pieces[pos[0] - move][pos[1] + move] != null)
            if (pos[1] + move < 8 && !checkPosEmpty(new int[]{pos[0] - move, pos[1] + move}))
                moveAble.add(new int[]{pos[0] - move, pos[1] + move});
            //bắt chốt qua đường
            boolean first = Math.abs(lastPiecePos[1] - pos[1]) == 1;
            boolean second = false;
            if(lastPieceMove != "")
                second = lastPieceMove.contains("pawn");
            boolean last = Math.abs(lastPiecePos[0] - currentLastMovePiecePos[0]) == 2;

            if (first && second && last) {
                if (lastPiecePos[0] - pos[0] == -move) {
                    moveAble.add(new int[]{pos[0] - move, pos[1] - move});
                    totQuaDuongAt = new int[]{pos[0] - move, pos[1] - move};
                } else {
                    moveAble.add(new int[]{pos[0] - move, pos[1] + move});
                    totQuaDuongAt = new int[]{pos[0] - move, pos[1] + move};
                }
            }
        }
    }
    private boolean checkPosEmpty(int[] pos){
        if(Math.min(pos[0], pos[1]) >= 0 && Math.max(pos[0], pos[1]) < 8)
            if(pieces[pos[0]][pos[1]].getTag() == null)
                return true;
        return false;
    }
}

