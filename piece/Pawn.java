package piece;

import main.GamePanel;

public class Pawn extends Piece {

    public Pawn(int color, int col, int row) {
        super(color, col, row);

        if (color == GamePanel.WHITE) {
            image = getImage("/res/piece/pawn_white.png");
        } else if (color == GamePanel.BLACK) {
            image = getImage("/res/piece/pawn_black.png");
        }
    }

    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
            int moveValue;
            if (color == GamePanel.WHITE)
                moveValue = -1;
            else
                moveValue = 1;

            // Check hitting piece
            hittingP = getHittingP(targetCol, targetRow);

            // 1 square movement
            if (targetCol == preCol && targetRow == preRow + moveValue && hittingP == null)
                return true;

            // 2 square movement
            if (targetCol == preCol && targetRow == preRow + moveValue * 2 &&
                    hittingP == null && moved == false &&
                    pieceIsOnStraightLine(targetCol, targetRow) == false)
                return true;

            // Diagonal movement and capture if piece is diagonal
            if (Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue &&
                    hittingP != null && hittingP.color != color)
                return true;
        }
        return false;
    }

}
