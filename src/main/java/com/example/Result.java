package com.example;

import java.util.List;

public class Result {
    private int points;
    private List<char[]> moves;

    public Result(int points, List<char[]> moves){
        this.points = points;
        this.moves = moves;
    }

    public int getPoints(){
        return points;
    }

    public List<char[]> getMoves(){
        return moves;
    }
}
