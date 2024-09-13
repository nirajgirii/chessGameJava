package main;

import piece.*;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    //PIECES
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    Piece activePiece;


    //COLOR of PIECES
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;

    //BOOLEANS
    boolean canMove;
    boolean validSquare;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        setPieces();
        copyPieces(pieces, simPieces);
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setPieces() {

        //-------------------------------- WHITE TEAM --------------------------------//

        // WHITE PAWN
        pieces.add(new Pawn(WHITE, 0, 6));
        pieces.add(new Pawn(WHITE, 1, 6));
        pieces.add(new Pawn(WHITE, 2, 6));
        pieces.add(new Pawn(WHITE, 3, 6));
        pieces.add(new Pawn(WHITE, 4, 6));
        pieces.add(new Pawn(WHITE, 5, 6));
        pieces.add(new Pawn(WHITE, 6, 6));
        pieces.add(new Pawn(WHITE, 7, 6));

        //WHITE ROOK
        pieces.add(new Rook(WHITE, 0, 7));
        pieces.add(new Rook(WHITE, 7, 7));

        //WHITE KNIGHT
        pieces.add(new Knight(WHITE, 1, 7));
        pieces.add(new Knight(WHITE, 6, 7));

        //WHITE BISHOP
        pieces.add(new Bishop(WHITE, 2, 7));
        pieces.add(new Bishop(WHITE, 5, 7));

        //WHITE KING
        pieces.add(new King(WHITE, 4, 7));

        //WHITE QUEEN
        pieces.add(new Queen(WHITE, 3, 7));


        //-------------------------------- BLACK TEAM --------------------------------//

        // BLACK PAWN
        pieces.add(new Pawn(BLACK, 0, 1));
        pieces.add(new Pawn(BLACK, 1, 1));
        pieces.add(new Pawn(BLACK, 2, 1));
        pieces.add(new Pawn(BLACK, 3, 1));
        pieces.add(new Pawn(BLACK, 4, 1));
        pieces.add(new Pawn(BLACK, 5, 1));
        pieces.add(new Pawn(BLACK, 6, 1));
        pieces.add(new Pawn(BLACK, 7, 1));

        //BLACK ROOK
        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Rook(BLACK, 7, 0));

        //BLACK KNIGHT
        pieces.add(new Knight(BLACK, 1, 0));
        pieces.add(new Knight(BLACK, 6, 0));

        //BLACK BISHOP
        pieces.add(new Bishop(BLACK, 2, 0));
        pieces.add(new Bishop(BLACK, 5, 0));

        //BLACK KING
        pieces.add(new King(BLACK, 4, 0));

        //BLACK QUEEN
        pieces.add(new Queen(BLACK, 3, 0));

    }


    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
        target.clear();
        target.addAll(source);
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int frames = 0;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
                frames++;
            }

            if (timer >= 1000000000) {
                System.out.println("FPS: " + frames);
                frames = 0;
                timer = 0;
            }
        }
    }


    private void update() {
        if (mouse.pressed) {
            if (activePiece == null) {
                for (Piece piece : simPieces) {
                    if (piece.color == currentColor && piece.col == mouse.x / Board.SQUARE_SIZE && piece.row == mouse.y / Board.SQUARE_SIZE) {
                        activePiece = piece;
                    }
                }
            } else {
                simulate();
            }
        }

        if (mouse.pressed == false) {
            if (activePiece != null) {
                if (validSquare) {
                    // MOVE CONFIRMED

                    // Update the piece list in case a piece has been captured and removed during the simulation
                    copyPieces(simPieces, pieces);
                    activePiece.updatePosition();
                } else {
                    // The move is not valid so reset everything
                    copyPieces(pieces, simPieces);
                    activePiece.resetPosition();
                    activePiece = null;
                }
            }
        }

    }

    private void simulate() {

        canMove = false;
        validSquare = false;

        // Reset the piece list in every loop
        // This is basically for restoring the removed piece during the simulation

        activePiece.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activePiece.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activePiece.col = activePiece.getCol(activePiece.x);
        activePiece.row = activePiece.getRow(activePiece.y);

        //Check if the piece is hovering over a reachable square
        if (activePiece.canMove(activePiece.col, activePiece.row)) {
            canMove = true;
            if (activePiece.hittingP != null) {
                simPieces.remove(activePiece.hittingP.getIndex());
            }
            validSquare = true;
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // Call Board draw method
        board.draw(g2);

        //Pieces
        for (Piece p : simPieces) {
            p.draw(g2);
        }

        if (activePiece != null) {

            if (canMove) {
                g2.setColor(new Color(255, 165, 0));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .7f));

                // Highlight the current position of the active piece
                g2.fillRect(activePiece.col * Board.SQUARE_SIZE, activePiece.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);

                // Reset transparency and draw the piece
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }

            activePiece.draw(g2);
        }
    }

}