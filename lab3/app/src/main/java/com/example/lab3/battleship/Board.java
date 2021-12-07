package com.example.lab3.battleship;

import android.util.Log;

import com.example.lab3.battleship.exceptions.ShipException;
import com.example.lab3.battleship.exceptions.ShipPlacingException;

import java.util.ArrayList;
import java.util.List;
import java.security.SecureRandom;

public class Board {
    public enum CellState {
        EMPTY(false),
        SHIP(false),
        SHOT_EMPTY(true),
        DAMAGED_SHIP(true),
        DEAD_SHIP(true);

        private final boolean isShot;

        CellState(boolean isShot) {
            this.isShot = isShot;
        }

        public boolean isShot() {
            return isShot;
        }
    }

    public enum ShotResult {
        MISS,
        IN_TARGET,
        DESTROYED,
        FAILED
    }

    private static final SecureRandom random = new SecureRandom();
    private final CellState[][] cells;
    private final List<Ship> ships;
    private final int size;

    public Board() {
        size = GameSetup.boardSize;
        cells = new CellState[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j] = CellState.EMPTY;
            }
        }

        ships = new ArrayList<>();
        randomizeShips();
    }

    public void randomizeShips() {
        for (int shipSize = GameSetup.maxShipSize; shipSize >= 1; shipSize--) {
            for (int shipCount = 0; shipCount < GameSetup.maxShipSize - shipSize + 1; shipCount++) {
                try {
                    Ship.Orientation orientation = random.nextBoolean() ? Ship.Orientation.HORIZONTAL : Ship.Orientation.VERTICAL;
                    Ship ship = new Ship(shipSize, orientation, random.nextInt(size), random.nextInt(size));
                    this.placeShip(ship);
                } catch (ShipPlacingException | ShipException e) {
                    shipCount--;
                }
            }
        }
    }

    public List<Ship> getShips() {
        return ships;
    }

    public Ship shipAt(int x, int y) {
        Ship ship = null;
        for (Ship sh : ships) {
            if (sh.isPlacedOn(x, y)) {
                ship = sh;
                break;
            }
        }
        return ship;
    }

    public CellState getCell(int x, int y) {
        if (!coordsInBounds(x, y)) {
            return null;
        }
        return cells[x][y];
    }

    public ShotResult shoot(int x, int y) {
        if (x < 0 || x >= GameSetup.boardSize || y < 0 || y >= GameSetup.boardSize || cells[x][y].isShot()) {
            return ShotResult.FAILED;
        }

        for (Ship ship : ships) {
            if (ship.shoot(x, y)) {
                cells[x][y] = CellState.DAMAGED_SHIP;
                if (ship.isDead()) {
                    markWatchedAroundShip(ship);
                    killShip(ship);
                    return ShotResult.DESTROYED;
                } else {
                    return ShotResult.IN_TARGET;
                }
            }
        }

        cells[x][y] = CellState.SHOT_EMPTY;
        return ShotResult.MISS;
    }

    public void placeShip(Ship newShip) throws ShipPlacingException {
        boolean isHorizontal = newShip.getOrientation() == Ship.Orientation.HORIZONTAL;
        int shipCoordsSingle = newShip.getY();
        int shipCoordsRangeMin = newShip.getxStart();
        int shipSize = newShip.getSize();

        int minX = Math.max(isHorizontal ? shipCoordsRangeMin - 1 : shipCoordsSingle - 1, 0);
        int maxX = Math.min(isHorizontal ? shipCoordsRangeMin + shipSize : shipCoordsSingle + 1, GameSetup.boardSize - 1);

        int minY = Math.max(isHorizontal ? shipCoordsSingle - 1 : shipCoordsRangeMin - 1, 0);
        int maxY = Math.min(isHorizontal ? shipCoordsSingle + 1 : shipCoordsRangeMin + shipSize, GameSetup.boardSize - 1);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (cells[x][y] == CellState.SHIP) {
                    throw new ShipPlacingException();
                }
            }
        }

        for (int i = 0; i < shipSize; i++) {
            int x = isHorizontal ? shipCoordsRangeMin + i : shipCoordsSingle;
            int y = isHorizontal ? shipCoordsSingle : shipCoordsRangeMin + i;
            cells[x][y] = CellState.SHIP;
        }
        ships.add(newShip);
    }

    public Ship removeShipAt(int x, int y) {
        Ship removedShip = shipAt(x, y);

        if (removedShip != null) {
            ships.remove(removedShip);
            boolean isHorizontal = removedShip.getOrientation() == Ship.Orientation.HORIZONTAL;
            for (int i = 0; i < removedShip.getSize(); i++) {
                x = isHorizontal ? removedShip.getxStart() + i : removedShip.getY();
                y = isHorizontal ? removedShip.getY() : removedShip.getxStart() + i;
                cells[x][y] = CellState.EMPTY;
            }
        }

        return removedShip;
    }

    public void killShip(Ship ship) {
        for (Coord crd : ship.getCoords()) {
            cells[crd.x][crd.y] = CellState.DEAD_SHIP;
        }
    }

    public void markWatchedAroundShip(Ship ship) {
        if (ship != null) {
            boolean isVertical = ship.getOrientation() == Ship.Orientation.VERTICAL;
            ArrayList<Coord> crds = new ArrayList<>(ship.getCoords());
            int x, y;
            for (int i = 0; i < ship.getSize(); i++) {
                x = crds.get(i).x;
                y = crds.get(i).y;

                int adjacentX, adjacentY;

                if (i == 0) {
                    for (int j = -1; j <= 1; j++) {
                        adjacentX = isVertical ?  x - j : x - 1;
                        adjacentY = isVertical ?  y - 1 : y - j;
                        if (coordsInBounds(adjacentX, adjacentY)) {
                            cells[adjacentX][adjacentY] = CellState.SHOT_EMPTY;
                        }
                    }
                }

                adjacentX = isVertical ?  x - 1 : x;
                adjacentY = isVertical ?  y : y - 1;

                if (coordsInBounds(adjacentX, adjacentY)) {
                    cells[adjacentX][adjacentY] = CellState.SHOT_EMPTY;
                }

                adjacentX = isVertical ?  x + 1 : x;
                adjacentY = isVertical ?  y : y + 1;

                if (coordsInBounds(adjacentX, adjacentY)) {
                    cells[adjacentX][adjacentY] = CellState.SHOT_EMPTY;
                }


                if (i == ship.getSize() - 1) {
                    for (int j = -1; j <= 1; j++) {
                        adjacentX = isVertical ?  x - j : x + 1;
                        adjacentY = isVertical ?  y + 1 : y - j;
                        if (coordsInBounds(adjacentX, adjacentY)) {
                            cells[adjacentX][adjacentY] = CellState.SHOT_EMPTY;
                        }
                    }
                }
            }
        }
    }

    private boolean coordsInBounds(int x, int y) {
        return x >= 0 && x < GameSetup.boardSize && y >= 0 && y < GameSetup.boardSize;
    }
}
