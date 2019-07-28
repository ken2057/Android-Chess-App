package com.example.chess;

public class SpecialAction {
    public String getLastPieceMove() {
        return lastPieceMove;
    }

    public void setLastPieceMove(String lastPieceMove) {
        this.lastPieceMove = lastPieceMove;
    }

    public int[] getLastPiecePos() {
        return lastPiecePos;
    }

    public void setLastPiecePos(int[] lastPiecePos) {
        this.lastPiecePos = lastPiecePos;
    }

    public int[] getCurrentLastMovePiecePos() {
        return currentLastMovePiecePos;
    }

    public void setCurrentLastMovePiecePos(int[] currentLastMovePiecePos) {
        this.currentLastMovePiecePos = currentLastMovePiecePos;
    }

    public int[] getTotQuaDuongAt() {
        return totQuaDuongAt;
    }

    public void setTotQuaDuongAt(int[] totQuaDuongAt) {
        this.totQuaDuongAt = totQuaDuongAt;
    }

    String lastPieceMove;
    int[] lastPiecePos;
    int[] currentLastMovePiecePos;
    int[] totQuaDuongAt;
    int[] castlingOldPos;
    int[] castlingNewPos;

    public int[] getLastKillPos() {
        return lastKillPos;
    }

    public void setLastKillPos(int[] lastKillPos) {
        this.lastKillPos = lastKillPos;
    }

    int[] lastKillPos;

    public SpecialAction(){
        lastPieceMove = "";
        lastPiecePos = new int[]{-1, -1};
        currentLastMovePiecePos = new int[]{-1, -1};
        totQuaDuongAt = new int[]{-1, -1};
        lastKillPos = new int[]{-1, -1};
        castlingOldPos = new int[]{-1, -1};
        castlingNewPos = new int[]{-1, -1};
    }

    public void resetCastling(){
        castlingOldPos = new int[]{-1, -1};
        castlingNewPos = new int[]{-1, -1};
    }
}
