package com.example.lab3.battleship.exceptions;

public class ShipPlacingException extends Exception {
    public ShipPlacingException() {
        super("failed ship placing (either out of border or ship overlay)");
    }
}
