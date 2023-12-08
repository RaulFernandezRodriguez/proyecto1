import java.util.ArrayList;

public class Groups{
    private ArrayList members;
    private int x;
    private int y;

    public Groups(ArrayList members, int x, int y){
        this.members = members;
        this.x = x;
        this.y = y;
    }

    public void addToGroup(Ficha ficha){
        this.members.add(ficha);
    }

    public int getGroupStartingRow(){
        return x;
    }

    public int getGroupStartingCol(){
        return y;
    }

    public int getGroupLength(){
        return this.members.size();
    }
}