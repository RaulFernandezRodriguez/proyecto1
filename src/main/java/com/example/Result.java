package com.example;

import java.util.List;

public class Result {
    private int points;
    private List<char[]> moves;
    private char color;
    private int groupLength;

    public Result(int points, List<char[]> moves, char color, int groupLength){
        this.points = points;
        this.moves = moves;
        this.color = color;
        this.groupLength = groupLength;
    }

    public int getPoints(){
        return points;
    }

    public List<char[]> getMoves(){
        return moves;
    }

    public char getGroupColor(){
        return color;
    }

    public int getGroupLength(){
        return groupLength;
    }
}
