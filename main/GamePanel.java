package main;

import piece.*;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

public class GamePanel extends JPanel implements Runnable {
    private int promotionCol;
    private int promotionRow;
    private int promotionColor;

    //Three Dot options
    private JPopupMenu optionsMenu;
    private JButton optionsButton;

    private boolean pieceSelected = false;
    private ArrayList<Point> possibleMoves = new ArrayList<>();

    // Players Name and setting Players Name
    private String player1Name;
    private String player2Name;

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    public static final int WIDTH = 1300;
    public static final int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    // PIECES
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    ArrayList<Piece> promotionPiece = new ArrayList<>();
    Piece activePiece, checkingPiece;
    public static Piece castlingPiece;

    // COLOR of PIECES
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;

    // BOOLEANS
    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameOver = false;
    boolean stalemate = false;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);
        createOptionsMenu();

        setPieces();
        copyPieces(pieces, simPieces);
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setPieces() {

        // -------------------------------- WHITE TEAM --------------------------------//

        // WHITE PAWN
        pieces.add(new Pawn(WHITE, 0, 6));
        pieces.add(new Pawn(WHITE, 1, 6));
        pieces.add(new Pawn(WHITE, 2, 6));
        pieces.add(new Pawn(WHITE, 3, 6));
        pieces.add(new Pawn(WHITE, 4, 6));
        pieces.add(new Pawn(WHITE, 5, 6));
        pieces.add(new Pawn(WHITE, 6, 6));
        pieces.add(new Pawn(WHITE, 7, 6));

        // WHITE ROOK
        pieces.add(new Rook(WHITE, 0, 7));
        pieces.add(new Rook(WHITE, 7, 7));

        // WHITE KNIGHT
        pieces.add(new Knight(WHITE, 1, 7));
        pieces.add(new Knight(WHITE, 6, 7));

        // WHITE BISHOP
        pieces.add(new Bishop(WHITE, 2, 7));
        pieces.add(new Bishop(WHITE, 5, 7));

        // WHITE KING
        pieces.add(new King(WHITE, 4, 7));

        // WHITE QUEEN
        pieces.add(new Queen(WHITE, 3, 7));

        // -------------------------------- BLACK TEAM --------------------------------//

        // BLACK PAWN
        pieces.add(new Pawn(BLACK, 0, 1));
        pieces.add(new Pawn(BLACK, 1, 1));
        pieces.add(new Pawn(BLACK, 2, 1));
        pieces.add(new Pawn(BLACK, 3, 1));
        pieces.add(new Pawn(BLACK, 4, 1));
        pieces.add(new Pawn(BLACK, 5, 1));
        pieces.add(new Pawn(BLACK, 6, 1));
        pieces.add(new Pawn(BLACK, 7, 1));

        // BLACK ROOK
        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Rook(BLACK, 7, 0));

        // BLACK KNIGHT
        pieces.add(new Knight(BLACK, 1, 0));
        pieces.add(new Knight(BLACK, 6, 0));

        // BLACK BISHOP
        pieces.add(new Bishop(BLACK, 2, 0));
        pieces.add(new Bishop(BLACK, 5, 0));

        // BLACK KING
        pieces.add(new King(BLACK, 4, 0));

        // BLACK QUEEN
        pieces.add(new Queen(BLACK, 3, 0));

    }

    // Three Dot options menu
    private void createOptionsMenu() {
        optionsMenu = new JPopupMenu();
        JMenuItem restartGame = new JMenuItem("Restart Game");
        JMenuItem newGame = new JMenuItem("Start New Game");
        JMenuItem logout = new JMenuItem("Logout");

        restartGame.addActionListener(e -> {
            restartGame();
        });

        newGame.addActionListener(e -> {
            startNewGame();
        });

        logout.addActionListener(e -> {
            logout();
        });

        optionsMenu.add(restartGame);
        optionsMenu.add(newGame);
        optionsMenu.add(logout);

        optionsButton = new JButton("⋮");
        optionsButton.setBounds(WIDTH - 50, 10, 40, 40);
        optionsButton.addActionListener(e -> {
            optionsMenu.show(optionsButton, 0, optionsButton.getHeight());
        });

        setLayout(null);
        add(optionsButton);
    }

    private void restartGame() {
        // Reset the game state
        currentColor = WHITE;
        gameOver = false;
        stalemate = false;
        promotion = false;
        activePiece = null;
        checkingPiece = null;
        castlingPiece = null;
        pieceSelected = false;
        possibleMoves.clear();

        // Clear and reset pieces
        pieces.clear();
        simPieces.clear();
        setPieces();
        copyPieces(pieces, simPieces);

        // Repaint the panel
        repaint();
    }

    private void startNewGame() {
        // Similar to restartGame, but you might want to prompt for new player names
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        String player1 = JOptionPane.showInputDialog(frame, "Enter name for Player 1 (White):");
        String player2 = JOptionPane.showInputDialog(frame, "Enter name for Player 2 (Black):");

        if (player1 != null && player2 != null && !player1.trim().isEmpty() && !player2.trim().isEmpty()) {
            setPlayer1Name(player1);
            setPlayer2Name(player2);
            restartGame();
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid player names. Game not started.");
        }
    }

    private void logout() {
        // Close the current game window and show the login screen
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        currentFrame.dispose();

        SwingUtilities.invokeLater(() -> {
            JFrame loginFrame = new JFrame("Chess Game Login");
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setSize(400, 300);

            // You'll need to implement a method to show the login panel
            // This is just a placeholder - you should implement this method in your Main class
            Main.displayLoginPage(loginFrame);

            loginFrame.setLocationRelativeTo(null);
            loginFrame.setVisible(true);
        });
    }


    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
        target.clear();
        target.addAll(source);
    }

    @Override
    public void run() {
        double drawInterval = (double) 1000000000 / FPS;
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
                System.out.println("Getting " + frames + " FPS");
                frames = 0;
                timer = 0;
            }
        }
    }

    private void update() {
        if (promotion) {
            promoting();
        } else if (!gameOver && !stalemate) {
            handleClicks();
        }
    }

    private void handleClicks() {
        while (mouse.hasClick()) {
            Point click = mouse.getNextClick();
            int col = click.x / Board.SQUARE_SIZE;
            int row = click.y / Board.SQUARE_SIZE;

            if (!pieceSelected) {
                // Select a piece
                for (Piece piece : simPieces) {
                    if (piece.color == currentColor && piece.col == col && piece.row == row) {
                        activePiece = piece;
                        pieceSelected = true;
                        possibleMoves = activePiece.getPossibleMoves();
                        break;
                    }
                }
            } else {
                // Move the selected piece
                if (activePiece.canMove(col, row)) {
                    // Perform the move
                    activePiece.col = col;
                    activePiece.row = row;
                    simulate();
                    if (validSquare) {
                        // Move confirmed
                        copyPieces(simPieces, pieces);
                        activePiece.updatePosition();
                        if (castlingPiece != null) {
                            castlingPiece.updatePosition();
                        }
                        if (isKingIsInCheck() && isCheckMate()) {
                            gameOver = true;
                        } else if (isStalemate() && !isKingIsInCheck()) {
                            stalemate = true;
                        } else {
                            if (canPromote()) {
                                promotion = true;
                            } else {
                                changePlayer();
                            }
                        }
                    } else {
                        // Invalid move, reset
                        copyPieces(pieces, simPieces);
                        activePiece.resetPosition();
                    }
                }
                // Deselect the piece
                activePiece = null;
                pieceSelected = false;
                possibleMoves.clear();
            }
        }
    }

    private void simulate() {
        canMove = false;
        validSquare = false;

        copyPieces(pieces, simPieces);

        if (castlingPiece != null) {
            castlingPiece.col = castlingPiece.preCol;
            castlingPiece.x = castlingPiece.getX(castlingPiece.col);
            castlingPiece = null;
        }

        if (activePiece.canMove(activePiece.col, activePiece.row)) {
            canMove = true;
            if (activePiece.hittingP != null) {
                simPieces.remove(activePiece.hittingP.getIndex());
            }
            checkCastling();
            if (!isIllegalKingMove(activePiece) && !opponentCanCaptureKing()) {
                validSquare = true;
            }
        }
    }

    private boolean isIllegalKingMove(Piece kingPiece) {
        if (kingPiece.type == Type.KING) {
            for (Piece piece : simPieces) {
                if (piece != kingPiece && piece.color != kingPiece.color && piece.canMove(kingPiece.col, kingPiece.row)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean opponentCanCaptureKing() {
        Piece kingPiece = getKing(false);
        for (Piece piece : simPieces) {
            if (piece.color != kingPiece.color && piece.canMove(kingPiece.col, kingPiece.row)) {
                return true;
            }
        }
        return false;
    }

    private boolean isKingIsInCheck() {
        Piece king = getKing(true);

        if (activePiece.canMove(king.col, king.row)) {
            checkingPiece = activePiece;
            return true;
        } else {
            checkingPiece = null;
        }
        return false;
    }

    private Piece getKing(boolean opponent) {
        Piece king = null;
        for (Piece piece : simPieces) {
            if (opponent) {
                if (piece.type == Type.KING && piece.color != currentColor) {
                    king = piece;
                }
            } else {
                if (piece.type == Type.KING && piece.color == currentColor) {
                    king = piece;
                }
            }
        }
        return king;
    }

    private boolean isCheckMate() {

        Piece king = getKing(true);

        if (kingCanMove(king)) {
            return false;
        } else {
            // Still may have chance
            int colDiff = Math.abs(checkingPiece.col - king.col);
            int rowDiff = Math.abs(checkingPiece.row - king.row);

            if (colDiff == 0) {
                // The checking piece is vertical
                if (checkingPiece.row < king.row) {
                    // The checking piece is above the king
                    for (int row = checkingPiece.row; row < king.row; row++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingPiece.col, row)) {
                                return false;
                            }
                        }
                    }
                }
                if (checkingPiece.row > king.row) {
                    // The checking piece is below the king
                    for (int row = checkingPiece.row; row > king.row; row--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingPiece.col, row)) {
                                return false;
                            }
                        }
                    }
                }
            } else if (rowDiff == 0) {
                // The checking piece is horizontal
                if (checkingPiece.col < king.col) {
                    // The checking piece is to the left of the king
                    for (int col = checkingPiece.col; col < king.row; col++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, checkingPiece.row)) {
                                return false;
                            }
                        }
                    }
                }
                if (checkingPiece.col > king.col) {
                    // The checking piece is to the right oo the king
                    for (int col = checkingPiece.col; col > king.row; col--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, checkingPiece.row)) {
                                return false;
                            }
                        }
                    }
                }
            } else if (colDiff == rowDiff) {
                // The checking piece is diagonal
                if (checkingPiece.row < king.row) {

                    if (checkingPiece.col < king.col) {
                        // Upper left
                        for (int col = checkingPiece.col, row = checkingPiece.row; col < king.col; col++, row++) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color == currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                    if (checkingPiece.col > king.col) {
                        // Upper right
                        for (int col = checkingPiece.col, row = checkingPiece.row; col > king.col; col--, row++) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color == currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                }

                if (checkingPiece.row > king.row) {
                    if (checkingPiece.col < king.col) {
                        // Lower left
                        for (int col = checkingPiece.col, row = checkingPiece.row; col < king.col; col++, row--) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color == currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }

                    }
                    if (checkingPiece.col > king.col) {
                        // Lower right
                        for (int col = checkingPiece.col, row = checkingPiece.row; col > king.col; col--, row--) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color == currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean kingCanMove(Piece king) {
        // Simulate if there is any square where the king can move to
        if (isValidMoveOfKing(king, -1, -1)) {
            return true;
        }
        if (isValidMoveOfKing(king, 0, -1)) {
            return true;
        }
        if (isValidMoveOfKing(king, 1, -1)) {
            return true;
        }
        if (isValidMoveOfKing(king, -1, 0)) {
            return true;
        }
        if (isValidMoveOfKing(king, 1, 0)) {
            return true;
        }
        if (isValidMoveOfKing(king, -1, 1)) {
            return true;
        }
        if (isValidMoveOfKing(king, 0, 1)) {
            return true;
        }
        if (isValidMoveOfKing(king, 1, 1)) {
            return true;
        }

        return false;
    }

    private boolean isValidMoveOfKing(Piece king, int colPlus, int rowPlus) {
        boolean isValidMove = false;

        // Update the king's position for a second
        king.col += colPlus;
        king.row += rowPlus;

        if (king.canMove(king.col, king.row)) {
            if (king.hittingP != null) {
                simPieces.remove(king.hittingP.getIndex());
            }
            if (!isIllegalKingMove(king)) {
                isValidMove = true;
            }
        }
        // Reset the king's position and restore the removed piece
        king.resetPosition();
        copyPieces(pieces, simPieces);

        return isValidMove;
    }

    private boolean isStalemate() {
        int count = 0;

        for (Piece piece : simPieces) {
            if (piece.color != currentColor) {
                count++;
            }
        }

        // if only king is left
        if (count == 1) {
            if (!kingCanMove(getKing(true))) {
                return true;
            }
        }
        return false;
    }

    private void checkCastling() {
        if (castlingPiece != null) {
            if (castlingPiece.col == 0) {
                castlingPiece.col += 3;
            } else if (castlingPiece.col == 7) {
                castlingPiece.col -= 2;
            }
            castlingPiece.x = castlingPiece.getX(castlingPiece.col);
        }
    }

    private void changePlayer() {
        if (currentColor == WHITE) {
            currentColor = BLACK;
        } else if (currentColor == BLACK) {
            currentColor = WHITE;
        }
        activePiece = null;
    }

    private void promoting() {
        while (mouse.hasClick()) {
            Point click = mouse.getNextClick();
            int col = click.x / Board.SQUARE_SIZE;
            int row = click.y / Board.SQUARE_SIZE;

            for (Piece piece : promotionPiece) {
                if (piece.col == col && piece.row == row) {
                    Piece newPiece = null;
                    switch (piece.type) {
                        case ROOK:
                            newPiece = new Rook(promotionColor, promotionCol, promotionRow);
                            break;
                        case KNIGHT:
                            newPiece = new Knight(promotionColor, promotionCol, promotionRow);
                            break;
                        case QUEEN:
                            newPiece = new Queen(promotionColor, promotionCol, promotionRow);
                            break;
                        case BISHOP:
                            newPiece = new Bishop(promotionColor, promotionCol, promotionRow);
                            break;
                        default:
                            break;
                    }
                    if (newPiece != null) {
                        // Remove the old pawn
                        simPieces.removeIf(p -> p.col == promotionCol && p.row == promotionRow);
                        // Add the new piece
                        simPieces.add(newPiece);
                        copyPieces(simPieces, pieces);
                        promotion = false;
                        changePlayer();
                        return;
                    }
                }
            }
        }
    }

    private boolean canPromote() {
        if (activePiece.type == Type.PAWN) {
            if (currentColor == WHITE && activePiece.row == 0 || currentColor == BLACK && activePiece.row == 7) {
                promotionCol = activePiece.col;
                promotionRow = activePiece.row;
                promotionColor = currentColor;
                promotionPiece.clear();
                promotionPiece.add(new Rook(currentColor, 10, 2));
                promotionPiece.add(new Knight(currentColor, 10, 3));
                promotionPiece.add(new Bishop(currentColor, 10, 4));
                promotionPiece.add(new Queen(currentColor, 10, 5));
                return true;
            }
        }
        return false;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        setComponentZOrder(optionsButton, 0);

        // Call Board draw method
        board.draw(g2);

        // Pieces
        for (Piece p : simPieces) {
            p.draw(g2);
        }

        if (activePiece != null) {
            g2.setColor(new Color(255, 165, 0));
            for (Point move : possibleMoves) {
                g2.fillOval(
                        move.x * Board.SQUARE_SIZE + Board.SQUARE_SIZE / 2 - Board.SQUARE_SIZE / 8,
                        move.y * Board.SQUARE_SIZE + Board.SQUARE_SIZE / 2 - Board.SQUARE_SIZE / 8,
                        Board.SQUARE_SIZE / 5,
                        Board.SQUARE_SIZE / 5
                );
            }

            if (canMove) {
                if (isIllegalKingMove(activePiece) || opponentCanCaptureKing()) {
                    g2.setColor(new Color(255, 0, 0));
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .7f));

                    // Highlight the current position of the active piece
                    g2.fillRect(activePiece.col * Board.SQUARE_SIZE, activePiece.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE,
                            Board.SQUARE_SIZE);

                    // Reset transparency and draw the piece
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                } else {

                    g2.setColor(new Color(255, 165, 0));
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .7f));

                    // Highlight the current position of the active piece
                    g2.fillRect(activePiece.col * Board.SQUARE_SIZE, activePiece.row * Board.SQUARE_SIZE, Board.SQUARE_SIZE,
                            Board.SQUARE_SIZE);

                    // Reset transparency and draw the piece
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }
            }

            activePiece.draw(g2);
        }


        // Message to show in game right hand side:
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Times New Roman", Font.BOLD, 40));
        g2.setColor(Color.WHITE);

        if (promotion) {
            g2.drawString("Promote your pawn to:", 840, 150);
            for (Piece piece : promotionPiece) {
                if (piece.color == BLACK) {
                    g2.setColor(Color.WHITE); // Set background to white for black pieces
                    g2.fillRect(piece.getX(piece.col), piece.getY(piece.row), Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                }
                g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row), Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
            }
        } else {
            if (currentColor == WHITE) {
                g2.drawString(player1Name + "'s Turn", 840, 550);
                if (checkingPiece != null && checkingPiece.color == BLACK) {
                    g2.setColor(Color.red);
                    g2.drawString("The King is in check", 840, 650);
                }
            } else {
                g2.drawString(player2Name + "'s Turn", 840, 250);
                if (checkingPiece != null && checkingPiece.color == WHITE) {
                    g2.setColor(Color.red);
                    g2.drawString("The King is in check", 840, 100);
                }
            }
        }

        if (gameOver) {
            String s = "";
            if (currentColor == WHITE) {
                s = player1Name + " Wins";
            } else {
                s = player2Name + " Wins";
            }
            g2.setFont(new Font("Times New Roman", Font.PLAIN, 90));
            g2.setColor(Color.GREEN);
            g2.drawString(s, 200, 420);
        }

        if (stalemate) {
            g2.setFont(new Font("Times New Roman", Font.PLAIN, 90));
            g2.setColor(Color.YELLOW);
            g2.drawString("Stalemate", 200, 420);
        }
    }

}