package piece;

import main.GamePanel;
import main.Type;

public class Rook extends Piece {

    public Rook(int color, int col, int row) {
        super(color, col, row);

        type = Type.ROOK;

        if (color == GamePanel.WHITE) {
            image = getImage("/res/piece/rook_white.png");
        } else if (color == GamePanel.BLACK) {
            image = getImage("/res/piece/rook_black.png");
        }
    }

    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) &&
                isSameSquare(targetCol, targetRow) == false) {
            // Rook can move as long as its col or row stays the same since it can move infinitely across one column or row
            // And it's not the same square it started on
            if (targetCol == preCol || targetRow == preRow) {
                if (isValidSquare(targetCol, targetRow) &&
                        pieceIsOnStraightLine(targetCol, targetRow) == false) {
                    return true;
                }
            }
        }

        return false;
    }
}
