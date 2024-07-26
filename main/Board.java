package main;

import java.awt.Color;
import java.awt.Graphics2D;

public class Board {

    final int MAX_ROW = 8;
    final int MAX_COL = 8;

    public static final int SQUARE_SIZE = 100;
    public static final int HALF_SQUARE_SIZE = SQUARE_SIZE / 2;

    public void draw(Graphics2D g2) {
        for (int row = 0; row < MAX_ROW; row++) {
            for (int col = 0; col < MAX_COL; col++) {
                if ((row + col) % 2 == 0) {
                    // Light square color
                    g2.setColor(new Color(0, 100, 0));
                } else {
                    // Dark square color
                    g2.setColor(new Color(255, 255, 255));
                }
                g2.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }
}
