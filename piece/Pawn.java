package piece;

import main.GamePanel;

public class Pawn extends Piece {

    public Pawn(int color, int col, int row) {
        super(color, col, row);

        if(color == GamePanel.WHITE){
            image = getImage("/piece/pawn_white.png");
        }else if(color == GamePanel.BLACK){
            image = getImage("/piece/pawn_black.png");
        }
    }
}
