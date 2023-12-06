package com.example;

public class Result {
    private int points;
    private int x, y;
    private char color;
    private int groupLength;

    public Result(int points, int x, int y, char color, int groupLength){
        this.points = points;
        this.x = x;
        this.y = y;
        this.color = color;
        this.groupLength = groupLength;
    }

    public int getPoints(){
        return points;
    }

    public void setPoints(int points){
        this.points = points;
    }

    public int getXPosition(){
        return x;
    }

    public int getYPosition(){
        return y;
    }

    public char getGroupColor(){
        return color;
    }

    public int getGroupLength(){
        return groupLength;
    }
}
