package com.example.chess;

public class Piece {
    public String name;

    public Piece(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}