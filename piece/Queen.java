package piece;

import main.GamePanel;

public class Queen extends Piece {

    public Queen(int color, int col, int row) {
        super(color, col, row);

        if(color == GamePanel.WHITE) {
            image = getImage("/res/piece/queen_white.png");
        }else  if(color == GamePanel.BLACK) {
            image = getImage("/res/piece/queen_black.png");
        }
    }
}
