package com.example.chess;

public class Piece {
    public String name;
    public int[] pos;

    public boolean isMoved() {
        return isMoved;
    }

    public void setMoved() {
        isMoved = true;
    }

    public boolean isMoved;

    public Piece(String name, int[] pos){
        this.name = name; this.pos = pos.clone();
    }

    public void setPos(int[] pos) { this.pos = pos.clone(); }
    public int[] getPos() { return this.pos; }

    @Override
    public String toString() {
        return this.name;
    }
}