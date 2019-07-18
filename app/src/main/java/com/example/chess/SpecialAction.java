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

    public SpecialAction(){
        lastPieceMove = "";
        lastPiecePos = new int[]{-1, -1};
        currentLastMovePiecePos = new int[]{-1, -1};
        totQuaDuongAt = new int[]{-1, -1};
    }
}
