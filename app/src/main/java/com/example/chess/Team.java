package com.example.chess;

import android.widget.ImageView;

import java.util.ArrayList;

public class Team {
    public ArrayList<ImageView> getAlive() {
        return alive;
    }

    public void setAlive(ArrayList<ImageView> alive) {
        this.alive = alive;
    }

    public ArrayList<ImageView> getKill() {
        return kill;
    }

    public void setKill(ArrayList<ImageView> kill) {
        this.kill = kill;
    }

    ArrayList<ImageView> alive;
    ArrayList<ImageView> kill;

    public Team(Team t) {
        this.alive = new ArrayList<>(t.alive);
        this.kill = new ArrayList<>(t.kill);
    }

    public Team(){
        this.alive = new ArrayList<>();
        this.kill = new ArrayList<>();
    }

    public Team clone(){
        Team t = new Team();
        t.setAlive(this.alive);
        t.setKill(this.kill);
        return t;
    }
}
