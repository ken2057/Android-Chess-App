package com.example.chess;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chess.Adapters.ChatAdapter;
import com.example.chess.AsyncTasks.GetPostMoveAble;
import com.example.chess.AsyncTasks.PostSendCheckMoved;
import com.example.chess.AsyncTasks.PostSendMove;
import com.example.chess.AsyncTasks.RequestCancelLobby;
import com.example.chess.Threads.ThrCheckMove;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


///Main function test push git
public class BoardActivity extends AppCompatActivity {
    public static ArrayList<int[]> moveAble = new ArrayList<>();
    //lưu trữ các vị trí có thể nhập thành
    static ArrayList<int[]> posCastling = new ArrayList<>();

    public static ArrayList<String> chatIdOrder;
    public static ArrayList<String> chatMsg;

    public static GridLayout gridEffect;
    public static GridLayout gridBoard;
    public static GridLayout gridChess;

    public static Board board;
    public static Team blackTeam;
    public static Team whiteTeam;
    public static SpecialAction spAct;

    public static Thread thrCheckMoved;

    public static int numMove = 0;
    public static int numChat = 0;
    static int size;

    public static boolean isWhite;
    static boolean isEvolve = false;
    public static boolean isKingCheckMove = false;
    public static boolean isCheckMate = false;
    public static boolean isWaitingMove = false;
    //block thread call when check opponent moved
    public static boolean isCheckMoved = false;
    //-1=lose - 0=null - 1=win - 2=draw
    public static int isWin = 0;

    public static String pawnEvolveTo = " ";
    public static String androidId, matchId;

