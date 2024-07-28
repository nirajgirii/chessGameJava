package piece;

import main.GamePanel;

public class Knight extends Piece {

    public Knight(int color, int col, int row) {
        super(color, col, row);

        if(color == GamePanel.WHITE) {
            image = getImage("/res/piece/knight_white.png");
        }else if(color == GamePanel.BLACK) {
            image = getImage("/res/piece/knight_black.png");
        }
    }
}
