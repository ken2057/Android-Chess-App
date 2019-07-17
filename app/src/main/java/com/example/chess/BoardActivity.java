package com.example.chess;

import android.content.ClipData;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;


public class BoardActivity extends AppCompatActivity {
    ImageView[][] arrColorBoard = new ImageView[8][8];
    ImageView[][] pieces = new ImageView[8][8];

    ArrayList<ImageView> blackAlive = new ArrayList<>();
    ArrayList<ImageView> whiteAlive = new ArrayList<>();
    ArrayList<ImageView> blackKill = new ArrayList<>();
    ArrayList<ImageView> whiteKill = new ArrayList<>();
    ArrayList<int[]> moveAble = new ArrayList<>();

    String lastPieceMove = "";
    int[] lastPiecePos = new int[]{-1, -1};
    int[] currentLastMovePiecePos = new int[]{-1, -1};
    int[] totQuaDuongAt = new int[]{-1, -1};

    boolean isKingCheckMove = false;
    //Toast.makeText(BoardActivity.this, (char) (pos[0] + 65) + "" + (pos[1] + 1) + " " + v.getTag(), Toast.LENGTH_SHORT).show();

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

//                    int[] posNew = getPositionArr(v);
//                    int[] posOld = getPositionArr(imgView);
                    int[] posNew = ((Piece)v.getTag()).getPos();
                    int[] posOld = ((Piece)imgView.getTag()).getPos();
                    boolean flag = false;
                    for (int i = 0; i < moveAble.size(); i++) {
                        if (Arrays.equals(posNew, moveAble.get(i))) {
                            flag = true;
                            break;
                        }
                    }
                    Log.v("DROP IN BLOCK", posNew[0] + " " + posNew[1]);
                    if (flag) {
                        if (pieces[posNew[0]][posNew[1]].getTag().toString().contains("black")) {
                            whiteKill.add(pieces[posNew[0]][posNew[1]]);
                            blackAlive.remove(pieces[posNew[0]][posNew[1]]);
                        }
                        else {
                            blackKill.add(pieces[posNew[0]][posNew[1]]);
                            whiteAlive.remove(pieces[posNew[0]][posNew[1]]);
                        }

                        lastPieceMove = pieces[posOld[0]][posOld[1]].getTag().toString();
                        Log.v("LAST PIECE KILL", lastPieceMove);

                        lastPiecePos = posOld;
                        currentLastMovePiecePos = posNew;

                        ImageView emptyView = createEmptyView();
                        emptyView.setTag(new Piece("null", posNew));
                        pieces[posNew[0]][posNew[1]] = emptyView;
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
                    int[] pos = getPositionArr(v);

                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                    v.startDragAndDrop(data, shadowBuilder, v, 0);

                    moveAble = checkMoveAble(v);
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

                    int[] posNew = getPositionArr(v);
                    int[] posOld = ((Piece)imgView.getTag()).getPos();
                    boolean flag = false;
                    for (int i = 0; i < moveAble.size(); i++) {
                        if (Arrays.equals(posNew, moveAble.get(i))) {
                            flag = true;
                            break;
                        }
                    }
                    Log.v("DROP EMPTY", posNew[0] + " " + posNew[1]);
                    if (flag) {
                        //an tot qua duong
                        if (Arrays.equals(totQuaDuongAt, posNew)) {
                            if (pieces[currentLastMovePiecePos[0]][currentLastMovePiecePos[1]].getTag().toString().contains("black")) {
                                whiteKill.add(pieces[currentLastMovePiecePos[0]][currentLastMovePiecePos[1]]);
                                blackAlive.remove(pieces[currentLastMovePiecePos[0]][currentLastMovePiecePos[1]]);
                            }
                            else {
                                blackKill.add(pieces[currentLastMovePiecePos[0]][currentLastMovePiecePos[1]]);
                                whiteAlive.remove(pieces[currentLastMovePiecePos[0]][currentLastMovePiecePos[1]]);
                            }
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

    private void setupBoard() {
        int[] blackOrder = new int[]{R.drawable.black_rook, R.drawable.black_knight, R.drawable.black_bishop, R.drawable.black_queen, R.drawable.black_king,
                R.drawable.black_bishop, R.drawable.black_knight, R.drawable.black_rook};
        int[] whiteOrder = new int[]{R.drawable.white_rook, R.drawable.white_knight, R.drawable.white_bishop, R.drawable.white_queen, R.drawable.white_king,
                R.drawable.white_bishop, R.drawable.white_knight, R.drawable.white_rook};
        String[] nameOrder = new String[]{"rook", "knight", "bishop", "queen", "king", "bishop", "knight", "rook"};
        //black
        for (int i = 0; i < 8; i++) {
            ImageView img = new ImageView(this);
            img.setBackgroundResource(blackOrder[i]);
            img.setTag(new Piece(nameOrder[i] + " black", new int[]{0, i} ));
            img = addCommonEvent(img);
            pieces[0][i] = img;

            ImageView imgPawn = new ImageView(this);
            imgPawn.setBackgroundResource(R.drawable.black_pawn);
            imgPawn.setTag(new Piece("pawn black", new int[]{1, i} ));
            imgPawn = addCommonEvent(imgPawn);
            pieces[1][i] = imgPawn;

            blackAlive.add(img);
            blackAlive.add(imgPawn);
        }
        //white
        for (int i = 0; i < 8; i++) {
            ImageView img = new ImageView(this);
            img.setBackgroundResource(whiteOrder[i]);
            img.setTag(new Piece(nameOrder[i] + " white", new int[]{7, i} ));
            img = addCommonEvent(img);
            pieces[7][i] = img;

            ImageView imgPawn = new ImageView(this);
            imgPawn.setBackgroundResource(R.drawable.white_pawn);
            imgPawn.setTag(new Piece("pawn white", new int[]{6, i} ));
            imgPawn = addCommonEvent(imgPawn);
            pieces[6][i] = imgPawn;

            whiteAlive.add(img);
            whiteAlive.add(imgPawn);
        }

        reDraw();
    }

    private void createEmptyBoard() {
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
                img.setTag(new Piece("null", new int[]{i,j}));
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
        Log.v("BLACK ALIVE/KILL", blackAlive.size()+"/"+blackKill.size());
        Log.v("WHITE ALIVE/KILL", whiteAlive.size()+"/"+whiteKill.size());
    }

    private void swap(int[] currentPos, int[] newPos) {
        Piece tempPiece = (Piece)pieces[currentPos[0]][currentPos[1]].getTag();
        tempPiece.setPos(newPos);

        Piece tempPiece2 = (Piece)pieces[newPos[0]][newPos[1]].getTag();
        tempPiece2.setPos(currentPos);

        ImageView temp = pieces[currentPos[0]][currentPos[1]];

        pieces[currentPos[0]][currentPos[1]] = pieces[newPos[0]][newPos[1]];
        pieces[newPos[0]][newPos[1]] = temp;

    }

    private int[] getPositionArr(View v) {
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

    private boolean checkPosEmpty(int[] pos) {
        if (Math.min(pos[0], pos[1]) >= 0 && Math.max(pos[0], pos[1]) < 8)
            return(pieces[pos[0]][pos[1]].getTag().toString().equals("null"));
        return false;
    }

    private ArrayList<int[]> checkMoveAble(View v) {
        ArrayList<int[]> moveAble_temp = new ArrayList<>();
        int[] pos = getPositionArr(v);
        int move = v.getTag().toString().contains("white") ? 1 : -1;
        String[] partsV = v.getTag().toString().split(" ");

        if (v.getTag().toString().contains("pawn")) {
            if(!isKingCheckMove) {
                //move 2
                if ((pos[0] == 6 && move == 1) || (pos[0] == 1 && move == -1)) {
                    if (checkPosEmpty(new int[]{pos[0] - move * 2, pos[1]}))
                        moveAble_temp.add(new int[]{pos[0] - move * 2, pos[1]});
                }
                //move 1
                if (checkPosEmpty(new int[]{pos[0] - move, pos[1]}))
                    moveAble_temp.add(new int[]{pos[0] - move, pos[1]});
                //bắt chốt qua đường
                //2 con nam ke nhau
                boolean first = Math.abs(lastPiecePos[1] - pos[1]) == 1;
                //la chot va khac quan
                boolean second = false;
                if (!lastPieceMove.equals("")) {
                    String[] parts = lastPieceMove.split(" ");
                    second = lastPieceMove.contains("pawn") && !v.getTag().toString().contains(parts[1]);
                }
                //vua di 2 nuoc
                boolean last = Math.abs(lastPiecePos[0] - currentLastMovePiecePos[0]) == 2;
                //cung hang
                boolean third = pos[0] == currentLastMovePiecePos[0];

                if (first && second && third && last) {
                    if (lastPiecePos[0] - pos[0] == -move) {
                        moveAble_temp.add(new int[]{pos[0] - move, pos[1] - move});
                        totQuaDuongAt = new int[]{pos[0] - move, pos[1] - move};
                    } else {
                        moveAble_temp.add(new int[]{pos[0] - move, pos[1] + move});
                        totQuaDuongAt = new int[]{pos[0] - move, pos[1] + move};
                    }
                }
            }
            //kill left
            if (pos[1] - move >= 0 && !checkPosEmpty(new int[]{pos[0] - move, pos[1] - move}))
                moveAble_temp.add(new int[]{pos[0] - move, pos[1] - move});
            //kill right
            if (pos[1] + move < 8 && !checkPosEmpty(new int[]{pos[0] - move, pos[1] + move}))
                moveAble_temp.add(new int[]{pos[0] - move, pos[1] + move});
        }
        else if (v.getTag().toString().contains("knight")) {
            int[] posX = new int[]{-1, 1, 2, 2, 1, -1, -2, -2};
            int[] posY = new int[]{-2, -2, -1, 1, 2, 2, 1, -1};

            for (int i = 0; i < 8; i++) {
                int[] newPos = new int[]{posX[i] + pos[0], posY[i] + pos[1]};
                if (Math.min(newPos[0], newPos[1]) >= 0 && Math.max(newPos[0], newPos[1]) < 8) {
                    if (pieces[newPos[0]][newPos[1]].getTag() != null) {
                        if (!pieces[newPos[0]][newPos[1]].getTag().toString().contains(partsV[1]))
                            moveAble_temp.add(newPos);
                    } else
                        moveAble_temp.add(newPos);
                }
            }
        }
        else if (v.getTag().toString().contains("bishop")) {
            moveAble_temp = moveXeo(v);
        }
        else if (v.getTag().toString().contains("rook")) {
            moveAble_temp = moveLine(v);
        }
        else if (v.getTag().toString().contains("queen")) {
            moveAble_temp.addAll(moveLine(v));
            moveAble_temp.addAll(moveXeo(v));
        }
        else if (v.getTag().toString().contains("king")) {
            int[] posX = new int[]{-1, -1, -1,  0, 0, 0,  1, 1, 1};
            int[] posY = new int[]{-1,  0,  1, -1, 0, 1, -1, 0, 1};


        }

        return moveAble_temp;
    }

    private ArrayList<int[]> moveLine(View v) {
        int[] pos = getPositionArr(v);
        String[] partsV = v.getTag().toString().split(" ");
        int[] math = new int[]{-1, -1, 1, 1};
        ArrayList<int[]> temp = new ArrayList<>();

        for (int i = 0; i < 4; i++) {

            int[] currentPos = pos.clone();
            while (Math.min(currentPos[0], currentPos[1]) >= 0 && Math.max(currentPos[0], currentPos[1]) < 8) {
                if (!Arrays.equals(pos, currentPos)) {
                    if (checkPosEmpty(currentPos))
                        temp.add(currentPos.clone());
                    else if (pieces[currentPos[0]][currentPos[1]].getTag().toString().contains(partsV[1])) {
                        break;
                    } else {
                        temp.add(currentPos.clone());
                        break;
                    }
                }

                if (i % 2 == 0)
                    currentPos[0] += math[i];
                else
                    currentPos[1] += math[i];
            }
        }

        return temp;
    }

    private ArrayList<int[]> moveXeo(View v) {
        int[] pos = getPositionArr(v);
        String[] partsV = v.getTag().toString().split(" ");
        int[] posX = new int[]{1, 1, -1, -1};
        int[] posY = new int[]{1, -1, 1, -1};
        ArrayList<int[]> temp = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            int[] currentPos = pos.clone();
            while (Math.min(currentPos[0], currentPos[1]) >= 0 && Math.max(currentPos[0], currentPos[1]) < 8) {
                if (!Arrays.equals(pos, currentPos)) {
                    if (checkPosEmpty(currentPos))
                        temp.add(currentPos.clone());
                    else if (pieces[currentPos[0]][currentPos[1]].getTag().toString().contains(partsV[1])) {
                        break;
                    } else {
                        temp.add(currentPos.clone());
                        break;
                    }
                }
                currentPos[0] += posX[i];
                currentPos[1] += posY[i];
            }
        }
        return temp;
    }

}