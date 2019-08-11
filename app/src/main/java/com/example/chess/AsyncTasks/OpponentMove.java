package com.example.chess.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.chess.BoardActivity;
import com.example.chess.Piece;
import com.example.chess.R;

public class OpponentMove extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... result) {
        String[] pos = result[0].split("-");
        Log.v("checking", result[0]);
        int[] posOld = new int[]{Integer.parseInt(pos[0].charAt(0) + ""), Integer.parseInt(pos[0].charAt(1) + "")};
        int[] posNew = new int[]{Integer.parseInt(pos[1].charAt(0) + ""), Integer.parseInt(pos[1].charAt(1) + "")};
        int[] posKill = new int[]{-1, -1};
        int[] posCastlingNew = new int[]{-1, -1};
        int[] posCastlingOld = new int[]{-1, -1};

        if(!(pos[0].equals(pos[1]) && pos[0].equals("00"))) {
            if (!pos[2].contains(" ")) {
                posKill = new int[]{Integer.parseInt(pos[2].charAt(0) + ""), Integer.parseInt(pos[2].charAt(1) + "")};
            }
            if (!pos[3].contains(" ")) {
                posCastlingNew = new int[]{Integer.parseInt(pos[3].charAt(2) + ""), Integer.parseInt(pos[3].charAt(3) + "")};
                posCastlingOld = new int[]{Integer.parseInt(pos[3].charAt(0) + ""), Integer.parseInt(pos[3].charAt(1) + "")};
                BoardActivity.board.swap(posCastlingNew, posCastlingOld);
            }

            if (!pos[2].contains(" ")) {
                ImageView v = BoardActivity.board.pieces[posKill[0]][posKill[1]];
                ImageView empV = BoardActivity.createEmptyView(posKill);

                if (BoardActivity.isWhite) {
                    BoardActivity.whiteTeam.alive.remove(v);
                    BoardActivity.blackTeam.kill.add(v);
                } else {
                    BoardActivity.blackTeam.alive.remove(v);
                    BoardActivity.whiteTeam.kill.add(v);
                }

                BoardActivity.board.pieces[posKill[0]][posKill[1]] = empV;
            }

            BoardActivity.board.swap(posOld, posNew);
        }

        return result[0];
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        String[] pos = result.split("-");

        int[] posNew = new int[]{ Integer.parseInt(pos[1].charAt(0)+""), Integer.parseInt(pos[1].charAt(1)+"")};

        if(!(pos[0].equals(pos[1]) && pos[0].equals("00"))) {
            BoardActivity.spAct.setLastPieceMove(BoardActivity.board.pieces[posNew[0]][posNew[1]].getTag().toString());
            BoardActivity.spAct.setLastPiecePos(new int[]{Integer.parseInt(pos[0].charAt(0) + ""), Integer.parseInt(pos[0].charAt(1) + "")});
            BoardActivity.spAct.setCurrentLastMovePiecePos(new int[]{Integer.parseInt(pos[1].charAt(0) + ""), Integer.parseInt(pos[1].charAt(1) + "")});
            //đối phương phong tốt
            if (!pos[4].equals(" ")) {
                Piece p = (Piece) BoardActivity.board.pieces[posNew[0]][posNew[1]].getTag();
                String[] partV = p.name.split(" ");
                switch (pos[4]) {
                    case "k":
                        p.name = "knight " + partV[1];
                        if (partV[1].equals("white"))
                            BoardActivity.board.pieces[posNew[0]][posNew[1]].setBackgroundResource(R.drawable.white_knight);
                        else
                            BoardActivity.board.pieces[posNew[0]][posNew[1]].setBackgroundResource(R.drawable.black_knight);
                        break;
                    case "b":
                        p.name = "bishop " + partV[1];
                        if (partV[1].equals("white"))
                            BoardActivity.board.pieces[posNew[0]][posNew[1]].setBackgroundResource(R.drawable.white_bishop);
                        else
                            BoardActivity.board.pieces[posNew[0]][posNew[1]].setBackgroundResource(R.drawable.black_bishop);
                        break;
                    case "q":
                        p.name = "queen " + partV[1];
                        if (partV[1].equals("white"))
                            BoardActivity.board.pieces[posNew[0]][posNew[1]].setBackgroundResource(R.drawable.white_queen);
                        else
                            BoardActivity.board.pieces[posNew[0]][posNew[1]].setBackgroundResource(R.drawable.black_queen);
                        break;
                    case "r":
                        p.name = "rook " + partV[1];
                        if (partV[1].equals("white"))
                            BoardActivity.board.pieces[posNew[0]][posNew[1]].setBackgroundResource(R.drawable.white_rook);
                        else
                            BoardActivity.board.pieces[posNew[0]][posNew[1]].setBackgroundResource(R.drawable.black_rook);
                        break;
                }
                BoardActivity.board.pieces[posNew[0]][posNew[1]].setTag(p);
            }

            BoardActivity.isCheckMate = BoardActivity.isCheckMate(BoardActivity.board.pieces[posNew[0]][posNew[1]]);
            BoardActivity.reDraw();
            BoardActivity.reDrawEffect();
        }
        //check đã win hay thua
        //nếu đã end kill thread
        if(!pos[5].equals(" ")) {
            switch (pos[5]) {
                case "W":
                    BoardActivity.isWin = BoardActivity.isWhite ? 1 : -1;
                    break;
                case "B":
                    BoardActivity.isWin = BoardActivity.isWhite ? -1 : 1;
                    break;
                case "D":
                    BoardActivity.isWin = 2;
                    break;
                default:
                    break;
            }
            BoardActivity.endGameNotify();
        }



        BoardActivity.isWaitingMove = false;
        BoardActivity.isCheckMoved = false;
    }
}