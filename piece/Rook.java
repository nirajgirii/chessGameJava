package piece;

import main.GamePanel;

public class Rook extends Piece{

    public Rook(int color, int col, int row) {
        super(color, col, row);

        if(color == GamePanel.WHITE){
            image = getImage("/res/piece/rook_white.png");
        }else if (color == GamePanel.BLACK){
            image = getImage("/res/piece/rook_black.png");
        }
    }
}
