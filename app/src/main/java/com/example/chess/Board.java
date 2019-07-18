package com.example.chess;

import android.util.Log;
import android.widget.ImageView;

public class Board {
    ImageView[][] layout;
    ImageView[][] pieces;

    public Board(){
        layout = new ImageView[8][8];
        pieces = new ImageView[8][8];
    }

    public Board(Board b){
        this.layout = b.layout.clone();
        pieces = clonePieces(b.pieces);
    }

    public void swap(int[] currentPos, int[] newPos) {
        Log.v("swap", currentPos[0]+" "+currentPos[1]+" - "+newPos[0]+" "+newPos[1]);
        Piece tempPiece = (Piece)this.pieces[currentPos[0]][currentPos[1]].getTag();
        tempPiece.setPos(newPos);

        Piece tempPiece2 = (Piece)this.pieces[newPos[0]][newPos[1]].getTag();
        tempPiece2.setPos(currentPos);

        ImageView temp = this.pieces[currentPos[0]][currentPos[1]];

        this.pieces[currentPos[0]][currentPos[1]] = this.pieces[newPos[0]][newPos[1]];
        this.pieces[newPos[0]][newPos[1]] = temp;
    }

    private ImageView[][] clonePieces(ImageView[][] v){
        ImageView[][] newPieces = new ImageView[8][8];
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++){
                newPieces[i][j] = v[i][j];
            }
        return newPieces;
    }

}