    public static String URL;

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);

        GridLayout gridBoard = findViewById(R.id.gridBoard);
        size = gridBoard.getLayoutParams().width/8;

        referenceVariable();
        addEventForBtn();
        getDataFromIntent();

        createEmptyBoard();

        setupBoard();
        thrCheckMoved = new ThrCheckMove();
        thrCheckMoved.start();
    }

    private void addEventForBtn(){
        Button btnOpenChat = findViewById(R.id.btnOpenChat);
        btnOpenChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chat = new Intent(BoardActivity.this, ChatActivity.class);
                startActivity(chat);
            }
        });
    }

    private void referenceVariable(){
        context = this;
        URL = getString(R.string.url_API);
        gridEffect = findViewById(R.id.gridEffect);
        gridBoard = findViewById(R.id.gridBoard);
        gridChess = findViewById(R.id.gridChess);

        board = new Board();
        blackTeam = new Team();
        whiteTeam = new Team();
        spAct = new SpecialAction();

        chatIdOrder = new ArrayList<String>(){};
        chatMsg = new ArrayList<String>(){};

        chatIdOrder.add(""); chatIdOrder.add("");
        chatMsg.add(""); chatMsg.add("");
    }

    private void getDataFromIntent(){
        Intent intent = getIntent();
        isWhite = intent.getBooleanExtra("isWhite", true);
        androidId = intent.getStringExtra("androidId");
        matchId = intent.getStringExtra("matchId");
        isWaitingMove = !isWhite;
    }

    private static void createPopupPawnEvolve( View v) {
        isEvolve = true;
        final String[] partV = v.getTag().toString().split(" ");

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View customView = layoutInflater.inflate(R.layout.activity_test,null);

        //instantiate popup window
        final PopupWindow popupWindow = new PopupWindow(
                customView,
                GridLayout.LayoutParams.WRAP_CONTENT,
                GridLayout.LayoutParams.WRAP_CONTENT
        );

        //display the popup window
//        popupWindow.showAtLocation(BoardActivity, Gravity.CENTER, 0, 0);
        popupWindow.showAtLocation(v,Gravity.CENTER, 0, 0);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);

        ImageButton imgB1 = customView.findViewById(R.id.imgBtn1);
        ImageButton imgB2 = customView.findViewById(R.id.imgBtn2);
        ImageButton imgB3 = customView.findViewById(R.id.imgBtn3);
        ImageButton imgB4 = customView.findViewById(R.id.imgBtn4);

        if(partV[1].equals("white")){
            imgB1.setBackgroundResource(R.drawable.white_bishop);
            imgB2.setBackgroundResource(R.drawable.white_knight);
            imgB3.setBackgroundResource(R.drawable.white_rook);
            imgB4.setBackgroundResource(R.drawable.white_queen);
        }else{
            imgB1.setBackgroundResource(R.drawable.black_bishop);
            imgB2.setBackgroundResource(R.drawable.black_knight);
            imgB3.setBackgroundResource(R.drawable.black_rook);
            imgB4.setBackgroundResource(R.drawable.black_queen);
        }
        //add event khi bấm chọn phong tốt
        imgB1.setOnClickListener(view -> {
            Log.v("image button", view.getId()+"");
            popupWindow.dismiss();
            isEvolve = false;
            pawnEvolveTo = "bishop "+partV[1];
            pawnEvolve();
        });
        imgB2.setOnClickListener(view -> {
            Log.v("image button", view.getId()+"");
            popupWindow.dismiss();
            isEvolve = false;
            pawnEvolveTo = "knight "+partV[1];
            pawnEvolve();
        });
        imgB3.setOnClickListener(view -> {
            Log.v("image button", view.getId()+"");
            popupWindow.dismiss();
            isEvolve = false;
            pawnEvolveTo = "rook "+partV[1];
            pawnEvolve();
        });
        imgB4.setOnClickListener(view -> {
            Log.v("image button", view.getId()+"");
            popupWindow.dismiss();
            isEvolve = false;
            pawnEvolveTo = "queen "+partV[1];
            pawnEvolve();
        });

        popupWindow.setFocusable(true);
    }

    private static void pawnEvolve(){
        int[] temp = spAct.currentLastMovePiecePos;
        Piece p = (Piece)board.pieces[temp[0]][temp[1]].getTag();
        p.name = pawnEvolveTo;
        board.pieces[temp[0]][temp[1]].setTag(p);

        String[] partName = pawnEvolveTo.split(" ");

        if(partName[1].equals("white")){
            switch (partName[0]) {
                case "knight":
                    board.pieces[temp[0]][temp[1]].setBackgroundResource(R.drawable.white_knight);
                    break;
                case "bishop":
                    board.pieces[temp[0]][temp[1]].setBackgroundResource(R.drawable.white_bishop);
                    break;
                case "rook":
                    board.pieces[temp[0]][temp[1]].setBackgroundResource(R.drawable.white_rook);
                    break;
                case "queen":
                    board.pieces[temp[0]][temp[1]].setBackgroundResource(R.drawable.white_queen);
                    break;
            }
        }else{
            switch (partName[0]) {
                case "knight":
                    board.pieces[temp[0]][temp[1]].setBackgroundResource(R.drawable.black_knight);
                    break;
                case "bishop":
                    board.pieces[temp[0]][temp[1]].setBackgroundResource(R.drawable.black_bishop);
                    break;
                case "rook":
                    board.pieces[temp[0]][temp[1]].setBackgroundResource(R.drawable.black_rook);
                    break;
                case "queen":
                    board.pieces[temp[0]][temp[1]].setBackgroundResource(R.drawable.black_queen);
                    break;
            }
        }
        isEvolve = false;
        isCheckMate = isCheckMate(board.pieces[temp[0]][temp[1]]);
        reDraw();
    }

    private ImageView addCommonEvent(final  ImageView img) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size-20, size-4);
        lp.setMargins(10, 2, 10, 2);

        img.setLayoutParams(lp);
        img.setClickable(true);

        img.setOnDragListener((v, e) -> {

            if (e.getAction() == DragEvent.ACTION_DROP) {
                GridLayout gridEffect = findViewById(R.id.gridEffect);
                ImageView imgView = (ImageView) e.getLocalState();
                int[] posNew = ((Piece) v.getTag()).getPos();
                int[] posOld = ((Piece) imgView.getTag()).getPos();
                boolean flag = false;
                //check k drop empty
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
                    } else {
                        blackTeam.kill.add(board.pieces[posNew[0]][posNew[1]]);
                        whiteTeam.alive.remove(board.pieces[posNew[0]][posNew[1]]);
                    }

                    spAct.setLastKillPos(posNew);
                    spAct.setLastPieceMove(board.pieces[posOld[0]][posOld[1]].getTag().toString());
                    spAct.setLastPiecePos(posOld);
                    spAct.setCurrentLastMovePiecePos(posNew);

                    Log.v("LAST PIECE KILL", spAct.getLastPieceMove());

                    //phong tốt khi tốt đi vào hàng 0 hoặc 7
                    //else reset value đè phòng nó đã phogn tốt nhưng vẫn bị stuck
                    if ((posNew[0] == 0 || posNew[0] == 7) && imgView.getTag().toString().contains("pawn")) {
                        Log.v("Pawn evolve", posNew[0] + ":" + posNew[1]);
                        createPopupPawnEvolve(imgView);
                    }else{pawnEvolveTo = " ";}

                    //tạo empty view tại vị trí bị ăn và swap với vị trí đi ăn
                    ImageView emptyView = createEmptyView(posNew);
                    board.pieces[posNew[0]][posNew[1]] = emptyView;

                    Piece p = ((Piece) board.pieces[posOld[0]][posOld[1]].getTag());
                    p.setMoved();
                    board.pieces[posOld[0]][posOld[1]].setTag(p);

                    board.swap(posNew, posOld);

                    isCheckMate = isCheckMate(board.pieces[posNew[0]][posNew[1]]);

                    reDraw();
                } else {
                    pawnEvolveTo = " ";
                    spAct.setLastKillPos(new int[]{-1, -1});
                    gridEffect.removeAllViewsInLayout();
                    if (isCheckMate) {
                        moveAble = new ArrayList<>();
                        reDrawEffect();
                    }
                }
            }

            return true;
        });
        //thêm sự kiện drag khi mà là team
        if(isWhite == img.getTag().toString().contains("white")) {

            img.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN && isWin == 0) {
                        spAct.resetCastling();
                        //3 điều kiện sẽ không cho move nữa
                        //-đang đợi chọn phong tốt
                        //-đang đợi đối phương move
                        //-nếu isWin != 0
                        if (isEvolve || isWaitingMove || isWin != 0)
                            return false;

                        ClipData data = ClipData.newPlainText("", "");
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                        v.startDragAndDrop(data, shadowBuilder, v, 0);

                        try {
                            new GetPostMoveAble().execute(v).get();
                        }catch (Exception e){
                            //
                        }
                    }
                    return true;
                }
            });
        }
        return img;
    }

    public static ImageView createEmptyView(final  int[] pos) {
        final ImageView img = new ImageView(context);
        img.setBackgroundResource(R.drawable.empty);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
        img.setLayoutParams(lp);
        img.setTag(new Piece("null", new int[]{pos[0], pos[1]}));

        img.setOnDragListener((v, e) -> {
            if (e.getAction() == DragEvent.ACTION_DROP) {
                ImageView imgView = (ImageView) e.getLocalState();

                int[] posNew = ((Piece) v.getTag()).getPos();
                int[] posOld = ((Piece) imgView.getTag()).getPos();
                spAct.resetCastling();

                //check khong thay doi vi tri
                boolean flag = false;
                for (int i = 0; i < moveAble.size(); i++) {
                    if (Arrays.equals(posNew, moveAble.get(i))) {
                        flag = true;
                        break;
                    }
                }

                Log.v("DROP EMPTY", posNew[0] + " " + posNew[1]+" "+flag);
                if (flag) {
                    //an tot qua duong
                    if (Arrays.equals(spAct.getTotQuaDuongAt(), posNew)) {
                        int[] temp = spAct.getCurrentLastMovePiecePos();
                        if (board.pieces[temp[0]][temp[1]].getTag().toString().contains("black")) {
                            whiteTeam.kill.add(board.pieces[temp[0]][temp[1]]);
                            blackTeam.alive.remove(board.pieces[temp[0]][temp[1]]);
                        } else {
                            blackTeam.kill.add(board.pieces[temp[0]][temp[1]]);
                            whiteTeam.alive.remove(board.pieces[temp[0]][temp[1]]);
                        }
                        spAct.setLastKillPos(temp);

                        ImageView imgTemp = createEmptyView(temp);
                        board.pieces[temp[0]][temp[1]] = imgTemp;

                        spAct.setTotQuaDuongAt(new int[]{-1, -1});
                    }else{
                        spAct.setLastKillPos(new int[]{-1, -1});
                    }
                    spAct.setLastPieceMove(board.pieces[posOld[0]][posOld[1]].getTag().toString());

                    //nhap thanh
                    for (int i = 0; i < posCastling.size(); i++) {
                        int[] posNhap = posCastling.get(i);
                        if (Arrays.equals(posNhap, posNew)) {
                            ImageView rook = new ImageView(context);
                            int[] posSwap = new int[]{posOld[0], -1};
                            //left
                            if (posNhap[1] - posOld[1] < 0) {
                                rook = board.pieces[posOld[0]][0];
                                posSwap[1] = posNew[1] + 1;
                            }
                            //right
                            else {
                                rook = board.pieces[posOld[0]][7];
                                posSwap[1] = posNew[1] - 1;
                            }

                            //swap xe
                            Piece p = (Piece) rook.getTag();
                            p.setMoved();
                            rook.setTag(p);
                            int[] rookPos = ((Piece) rook.getTag()).getPos();

                            board.swap(posSwap, rookPos);

                            spAct.castlingNewPos = posSwap;
                            spAct.castlingOldPos = rookPos;

                            break;
                        }
                    }

                    //phong tốt khi tốt đi vào hàng 0 hoặc 7
                    //else reset value đè phòng nó đã phogn tốt nhưng vẫn bị stuck
                    if ((posNew[0] == 0 || posNew[0] == 7) && imgView.getTag().toString().contains("pawn")) {
                        Log.v("Pawn evolve", posNew[0] + ":" + posNew[1]);
                        createPopupPawnEvolve(imgView);
                    }else{pawnEvolveTo = " ";}

                    Log.v("LAST PIECE MOVED", spAct.getLastPieceMove());
                    spAct.setLastPiecePos(posOld);
                    spAct.setCurrentLastMovePiecePos(posNew);

                    Piece p = ((Piece) board.pieces[posOld[0]][posOld[1]].getTag());
                    p.setMoved();
                    board.pieces[posOld[0]][posOld[1]].setTag(p);

                    board.swap(posNew, posOld);

                    isCheckMate = isCheckMate(board.pieces[posNew[0]][posNew[1]]);

                    reDraw();
                } else {

                    pawnEvolveTo = " ";
                    if (!isCheckMate)
                        gridEffect.removeAllViewsInLayout();
                }
            }
            return true;
        });

        return img;
    }

    private void setupBoard() {
        int[] blackOrder = new int[]{R.drawable.black_rook, R.drawable.black_knight, R.drawable.black_bishop, R.drawable.black_queen, R.drawable.black_king,
                R.drawable.black_bishop, R.drawable.black_knight, R.drawable.black_rook};
        int[] whiteOrder = new int[]{R.drawable.white_rook, R.drawable.white_knight, R.drawable.white_bishop, R.drawable.white_queen, R.drawable.white_king,
                R.drawable.white_bishop, R.drawable.white_knight, R.drawable.white_rook};

        String[] nameOrder = new String[]{"rook", "knight", "bishop", "queen", "king", "bishop", "knight", "rook"};

        if(!isWhite) {
            blackOrder = new int[]{R.drawable.black_rook, R.drawable.black_knight, R.drawable.black_bishop, R.drawable.black_king, R.drawable.black_queen,
                    R.drawable.black_bishop, R.drawable.black_knight, R.drawable.black_rook};
            whiteOrder = new int[]{R.drawable.white_rook, R.drawable.white_knight, R.drawable.white_bishop, R.drawable.white_king, R.drawable.white_queen,
                    R.drawable.white_bishop, R.drawable.white_knight, R.drawable.white_rook};

            nameOrder = new String[]{"rook", "knight", "bishop", "king", "queen", "bishop", "knight", "rook"};
        }
        int[] player = isWhite ? new int[]{0, 1, 6, 7}: new int[]{7,6,1,0};

        for (int i = 0; i < 8; i++) {
            //white
            ImageView imgWhite = new ImageView(this);
            imgWhite.setBackgroundResource(whiteOrder[i]);
            imgWhite.setTag(new Piece(nameOrder[i] + " white", new int[]{player[3], i} ));
            imgWhite = addCommonEvent(imgWhite);
            board.pieces[player[3]][i] = imgWhite;

            ImageView imgPawnWhite = new ImageView(this);
            imgPawnWhite.setBackgroundResource(R.drawable.white_pawn);
            imgPawnWhite.setTag(new Piece("pawn white", new int[]{player[2], i} ));
            imgPawnWhite = addCommonEvent(imgPawnWhite);
            board.pieces[player[2]][i] = imgPawnWhite;

            if(imgWhite.getTag().toString().contains("king"))
                whiteTeam.alive.add(0, imgWhite);
            else
                whiteTeam.alive.add(imgWhite);
            whiteTeam.alive.add(imgPawnWhite);

            //black
            ImageView imgBlack = new ImageView(this);
            imgBlack.setBackgroundResource(blackOrder[i]);
            imgBlack.setTag(new Piece(nameOrder[i] + " black", new int[]{player[0], i} ));
            imgBlack = addCommonEvent(imgBlack);
            board.pieces[player[0]][i] = imgBlack;

            ImageView imgPawnBlack = new ImageView(this);
            imgPawnBlack.setBackgroundResource(R.drawable.black_pawn);
            imgPawnBlack.setTag(new Piece("pawn black", new int[]{player[1], i} ));
            imgPawnBlack = addCommonEvent(imgPawnBlack);
            board.pieces[player[1]][i] = imgPawnBlack;

            if(imgBlack.getTag().toString().contains("king"))
                blackTeam.alive.add(0, imgBlack);
            else
                blackTeam.alive.add(imgBlack);
            blackTeam.alive.add(imgPawnBlack);
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
//                imgView.setBackgroundResource(R.drawable.rec_black);

                if ((i + j) % 2 == 1)
                    imgView.setBackgroundColor(getResources().getColor(R.color.black));
                else
                    imgView.setBackgroundColor(getResources().getColor(R.color.white));

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);

                imgView.setLayoutParams(lp);
                board.layout[i][j] = imgView;

                ImageView img = createEmptyView(new int[]{i, j});
                board.pieces[i][j] = img;

                board.effect[i][j] = createEmptyView(new int[]{-1, -1});

                gridEffect.addView(board.effect[i][j]);
                gridBoard.addView(imgView);
                gridChess.addView(img);
            }
        }
    }

    public static  void reDraw() {
        //redraw pieces layer
        if (!isKingCheckMove) {
            moveAble = new ArrayList<>();
            Log.v("isCheckMate", isCheckMate+"");
            addEffectMoveAble();
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
            Log.v("Is end game", isWin+"");

            //nếu k đợi đối phương di chuyển
            //nếu k vị trí mới không = vị trí củ
            //nếu k đang chọn thăng cấp
            if(!isWaitingMove && !Arrays.equals(spAct.lastPiecePos, spAct.currentLastMovePiecePos) && !isEvolve){
                isWin = checkWin();
                if(isWin != 0) {
                    thrCheckMoved.interrupt();
                    endGameNotify();
                }
                isWaitingMove = true;
                numMove++;
                try {
                    new PostSendMove().execute().get();
                }
                catch (Exception e){
                    //
                }
            }
        }
    }

    public static  void reDrawEffect(){
        //redraw effect layer
        gridEffect.removeAllViewsInLayout();
        String[] partV = spAct.lastPieceMove.split(" ");
        int[] kingPos = new int[]{-1,-1};
        if(isCheckMate) {
            Team t = new Team(blackTeam);
            if(!partV[1].equals("white"))
                t = new Team(whiteTeam);
            kingPos = ((Piece)t.alive.get(0).getTag()).getPos();
            board.effect[kingPos[0]][kingPos[1]] = createEmptyView(new int[]{-1, -1});
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
                            gridEffect.addView(createEmptyView(new int[]{-1, -1}));
                    }
                }
            }
        }
        Log.v("Add effect", "Added");
    }

    private static boolean isPosEmpty(int[] pos) {
        if (Math.min(pos[0], pos[1]) >= 0 && Math.max(pos[0], pos[1]) < 8)
            return(board.pieces[pos[0]][pos[1]].getTag().toString().equals("null"));
        return false;
    }

    public static ArrayList<int[]> getMoveAble(View v) {
        if(!isKingCheckMove)
            posCastling = new ArrayList<>();
        ArrayList<int[]> moveAble_temp = new ArrayList<>();
        int[] pos = ((Piece)v.getTag()).getPos();
        int move = v.getTag().toString().contains("white") ? 1 : -1;
        if(!isWhite)
            move*=-1;
        String[] partsV = v.getTag().toString().split(" ");

        if (v.getTag().toString().contains("pawn")) {
            //isKingCheckMove để khi được kiểm tra nước đi các nước đi tấn công của đôi phương
            //==>> loại 2 bước đi thẳng của tốt
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
                    if (spAct.lastPiecePos[1] - pos[1] == -move) {
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
            //kiểm tra từ vị trí của king
            //sang trái/phải xem tất cả vị trí có empty
            // và rook + king chưa di chuyển
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
                            posCastling.add(new int[]{pos[0], pos[1] - 2});
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
                            posCastling.add(new int[]{pos[0], pos[1] + 2});
                        } else
                            break;
                    }
                }
            }
        }
        //loại bỏ các nước đi tự sát
        if(!isKingCheckMove) {
            try {
                //bật chặn để khi recall lạihàm này sẽ không chạy lại cái này
                isKingCheckMove = true;
                ArrayList<int[]> posCanMove = new ArrayList<>();
                //với mỗi vị trí có thể đi kiểm tra chiếu vua
                for (int i = 0; i < moveAble_temp.size(); i++) {
                    int[] temp = moveAble_temp.get(i);
                    if (Math.min(temp[0], temp[1]) >= 0 && Math.max(temp[0], temp[1]) < 8)
                        //nếu vị trí đi không làm vua bị chiếu thì sẽ thêm vào bước đi có thể
                        if (!isSuicide(v, moveAble_temp.get(i).clone())) {
                            posCanMove.add(moveAble_temp.get(i).clone());
                        }
                }


                return posCanMove;
            }catch (Exception ex){ Log.v("Error sth 1", ex.getMessage()+""); }
            finally {
                isKingCheckMove = false;
            }
        }
        return moveAble_temp;
    }

    private static ArrayList<int[]> moveLine(View v) {
//        int[] pos = getPositionArr(v);
        int[] pos = ((Piece)v.getTag()).getPos();
        String[] partsV = v.getTag().toString().split(" ");
        int[] math = new int[]{-1, -1, 1, 1};
        ArrayList<int[]> temp = new ArrayList<>();
        //chạy theo 4 hướng dọc và ngang
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

    private static ArrayList<int[]> moveXeo(View v) {
//        int[] pos = getPositionArr(v);
        int[] pos = ((Piece)v.getTag()).getPos();
        String[] partsV = v.getTag().toString().split(" ");
        int[] posX = new int[]{1, 1, -1, -1};
        int[] posY = new int[]{1, -1, 1, -1};
        ArrayList<int[]> temp = new ArrayList<>();
        //chạy theo 4 hướng xéo mỗi lần
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

    private static boolean isSuicide( View v, int[] nextPos){
        String[] partV = v.getTag().toString().split(" ");
        int[] currentPos = ((Piece)v.getTag()).getPos();
        //lấy team cùng với con cờ đang kiểmtra
        Team team = blackTeam.clone();
        if(partV[1].equals("white")) {
            team = whiteTeam.clone();
        }

        //simulating
        //thử hoán đổi vị trí hiện tại với vị trí mới
        ImageView imgNextPos = board.pieces[nextPos[0]][nextPos[1]];
        Piece p = (Piece)imgNextPos.getTag();
        p.setPos(((Piece)board.pieces[currentPos[0]][currentPos[1]].getTag()).getPos());

        ImageView emptyImg = createEmptyView(nextPos);
        board.pieces[nextPos[0]][nextPos[1]] = emptyImg;
        board.swap(currentPos, nextPos);

        Team currentOpponent = whiteTeam.clone();
        if(partV[1].equals("white"))
            currentOpponent = blackTeam.clone();

        //find current king pos
        int[] kingPos = ((Piece)team.alive.get(0).getTag()).getPos();
        if(v.getTag().toString().contains("king")){
            kingPos = ((Piece)v.getTag()).getPos();
        }

        try{
            //kiếm tra từng quân bên đối phương, nếu có thể chiếu vua thì trả về false
            currentOpponent.alive.remove(imgNextPos);
            for(int i = 0; i < currentOpponent.alive.size(); i++){
                ArrayList<int[]> temp = getMoveAble(currentOpponent.alive.get(i));
                for(int j = 0; j < temp.size(); j++){
                    int[] pos = temp.get(j).clone();
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
            //hoán đổi lại vị trí ban đầu
            imgNextPos.setTag(p);
            board.pieces[currentPos[0]][currentPos[1]] = imgNextPos;
            board.swap(currentPos, nextPos);
        }

        return false;
    }

    public static boolean isCheckMate(View v){
        String[] partV = v.getTag().toString().split(" ");
        //team - opponent
        Team t = new Team(blackTeam);
        Team o = new Team(whiteTeam);
        if(partV[1].equals("white")) {
            t = new Team(whiteTeam);
            o = new Team(blackTeam);
        }
        try {
            for (int i = 0; i < t.alive.size(); i++) {
                BoardActivity.isKingCheckMove = true;

                ArrayList<int[]> move =  BoardActivity.getMoveAble(t.alive.get(i));
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

    private static int checkWin(){
        //team
        Team t = new Team(blackTeam);
        //opponent
        Team o = new Team(whiteTeam);
        if(isWhite) {
            t = new Team(whiteTeam);
            o = new Team(blackTeam);
        }
        //kiểm tra king 2 team còn sống hay không
        if(!((Piece)t.alive.get(0).getTag()).name.contains("king"))
            return -1;
        if(!((Piece)o.alive.get(0).getTag()).name.contains("king"))
            return 1;
        //khi check mate
        if(isCheckMate){
            boolean flag = true;
            //kiểm tra team còn nước đi
            for(int i = 0; i < t.alive.size(); i++){
                if(getMoveAble(t.alive.get(i)).size() != 0) {
                    flag = false;
                    break;
                }
            }
            if(flag)
                return -1;
            flag = true;
            //kiểm tra đối phương còn nước đi
            for(int i = 0; i < o.alive.size(); i++){
                if(getMoveAble(o.alive.get(i)).size() != 0) {
                    flag = false;
                    break;
                }
            }
            if(flag)
                return 1;
        }else{
            //hoà TH1:
            //đối phương còn vua và k còn nước đi
            if(o.alive.size() == 1 && o.alive.get(0).getTag().toString().contains("king"))
                if(getMoveAble(o.alive.get(0)).size() == 0)
                    return 2;

            //hoà TH2:
            //cả 2 còn vua
            if(o.alive.size() == 1 && t.alive.size() == 1)
                return 2;
        }
        return 0;
    }

    public static void addEffectMoveAble(){
        //reset effect array
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                board.effect[i][j] = createEmptyView(new int[]{-1, -1});
            }
        }
        for(int i = 0; i < moveAble.size(); i++){
            int[] pos = moveAble.get(i);
            //set màu ở lớp effect những ô có thể đi

            board.effect[pos[0]][pos[1]].setBackgroundColor(context.getResources().getColor(R.color.moveAble));
        }
    }

    private static void addEffectCheckMate(){
        //reset effect array
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                board.effect[i][j] = createEmptyView(new int[]{-1, -1});
            }
        }

        String[] partLast = spAct.lastPieceMove.split(" ");
        int[] kingPos = ((Piece) blackTeam.alive.get(0).getTag()).getPos();
        if(partLast[1].equals("black")){
            kingPos = ((Piece) whiteTeam.alive.get(0).getTag()).getPos();
        }
        //add effect màu chiếu khi vua bị chiếu
        board.effect[kingPos[0]][kingPos[1]].setBackgroundColor(context.getResources().getColor(R.color.checkmate));
        Log.v("add check mate effect", "added");
    }

    public static  void endGameNotify(){
        thrCheckMoved.interrupt();

        if(isWin == 1)
            Toast.makeText(context, "You win", Toast.LENGTH_SHORT).show();
        else if(isWin == -1)
            Toast.makeText(context, "You lose", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "Draw", Toast.LENGTH_SHORT).show();
    }

//    @Override
//    public void onPause(){
//        super.onPause();
//        thrCheckMoved.interrupt();
//    }
//
//    @Override
//    public void onResume(){
//        super.onResume();
//        thrCheckMoved.start();
//    }
}