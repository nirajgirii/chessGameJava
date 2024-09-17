package piece;

import main.GamePanel;
import main.Type;

public class Bishop extends Piece {

    public Bishop(int color, int col, int row) {
        super(color, col, row);
        type = Type.BISHOP;

        if(color == GamePanel.WHITE){
            image = getImage("/res/piece/bishop_white.png");
        }else if(color == GamePanel.BLACK){
            image = getImage("/res/piece/bishop_black.png");
        }
    }
    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
            // Rook can move as long as either its col or row is the same
            if (Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)) {
                if (isValidSquare(targetCol, targetRow) && !pieceIsOnDiagonalLine(targetCol, targetRow)) {
                    return true;
                }
            }
        }
        return false;
    }
}
