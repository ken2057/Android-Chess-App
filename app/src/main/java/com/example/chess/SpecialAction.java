package com.example.chess;

public class SpecialAction {
    public static String getLastPieceMove() {
        return lastPieceMove;
    }

    public static void setLastPieceMove(String lastPieceMove) {
        SpecialAction.lastPieceMove = lastPieceMove;
    }

    public static int[] getLastPiecePos() {
        return lastPiecePos;
    }

    public static void setLastPiecePos(int[] lastPiecePos) {
        SpecialAction.lastPiecePos = lastPiecePos;
    }

    public static int[] getCurrentLastMovePiecePos() {
        return currentLastMovePiecePos;
    }

    public static void setCurrentLastMovePiecePos(int[] currentLastMovePiecePos) {
        SpecialAction.currentLastMovePiecePos = currentLastMovePiecePos;
    }

    public static int[] getTotQuaDuongAt() {
        return totQuaDuongAt;
    }

    public static void setTotQuaDuongAt(int[] totQuaDuongAt) {
        SpecialAction.totQuaDuongAt = totQuaDuongAt;
    }

    public static int[] getCastlingOldPos() {
        return castlingOldPos;
    }

    public static void setCastlingOldPos(int[] castlingOldPos) {
        SpecialAction.castlingOldPos = castlingOldPos;
    }

    public static int[] getCastlingNewPos() {
        return castlingNewPos;
    }

    public static void setCastlingNewPos(int[] castlingNewPos) {
        SpecialAction.castlingNewPos = castlingNewPos;
    }

    public static int[] getLastKillPos() {
        return lastKillPos;
    }

    public static void setLastKillPos(int[] lastKillPos) {
        SpecialAction.lastKillPos = lastKillPos;
    }

    public static String lastPieceMove;
    public static int[] lastPiecePos;
    public static int[] currentLastMovePiecePos;
    public static int[] totQuaDuongAt;
    public static int[] castlingOldPos;
    public static int[] castlingNewPos;
    public static int[] lastKillPos;

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
