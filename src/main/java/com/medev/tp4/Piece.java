/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.medev.tp4;
import java.io.Serializable;


/**
 *
 * @author yasminebouhadji
 */
public class Piece implements Serializable {
    
    private final Color color;
    private boolean isQueen;

    public Piece(Color color) {
        this.color = color;
        this.isQueen = false;
    }

    public Color getColor() {
        return color;
    }

    public boolean isQueen() {
        return isQueen;
    }

    public void promote() {
        this.isQueen = true;
    }

    @Override
    public String toString() {
        if (isQueen) {
            return color == Color.WHITE ? "W" : "B";
        } else {
            return color == Color.WHITE ? "w" : "b";
        }
    }
}
