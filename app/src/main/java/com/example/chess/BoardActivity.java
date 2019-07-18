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
    ArrayList<int[]> moveAble = new ArrayList<>();

    Board board = new Board();
    Team blackTeam = new Team();
    Team whiteTeam = new Team();
    SpecialAction spAct = new SpecialAction();

    boolean isKingCheckMove = false;
    //Toast.makeText(BoardActivity.this, (char) (pos[0] + 65) + "" + (pos[1] + 1) + " " + v.getTag(), Toast.LENGTH_SHORT).show();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);

        createEmptyBoard();
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
                        if (board.pieces[posNew[0]][posNew[1]].getTag().toString().contains("black")) {
                            whiteTeam.kill.add(board.pieces[posNew[0]][posNew[1]]);
                            blackTeam.alive.remove(board.pieces[posNew[0]][posNew[1]]);
                        }
                        else {
                            blackTeam.kill.add(board.pieces[posNew[0]][posNew[1]]);
                            whiteTeam.alive.remove(board.pieces[posNew[0]][posNew[1]]);
                        }

                        spAct.setLastPieceMove(board.pieces[posOld[0]][posOld[1]].getTag().toString());

                        Log.v("LAST PIECE KILL", spAct.getLastPieceMove());

                        spAct.setLastPiecePos(posOld);
                        spAct.setCurrentLastMovePiecePos(posNew);

                        ImageView emptyView = createEmptyView();
                        emptyView.setTag(new Piece("null", posNew));
                        board.pieces[posNew[0]][posNew[1]] = emptyView;
                        board.swap(posNew, posOld);
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
        final ImageView img = new ImageView(this);
        img.setBackgroundResource(R.drawable.empty);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(125, 125);
        img.setLayoutParams(lp);

        img.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent e) {
                if (e.getAction() == DragEvent.ACTION_DROP) {
                    ImageView imgView = (ImageView) e.getLocalState();

                    int[] posNew = ((Piece)v.getTag()).getPos();
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
                        if (Arrays.equals(spAct.getTotQuaDuongAt(), posNew)) {
                            int[] temp = spAct.getCurrentLastMovePiecePos();
                            if (board.pieces[temp[0]][temp[1]].getTag().toString().contains("black")) {
                                whiteTeam.kill.add(board.pieces[temp[0]][temp[1]]);
                                blackTeam.alive.remove(board.pieces[temp[0]][temp[1]]);
                            }
                            else {
                                blackTeam.kill.add(board.pieces[temp[0]][temp[1]]);
                                whiteTeam.alive.remove(board.pieces[temp[0]][temp[1]]);
                            }
                            ImageView imgTemp = createEmptyView();
                            imgTemp.setTag(new Piece("null", temp));
                            board.pieces[temp[0]][temp[1]] = imgTemp;

                            spAct.setTotQuaDuongAt(new int[]{-1, -1});
                        }
                        Log.v("???", board.pieces[0][0].getTag().toString()+"");
                        spAct.setLastPieceMove(board.pieces[posOld[0]][posOld[1]].getTag().toString());

                        Log.v("LAST PIECE MOVED", spAct.getLastPieceMove());
                        spAct.setLastPiecePos(posOld);
                        spAct.setCurrentLastMovePiecePos(posNew);

                        board.swap(posNew, posOld);
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
            board.pieces[0][i] = img;

            ImageView imgPawn = new ImageView(this);
            imgPawn.setBackgroundResource(R.drawable.black_pawn);
            imgPawn.setTag(new Piece("pawn black", new int[]{1, i} ));
            imgPawn = addCommonEvent(imgPawn);
            board.pieces[1][i] = imgPawn;

            if(img.getTag().toString().contains("king"))
                blackTeam.alive.add(0, img);
            else
                blackTeam.alive.add(img);
            blackTeam.alive.add(imgPawn);
        }

        //white
        for (int i = 0; i < 8; i++) {
            ImageView img = new ImageView(this);
            img.setBackgroundResource(whiteOrder[i]);
            img.setTag(new Piece(nameOrder[i] + " white", new int[]{7, i} ));
            img = addCommonEvent(img);
            board.pieces[7][i] = img;

            ImageView imgPawn = new ImageView(this);
            imgPawn.setBackgroundResource(R.drawable.white_pawn);
            imgPawn.setTag(new Piece("pawn white", new int[]{6, i} ));
            imgPawn = addCommonEvent(imgPawn);
            board.pieces[6][i] = imgPawn;

            if(img.getTag().toString().contains("king"))
                whiteTeam.alive.add(0, img);
            else
                whiteTeam.alive.add(img);
            whiteTeam.alive.add(imgPawn);
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
                board.layout[i][j] = imgView;

                ImageView img = createEmptyView();
                img.setTag(new Piece("null", new int[]{i,j}));
                board.pieces[i][j] = img;

                gridBoard.addView(imgView);
                gridChess.addView(img);
            }
        }
    }

    private void reDraw() {
        if (!isKingCheckMove) {
            GridLayout gridBoard = findViewById(R.id.gridBoard);
            GridLayout gridChess = findViewById(R.id.gridChess);
            gridChess.removeAllViewsInLayout();
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (board.pieces[i][j].getParent() != null) {
                        ((GridLayout) board.pieces[i][j].getParent()).removeView(board.pieces[i][j]);
                    }
                    gridChess.addView(board.pieces[i][j]);
                }
            }
            Log.v("BLACK ALIVE/KILL", blackTeam.alive.size() + "/" + blackTeam.alive.size());
            Log.v("WHITE ALIVE/KILL", whiteTeam.alive.size() + "/" + whiteTeam.alive.size());
        }
    }

    private int[] getPositionArr(View v) {
        if (v == null)
            return new int[]{-1, -1};

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.pieces[i][j] == v)
                    return new int[]{i, j};
            }
        }
        return new int[]{-1, -1};
    }

    private boolean isPosEmpty(int[] pos) {
        if (Math.min(pos[0], pos[1]) >= 0 && Math.max(pos[0], pos[1]) < 8)
            return(board.pieces[pos[0]][pos[1]].getTag().toString().equals("null"));
        return false;
    }

    private ArrayList<int[]> checkMoveAble(View v) {
        ArrayList<int[]> moveAble_temp = new ArrayList<>();
        int[] pos = ((Piece)v.getTag()).getPos();
        int move = v.getTag().toString().contains("white") ? 1 : -1;
        String[] partsV = v.getTag().toString().split(" ");

        if (v.getTag().toString().contains("pawn")) {
            if(!isKingCheckMove) {
                //move 2
                if ((pos[0] == 6 && move == 1) || (pos[0] == 1 && move == -1)) {
                    if (isPosEmpty(new int[]{pos[0] - move * 2, pos[1]}))
                        moveAble_temp.add(new int[]{pos[0] - move * 2, pos[1]});
                }
                //move 1
                if (isPosEmpty(new int[]{pos[0] - move, pos[1]}))
                    moveAble_temp.add(new int[]{pos[0] - move, pos[1]});
                //bắt chốt qua đường
                //2 con nam ke nhau
                boolean first = Math.abs(spAct.lastPiecePos[1] - pos[1]) == 1;
                //la chot va khac quan
                boolean second = false;
                if (!spAct.lastPieceMove.equals("")) {
                    String[] parts = spAct.lastPieceMove.split(" ");
                    second = spAct.lastPieceMove.contains("pawn") && !v.getTag().toString().contains(parts[1]);
                }
                //vua di 2 nuoc
                boolean last = Math.abs(spAct.lastPiecePos[0] - spAct.currentLastMovePiecePos[0]) == 2;
                //cung hang
                boolean third = pos[0] == spAct.currentLastMovePiecePos[0];

                if (first && second && third && last) {
                    if (spAct.lastPiecePos[0] - pos[0] == -move) {
                        moveAble_temp.add(new int[]{pos[0] - move, pos[1] - move});
                        spAct.setTotQuaDuongAt(new int[]{pos[0] - move, pos[1] - move});
                    } else {
                        moveAble_temp.add(new int[]{pos[0] - move, pos[1] + move});
                        spAct.setTotQuaDuongAt(new int[]{pos[0] - move, pos[1] + move});
                    }
                }
            }
            //kill left

            if(pos[1] - move >= 0)
                if ( !isPosEmpty(new int[]{pos[0] - move, pos[1] - move}) || isKingCheckMove)
                    moveAble_temp.add(new int[]{pos[0] - move, pos[1] - move});
            //kill right
            if(pos[1] + move < 8)
                if(!isPosEmpty(new int[]{pos[0] - move, pos[1] + move}) || isKingCheckMove)
                    moveAble_temp.add(new int[]{pos[0] - move, pos[1] + move});
        }
        else if (v.getTag().toString().contains("knight")) {
            int[] posX = new int[]{-1, 1, 2, 2, 1, -1, -2, -2};
            int[] posY = new int[]{-2, -2, -1, 1, 2, 2, 1, -1};

            for (int i = 0; i < 8; i++) {
                int[] newPos = new int[]{posX[i] + pos[0], posY[i] + pos[1]};
                if (Math.min(newPos[0], newPos[1]) >= 0 && Math.max(newPos[0], newPos[1]) < 8) {
                    if (board.pieces[newPos[0]][newPos[1]].getTag() != null) {
                        if (!board.pieces[newPos[0]][newPos[1]].getTag().toString().contains(partsV[1]))
                            moveAble_temp.add(newPos);
                    } else
                        moveAble_temp.add(newPos);
//                    if(isKingCheckMove)
//                        moveAble_temp.add(newPos);
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
            int[] posX = new int[]{-1, -1, -1,  0, 0,  1, 1, 1};
            int[] posY = new int[]{-1,  0,  1, -1, 1, -1, 0, 1};
            ArrayList<int[]> movePossibility = new ArrayList<>();

            for(int i = 0; i < posX.length; i++){
                posX[i] += pos[0];
                posY[i] += pos[1];

                if(Math.min(posX[i], posY[i]) >= 0
                        && Math.max(posX[i], posY[i]) < 8
                        && !board.pieces[posX[i]][posY[i]].getTag().toString().contains(partsV[1]))
                    movePossibility.add(new int[]{ posX[i], posY[i] });
            }
            moveAble_temp.addAll(movePossibility);
//            if(!isKingCheckMove) {
//                for(int i = 0; i < movePossibility.size(); i++)
//                    //neu pos empty hoac la quan dich
//                    if(isPosEmpty(movePossibility.get(i)) ||
//                            !pieces[movePossibility.get(i)[0]][movePossibility.get(i)[1]].getTag().toString().contains(partsV[1])){
//                        isKingCheckMove = true;
//                        break;
//                    }
//
//                //king can move
//                if (isKingCheckMove) {
//                    ArrayList<int[]> cantMovePos = new ArrayList<>();
//                    ArrayList<ImageView> check = new ArrayList<>(whiteAlive);
//                    if (partsV[1].equals("white"))
//                        check = new ArrayList<>(blackAlive);
//
//                    for (int i = 0; i < check.size(); i++) {
//                        cantMovePos.addAll(checkMoveAble(check.get(i)));
//                    }
//
//                    ArrayList<int[]> moveTemp = new ArrayList<>(movePossibility);
//
//                    for (int i = 0; i < movePossibility.size(); i++) {
//                        for(int j = 0; j < cantMovePos.size(); j++){
//                            int[] temp = cantMovePos.get(j);
//                            int[] temp2 = movePossibility.get(i);
//
//                            if(temp[0] == temp2[0] && temp[1] == temp2[1]){
//                                moveTemp.remove(temp2);
//                                break;
//                            }
//                        }
//                    }
//
//                    moveAble_temp.addAll(moveTemp);
//
//                    isKingCheckMove = false;
//                }
//            }else{
//                moveAble_temp.addAll(movePossibility);
//            }
        }

        if(!isKingCheckMove) {
            isKingCheckMove = true;
            ArrayList<int[]> posCanMove = new ArrayList<>();
            for (int i = 0; i < moveAble_temp.size(); i++) {
                int[] temp = moveAble_temp.get(i);
                if(Math.min(temp[0], temp[1]) >= 0 && Math.min(temp[0], temp[1]) < 8)
                    if (!isCheckMate(v, moveAble_temp.get(i).clone())) {
                        posCanMove.add(moveAble_temp.get(i).clone());
                    }
            }
            isKingCheckMove = false;
            return posCanMove;
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
                    if (isPosEmpty(currentPos))
                        temp.add(currentPos.clone());
                    else if (board.pieces[currentPos[0]][currentPos[1]].getTag().toString().contains(partsV[1])) {
//                        if(isKingCheckMove)
//                            temp.add(currentPos.clone());
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
                    if (isPosEmpty(currentPos))
                        temp.add(currentPos.clone());
                    else if (board.pieces[currentPos[0]][currentPos[1]].getTag().toString().contains(partsV[1])) {
//                        if(isKingCheckMove)
//                            temp.add(currentPos.clone());
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

    private boolean isCheckMate(View v, int[] nextPos){
        String[] partV = v.getTag().toString().split(" ");
        int[] currentPos = ((Piece)v.getTag()).getPos();

        Team oldOpponent = whiteTeam.clone();
        Team oldTeam = blackTeam.clone();
        if(partV[1].equals("white")) {
            oldOpponent = blackTeam.clone();
            oldTeam = whiteTeam.clone();
        }

        //simulating
        ImageView imgNextPos = board.pieces[nextPos[0]][nextPos[1]];
        Piece p = (Piece)imgNextPos.getTag();
        p.setPos(((Piece)board.pieces[currentPos[0]][currentPos[1]].getTag()).getPos());
        imgNextPos.setTag(p);

        ImageView emptyImg = createEmptyView();
        emptyImg.setTag(new Piece("null", nextPos));
        board.pieces[nextPos[0]][nextPos[1]] = emptyImg;
        board.swap(currentPos, nextPos);

        Team currentOpponent = whiteTeam.clone();
        if(partV[1].equals("white"))
            currentOpponent = blackTeam.clone();

        //find current king pos
        int[] kingPos = ((Piece)oldTeam.alive.get(0).getTag()).getPos();

        try{
            for(int i = 0; i < currentOpponent.alive.size(); i++){
                ArrayList<int[]> temp = checkMoveAble(currentOpponent.alive.get(i));
                for(int j = 0; j < temp.size(); j++){
                    int[] pos = temp.get(j).clone();
                    if(pos[0] == kingPos[0] && pos[1] == kingPos[1] )
                        return true;
                }
            }
            return false;
        }
        catch (Exception ex){
            Log.v("Error sth", ex.getMessage()+"");
        }
        finally {
            board.pieces[currentPos[0]][currentPos[1]] = imgNextPos;
            board.swap(currentPos, nextPos);

            if(partV[1].equals("white")) {
//                Log.v("equal team 1", blackTeam.alive.equals(oldOpponent.alive)+"");
                blackTeam = oldOpponent.clone();
                whiteTeam = oldTeam.clone();
//                Log.v("equal team 2", blackTeam.alive.equals(oldOpponent.alive)+"");
            }
            else {
//                Log.v("equal team 1", blackTeam.alive.equals(oldTeam.alive)+"");
                blackTeam = oldTeam.clone();
                whiteTeam = oldOpponent.clone();
//                Log.v("equal team 2", blackTeam.alive.equals(oldTeam.alive)+"");
            }
        }

        return false;
    }

}