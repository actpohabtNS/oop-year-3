package com.example.lab3.boardview;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.content.Context;

import com.example.lab3.R;
import com.example.lab3.battleship.Board;
import com.example.lab3.battleship.Coord;
import com.example.lab3.battleship.GameSetup;


public class BoardView extends View {
    private final Paint fieldPaint;
    protected float cellSize, cellPadding;
    protected float hPadding, vPadding;
    Board board;

    @SuppressLint("ClickableViewAccessibility")
    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        fieldPaint = new Paint();
        board = new Board();

        this.setOnTouchListener((v, event) -> {
            int x = (int) event.getX();
            int y = (int) event.getY();

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                boolean isCell = isCell(x, y);
                if (isCell) {
                    Coord crd = getCell(x, y);
                    board.shoot(crd.x, crd.y);
                    this.setBoard(board);
                    this.invalidate();
                }
            }

            return true;
        });
    }

    protected Coord getCell(float xCoord, float yCoord) {
        int x = (int) Math.floor((xCoord - hPadding ) / (cellSize + cellPadding)) - 1;
        int y = (int) Math.floor((yCoord - vPadding) / (cellSize + cellPadding)) - 1;
        return new Coord(x, y);
    }

    protected boolean isCell(float x, float y) {
        float sizeAndPadding = cellSize + cellPadding;
        if (x - sizeAndPadding - hPadding <= 0 ||
                y - sizeAndPadding - vPadding <= 0) {
            return false;
        }

        Coord crd = getCell(x, y);
        if ((crd.x + 2) * sizeAndPadding - x + hPadding < cellPadding ||
                (crd.y + 2) * sizeAndPadding - y + vPadding < cellPadding) {
            return false;
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        hPadding = vPadding = 0;
        float diff = this.getMeasuredHeight() - this.getMeasuredWidth();
        if (diff < 0) {
            hPadding = Math.abs(diff / 2f);
        } else {
            vPadding = Math.abs(diff / 2f);
        }
        cellSize = (this.getMeasuredWidth() - 2 * hPadding) / 13;
        cellPadding = cellSize / 5;

        fieldPaint.setColor(getResources().getColor(R.color.field_color, null));
        fieldPaint.setTextAlign(Paint.Align.CENTER);
        fieldPaint.setTextSize(cellSize * 0.6f);
        fieldPaint.setStyle(Paint.Style.FILL);
        fieldPaint.setAntiAlias(true);

        drawBoard(board, canvas);
    }

    private void drawBoard(Board board, Canvas canvas) {
        float x = cellPadding + cellSize * 1.5f + hPadding;
        float y = cellSize + vPadding;
        for (int i = 1; i <= GameSetup.boardSize; i++, x += cellSize + cellPadding) {
            canvas.drawText(String.valueOf(i), x, y, fieldPaint);
        }

        char symbol = 'A';
        y = cellSize + cellPadding + vPadding;
        for (int i = 1; i <= GameSetup.boardSize; i++, y += cellSize + cellPadding) {
            x = cellSize * 0.5f + hPadding;
            canvas.drawText(String.valueOf(symbol++), x, y + cellSize * 0.5f + cellPadding, fieldPaint);
            x = cellSize + cellPadding + hPadding;
            for (int j = 1; j <= GameSetup.boardSize; j++, x += cellSize + cellPadding) {
                int color = getResources().getColor(R.color.field_color, null);
                switch (board.getCell(j-1, i-1)) {
                    case SHOT_EMPTY:
                        color = getResources().getColor(R.color.empty_field_color, null);
                        break;
                    case DEAD_SHIP:
                        color = getResources().getColor(R.color.dead_ship_color, null);
                        break;
                    case DAMAGED_SHIP:
                        color = getResources().getColor(R.color.damaged_ship_color, null);
                        break;
//                    case SHIP:
//                        color = getResources().getColor(R.color.teal_700, null);
//                        break;
                }
                fieldPaint.setColor(color);
                canvas.drawRect(x, y, x + cellSize, y + cellSize, fieldPaint);
            }
        }
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board newBoard) {
        board = newBoard;
    }
}
