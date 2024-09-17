package piece;

import main.GamePanel;
import main.Type;

public class Knight extends Piece {

    public Knight(int color, int col, int row) {
        super(color, col, row);

        type = Type.KNIGHT;

        if(color == GamePanel.WHITE) {
            image = getImage("/res/piece/knight_white.png");
        }else if(color == GamePanel.BLACK) {
            image = getImage("/res/piece/knight_black.png");
        }
    }
    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow)) {
            // Knight can move if its movement ratio of col and row is 2:1 or 1:2
            if (Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 2) {
                if (isValidSquare(targetCol, targetRow)) {
                    return true;
                }
            }
        }
        return false;
    }
}
