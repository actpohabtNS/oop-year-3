package com.example.lab3.battleship.exceptions;

public class ShipException extends Exception {
    public ShipException() {
        super("incorrect ship parameters");
    }
}
