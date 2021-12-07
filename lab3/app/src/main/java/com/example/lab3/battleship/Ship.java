package com.example.lab3.battleship;

import com.example.lab3.battleship.exceptions.ShipException;

import java.util.ArrayList;
import java.util.List;

public class Ship {
    public enum Orientation {
        HORIZONTAL,
        VERTICAL;

        public Orientation rotate() {
            switch (this) {
                case HORIZONTAL:
                    return VERTICAL;
                case VERTICAL:
                    return HORIZONTAL;
            }
            return null;
        }
    }

    private final Orientation orientation;
    private final int xStart, y;
    private final int size;
    private boolean[] damaged;

    public Ship(int size, Orientation orientation, int xStart, int y) throws ShipException {
        if (size <= 0 || size > GameSetup.maxShipSize ||
                y < 0 || y >= GameSetup.boardSize ||
                xStart < 0 || xStart + size - 1 >= GameSetup.boardSize) {
            throw new ShipException();
        }

        this.size = size;
        this.orientation = orientation;
        this.y = y;
        this.xStart = xStart;
        damaged = new boolean[size];
        for (int i = 0; i < size; i++) {
            damaged[i] = false;
        }
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public int getY() {
        return y;
    }

    public int getxStart() {
        return xStart;
    }

    public int getSize() {
        return size;
    }

    public List<Coord> getCoords() {
        boolean isHorizontal = orientation == Ship.Orientation.HORIZONTAL;
        int x, y;
        List<Coord> res = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            x = isHorizontal ? getxStart() + i : getY();
            y = isHorizontal ? getY() : getxStart() + i;
            res.add(new Coord(x, y));
        }
        return res;
    }

    public boolean isPlacedOn(int x, int y) {
        if (orientation == Orientation.VERTICAL) {
            int tmp = y;
            y = x;
            x = tmp;
        }
        return this.y == y && x >= xStart && x <= xStart + size - 1;
    }

    public boolean shoot(int x, int y) {
        if (isPlacedOn(x, y)) {
            int dist = orientation == Orientation.HORIZONTAL ? x - xStart : y - xStart;
            if (!damaged[dist]) {
                damaged[dist] = true;
                return true;
            }
        }
        return false;
    }

    public boolean isDead() {
        for (int i = 0; i < size; i++) {
            if (!damaged[i]) {
                return false;
            }
        }
        return true;
    }
}
