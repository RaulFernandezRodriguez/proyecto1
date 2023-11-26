package com.example;

import java.util.ArrayList;

public class movesTree {
    private movesTree padre;
    private Ficha[][] estado;
    private ArrayList<movesTree> hijos;
    private Result puntuacion;

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

    public Result getData(){
        return puntuacion;
    }

    public void addChild(movesTree hijo) {
        if (hijos == null) {
            hijos = new ArrayList<>();
        }
        hijos.add(hijo);
    }

    public ArrayList<movesTree> getChilds() {
        return hijos;
    }

    public Ficha[][] getBoard(){
        return estado;
    }
}
