package com.example.chess;

import android.content.ClipData;
import android.graphics.Color;
import android.media.Image;
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
    boolean isCheckMate = false;
    ArrayList<int[]> posNhapThanh = new ArrayList<>();
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
                    GridLayout gridEffect = findViewById(R.id.gridEffect);
                    ImageView imgView = (ImageView) e.getLocalState();
                    Log.v("???", v.getTag()+" "+imgView.getTag());
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

                        Piece p = ((Piece)board.pieces[posOld[0]][posOld[1]].getTag());
                        p.setMoved();
                        board.pieces[posOld[0]][posOld[1]].setTag(p);

                        board.swap(posNew, posOld);

                        isCheckMate = isCheckMate(board.pieces[posNew[0]][posNew[1]]);

                        reDraw();
                    }
                    else{
                        gridEffect.removeAllViewsInLayout();
                        if(isCheckMate) {
                            moveAble = new ArrayList<>();
                            reDrawEffect();
                        }
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

                    moveAble = getMoveAble(v);
                    for (int i = 0; i < moveAble.size(); i++) {
                        int[] temp = moveAble.get(i);
                        Log.v("moveAble", temp[0] + " " + temp[1]);
                    }
                    addEffectMoveAble();
                    reDrawEffect();
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
                    GridLayout gridEffect = findViewById(R.id.gridEffect);
                    ImageView imgView = (ImageView) e.getLocalState();

                    int[] posNew = ((Piece)v.getTag()).getPos();
                    int[] posOld = ((Piece)imgView.getTag()).getPos();
                    //check khong thay doi vi tri
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
                        spAct.setLastPieceMove(board.pieces[posOld[0]][posOld[1]].getTag().toString());

                        //nhap thanh
                        for(int i = 0; i < posNhapThanh.size(); i++){
                            Log.v("nhap thanh", posNhapThanh.get(i)[0]+":"+posNhapThanh.get(i)[1]+" - "+posNew[0]+":"+posNew[1]);
                            int[] posNhap = posNhapThanh.get(i);
                            if(Arrays.equals(posNhap, posNew)){
                                ImageView rook = createEmptyView();
                                int[] posSwap = new int[]{posOld[0],-1};
                                //left
                                if(posNhap[1] - posOld[1] < 0){
                                    rook = board.pieces[posOld[0]][0];
                                    posSwap[1] = posNew[1]+1;
                                }
                                //right
                                else{
                                    rook = board.pieces[posOld[0]][7];
                                    posSwap[1] = posNew[1]-1;
                                }

                                //swap xe

                                Piece p = (Piece) rook.getTag();
                                p.setMoved();
                                rook.setTag(p);
                                int[] rookPos = ((Piece)rook.getTag()).getPos();
                                Log.v("clgt", posSwap[0]+":"+posSwap[1]+" - "+rookPos[0]+":"+rookPos[1]);
                                board.swap(posSwap,  rookPos);

                                break;
                            }
                        }


                        Log.v("LAST PIECE MOVED", spAct.getLastPieceMove());
                        spAct.setLastPiecePos(posOld);
                        spAct.setCurrentLastMovePiecePos(posNew);

                        Piece p = ((Piece)board.pieces[posOld[0]][posOld[1]].getTag());
                        p.setMoved();
                        board.pieces[posOld[0]][posOld[1]].setTag(p);

                        board.swap(posNew, posOld);

                        isCheckMate = isCheckMate(board.pieces[posNew[0]][posNew[1]]);

                        reDraw();
                    }
                    else {
                        if(!isCheckMate)
                            gridEffect.removeAllViewsInLayout();
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
        GridLayout gridEffect = findViewById(R.id.gridEffect);
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

                board.effect[i][j] = createEmptyView();

                gridEffect.addView(board.effect[i][j]);
                gridBoard.addView(imgView);
                gridChess.addView(img);
            }
        }
    }

    private void reDraw() {
        if (!isKingCheckMove) {
            moveAble = new ArrayList<>();
            Log.v("isCheckMate", isCheckMate+"");
            addEffectMoveAble();
            GridLayout gridEffect = findViewById(R.id.gridEffect);
            GridLayout gridChess = findViewById(R.id.gridChess);
            gridChess.removeAllViewsInLayout();
            gridEffect.removeAllViewsInLayout();

            if (isCheckMate) {
                addEffectCheckMate();
            }

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (board.pieces[i][j].getParent() != null) {
                        ((GridLayout) board.pieces[i][j].getParent()).removeView(board.pieces[i][j]);
                    }
                    gridChess.addView(board.pieces[i][j]);
                    if(isCheckMate){
                        gridEffect.addView(board.effect[i][j]);
                    }
                }
            }
            Log.v("BLACK ALIVE/KILL", blackTeam.alive.size() + "/" + blackTeam.kill.size());
            Log.v("WHITE ALIVE/KILL", whiteTeam.alive.size() + "/" + whiteTeam.kill.size());
        }
    }

    private void reDrawEffect(){
        GridLayout gridEffect = findViewById(R.id.gridEffect);
        gridEffect.removeAllViewsInLayout();
        String[] partV = spAct.lastPieceMove.split(" ");
        int[] kingPos = new int[]{-1,-1};
        if(isCheckMate) {
            Team t = new Team(blackTeam);
            if(!partV[1].equals("white"))
                t = new Team(whiteTeam);
            kingPos = ((Piece)t.alive.get(0).getTag()).getPos();
            board.effect[kingPos[0]][kingPos[1]] = createEmptyView();
            board.effect[kingPos[0]][kingPos[1]].setBackgroundColor(Color.parseColor("#cf213e"));
        }

        if(moveAble.size() > 0 || isCheckMate) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if(moveAble.size() != 0)
                        gridEffect.addView(board.effect[i][j]);
                    else {
                        if (Arrays.equals(kingPos, new int[]{i,j}))
                            gridEffect.addView(board.effect[i][j]);
                        else
                            gridEffect.addView(createEmptyView());
                    }
                }
            }
        }
        Log.v("Add effect", "Added");
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

    private ArrayList<int[]> getMoveAble(View v) {
        if(!isKingCheckMove)
            posNhapThanh = new ArrayList<>();
        ArrayList<int[]> moveAble_temp = new ArrayList<>();
        int[] pos = ((Piece)v.getTag()).getPos();
        int move = v.getTag().toString().contains("white") ? 1 : -1;
        String[] partsV = v.getTag().toString().split(" ");

        if (v.getTag().toString().contains("pawn")) {
            if (!isKingCheckMove) {
                //move 1
                if (isPosEmpty(new int[]{pos[0] - move, pos[1]})) {
                    moveAble_temp.add(new int[]{pos[0] - move, pos[1]});
                    //move 2
                    if ((pos[0] == 6 && move == 1) || (pos[0] == 1 && move == -1)) {
                        if (isPosEmpty(new int[]{pos[0] - move * 2, pos[1]}))
                            moveAble_temp.add(new int[]{pos[0] - move * 2, pos[1]});
                    }
                }
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
            if ( pos[0] - move >= 0 && pos[0] - move < 8 && pos[1] - move >= 0 && pos[1] - move < 8)
                if (!board.pieces[pos[0] - move][pos[1] - move].getTag().toString().contains(partsV[1])) {
                    if (!isPosEmpty(new int[]{pos[0] - move, pos[1] - move}) || isKingCheckMove)
                        moveAble_temp.add(new int[]{pos[0] - move, pos[1] - move});
                }
            //kill right
            if ( pos[0] - move >= 0 && pos[0] - move < 8 && pos[1] + move >= 0 && pos[1] + move < 8)
                if (!board.pieces[pos[0] - move][pos[1] + move].getTag().toString().contains(partsV[1])) {
                    if (!isPosEmpty(new int[]{pos[0] - move, pos[1] + move}) || isKingCheckMove)
                        moveAble_temp.add(new int[]{pos[0] - move, pos[1] + move});
                }
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
            int[] posX = new int[]{-1, -1, -1, 0, 0, 1, 1, 1};
            int[] posY = new int[]{-1, 0, 1, -1, 1, -1, 0, 1};

            for (int i = 0; i < posX.length; i++) {
                posX[i] += pos[0];
                posY[i] += pos[1];

                if (Math.min(posX[i], posY[i]) >= 0
                        && Math.max(posX[i], posY[i]) < 8
                        && !board.pieces[posX[i]][posY[i]].getTag().toString().contains(partsV[1]))
                    moveAble_temp.add(new int[]{posX[i], posY[i]});
            }
            //nhap thanh
            if (!isCheckMate && !((Piece) board.pieces[pos[0]][pos[1]].getTag()).isMoved && !isKingCheckMove) {
                //left
                for (int l = pos[1]-1; l >= 0; l--) {
                    boolean isNull = board.pieces[pos[0]][l].getTag().toString().contains("null");
                    if (!isNull) {
                        boolean isTeam = board.pieces[pos[0]][l].getTag().toString().contains(partsV[1]);
                        boolean isMoved = ((Piece) board.pieces[pos[0]][l].getTag()).isMoved;
                        boolean isRook = board.pieces[pos[0]][l].getTag().toString().contains("rook");
                        if (isTeam && !isMoved && isRook) {
                            moveAble_temp.add(new int[]{pos[0], pos[1] - 2});
                            posNhapThanh.add(new int[]{pos[0], pos[1] - 2});
                        } else
                            break;
                    }
                }
                //right
                for (int l = pos[1]+1; l < 8; l++) {
                    boolean isNull = board.pieces[pos[0]][l].getTag().toString().contains("null");
                    if (!isNull) {
                        boolean isTeam = board.pieces[pos[0]][l].getTag().toString().contains(partsV[1]);
                        boolean isMoved = ((Piece) board.pieces[pos[0]][l].getTag()).isMoved;
                        boolean isRook = board.pieces[pos[0]][l].getTag().toString().contains("rook");
                        if (isTeam && !isMoved && isRook) {
                            moveAble_temp.add(new int[]{pos[0], pos[1] + 2});
                            posNhapThanh.add(new int[]{pos[0], pos[1] + 2});
                        } else
                            break;
                    }
                }
            }
        }
        if(!isKingCheckMove) {
            try {
                isKingCheckMove = true;
                ArrayList<int[]> posCanMove = new ArrayList<>();
                for (int i = 0; i < moveAble_temp.size(); i++) {
                    int[] temp = moveAble_temp.get(i);
                    Log.v("pawn 2", temp[0]+" "+temp[1]);
                    if (Math.min(temp[0], temp[1]) >= 0 && Math.max(temp[0], temp[1]) < 8)
                        if (!isSuicide(v, moveAble_temp.get(i).clone())) {
                            posCanMove.add(moveAble_temp.get(i).clone());
                        }
//                    else{
//                            isCheckMate = true;
//                        }
                }
//                if(posCanMove.size() == moveAble_temp.size()){
//                    isCheckMate = false;
//                }

                return posCanMove;
            }catch (Exception ex){ Log.v("Error sth 1", ex.getMessage()+""); }
            finally {
                isKingCheckMove = false;
            }
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

    private boolean isSuicide(View v, int[] nextPos){
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

        ImageView emptyImg = createEmptyView();
        emptyImg.setTag(new Piece("null", nextPos));
        board.pieces[nextPos[0]][nextPos[1]] = emptyImg;
        board.swap(currentPos, nextPos);

        Team currentOpponent = whiteTeam.clone();
        if(partV[1].equals("white"))
            currentOpponent = blackTeam.clone();

        //find current king pos
        int[] kingPos = ((Piece)oldTeam.alive.get(0).getTag()).getPos();
        if(v.getTag().toString().contains("king")){
            kingPos = ((Piece)v.getTag()).getPos();
        }

        try{
            currentOpponent.alive.remove(imgNextPos);
            for(int i = 0; i < currentOpponent.alive.size(); i++){
                ArrayList<int[]> temp = getMoveAble(currentOpponent.alive.get(i));
                for(int j = 0; j < temp.size(); j++){
                    int[] pos = temp.get(j).clone();
//                    Log.v("????????????",pos[0]+":"+pos[1]+" "+kingPos[0]+":"+kingPos[1]);
                    if(pos[0] == kingPos[0] && pos[1] == kingPos[1] )
                        return true;
                }
            }

        }
        catch (Exception ex){
            Log.v("Error sth 2", ex.getMessage()+"");
        }
        finally {
            if(!imgNextPos.getTag().toString().equals("null")) {
                if(imgNextPos.getTag().toString().contains("king"))
                    currentOpponent.alive.add(0, imgNextPos);
                else
                    currentOpponent.alive.add(imgNextPos);
            }

            imgNextPos.setTag(p);
            board.pieces[currentPos[0]][currentPos[1]] = imgNextPos;
            board.swap(currentPos, nextPos);
        }

        return false;
    }

    private  boolean isCheckMate(View v){
        String[] partV = v.getTag().toString().split(" ");
        Team t = new Team(blackTeam);
        Team o = new Team(whiteTeam);
        if(partV[1].equals("white")) {
            t = new Team(whiteTeam);
            o = new Team(blackTeam);
        }
        try {
            for (int i = 0; i < t.alive.size(); i++) {
                isKingCheckMove = true;

                ArrayList<int[]> move = getMoveAble(t.alive.get(i));
                for (int j = 0; j < move.size(); j++) {
                    int[] temp = move.get(j);
                    int[] temp2 = ((Piece)o.alive.get(0).getTag()).getPos();
                    if(Arrays.equals(temp, temp2))
                        return true;
                }
            }
        }
        catch (Exception ex){  Log.v("Error sth 3", ex.getMessage()+""); }
        finally {
            isKingCheckMove = false;
        }
        return false;
    }

    private void addEffectMoveAble(){
        //reset effect array
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                board.effect[i][j] = createEmptyView();
            }
        }
        for(int i = 0; i < moveAble.size(); i++){
            int[] pos = moveAble.get(i);
            board.effect[pos[0]][pos[1]].setBackgroundColor(Color.parseColor("#85aff2"));
        }
    }

    private void addEffectCheckMate(){
        //reset effect array
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                board.effect[i][j] = createEmptyView();
            }
        }

        String[] partLast = spAct.lastPieceMove.split(" ");
        int[] kingPos = ((Piece) blackTeam.alive.get(0).getTag()).getPos();
        if(partLast[1].equals("black")){
            kingPos = ((Piece) whiteTeam.alive.get(0).getTag()).getPos();
        }

        board.effect[kingPos[0]][kingPos[1]].setBackgroundColor(Color.parseColor("#cf213e"));
        Log.v("add check mate effect", "added");
    }
}