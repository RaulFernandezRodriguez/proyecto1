package com.example;

import java.util.ArrayList;

public class movesTree {
    movesTree padre;
    Ficha[][] estado;
    ArrayList<Ficha[][]> hijos;
    Result puntuacion;

    public movesTree(Ficha[][] estado, Result puntuacion, movesTree padre){
        this.estado = estado;
        this.hijos = new ArrayList<>();
        this.puntuacion = puntuacion;
        this.padre = padre;
    }

    public movesTree(Ficha[][] estado){
        this.estado = estado;
        this.hijos = null;
        this.puntuacion = null;
        this.padre = null;
    }

    public ArrayList<Ficha[][]> getChilds(){
        return hijos;
    }

    public Result getPoints(){
        return puntuacion;
    }
}
