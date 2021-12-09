import javax.swing.*;
import java.awt.*;

class BallThread extends Thread {
    JPanel panel;
    private int posX, posY;
    private final int BALL_SIZE = 10;
    private int SPEED = 4;
    private int sign = 1;

    BallThread(JPanel p) {
        this.panel = p;
        this.posX = 0;
        posY = (int)((panel.getHeight() - BALL_SIZE)
                * Math.random());
    }
    public void run() {
        while(true) {
            posX += sign * SPEED;
            if( posX >= panel.getWidth() - BALL_SIZE ) {
                sign = -1;
                posY = (int)((panel.getHeight() - BALL_SIZE)
                        * Math.random());
            }
            else if( posX <= 0 ) {
                sign = 1;
                posY = (int)((panel.getHeight() - BALL_SIZE)
                        * Math.random());
            }
            paint(panel.getGraphics());
        }
    }
    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillArc(posX, posY, BALL_SIZE, BALL_SIZE, 0, 360);
        g.setColor(Color.WHITE);
        g.drawArc(posX + 1, posY + 1, BALL_SIZE,
                BALL_SIZE, 120, 30);
        try {
            sleep(30);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        g.setColor(panel.getBackground());
        g.fillArc(posX, posY, BALL_SIZE, BALL_SIZE, 0, 360);
    }
}