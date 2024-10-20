package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Main {

    private static Connection connection;

    public static void main(String[] args) {
        // Initialize the database connection
        connectToDatabase();

        JFrame window = new JFrame("Pro Chess Game by Niraj");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setSize(400, 400); // Increased height to accommodate stats

        // Home Page with Game Stats, Login, and New User options
        JPanel homePanel = new JPanel();
        homePanel.setLayout(new GridLayout(4, 1)); // Increased to 4 rows

        // Add game stats panel
        JPanel statsPanel = createGameStatsPanel();
        homePanel.add(statsPanel);

        JButton loginButton = new JButton("Login");
        JButton newUserButton = new JButton("Register New User");

        homePanel.add(loginButton);
        homePanel.add(newUserButton);

        window.add(homePanel);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        loginButton.addActionListener(e -> displayLoginPage(window));
        newUserButton.addActionListener(e -> displayRegisterPage(window, homePanel));
    }

    private static JPanel createGameStatsPanel() {
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Game Statistics"));

        String query = "SELECT result, COUNT(*) as count FROM game_stats GROUP BY result";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String result = rs.getString("result");
                int count = rs.getInt("count");
                JLabel statLabel = new JLabel(result + ": " + count);
                statsPanel.add(statLabel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statsPanel.add(new JLabel("Error retrieving game statistics."));
        }

        return statsPanel;
    }

    // Connect to MySQL Database
    private static void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + DatabaseInformation.DatabaseName, DatabaseInformation.DatabaseUser, DatabaseInformation.DatabasePassword);
            System.out.println("Database connected successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Display login page
    static void displayLoginPage(JFrame window) {
        window.getContentPane().removeAll();

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(4, 1));

        JTextField usernameField = new JTextField();
        JTextField player1Field = new JTextField();
        JTextField player2Field = new JTextField();
        JButton loginSubmitButton = new JButton("Login");

        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Player One Name (White):"));
        loginPanel.add(player1Field);
        loginPanel.add(new JLabel("Player Two Name (Black):"));
        loginPanel.add(player2Field);
        loginPanel.add(loginSubmitButton);

        window.add(loginPanel);
        window.revalidate();
        window.repaint();

        loginSubmitButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String player1 = player1Field.getText();
                String player2 = player2Field.getText();

                if (checkLogin(username)) {
                    // Clear previous components in the window
                    window.getContentPane().removeAll();

                    // Create a new panel to hold the "Edit Username" and "Delete Username" buttons
                    JPanel actionPanel = new JPanel();
                    actionPanel.setLayout(new GridLayout(3, 1)); // Stack buttons vertically

                    // Create buttons for "Edit Username", "Delete Username", and "Start Game"
                    JButton editUsernameButton = new JButton("Edit Username");
                    JButton deleteUsernameButton = new JButton("Delete User");
                    JButton startGameButton = new JButton("Start Game");

                    // Add the buttons to the new panel
                    actionPanel.add(editUsernameButton);
                    actionPanel.add(deleteUsernameButton);
                    actionPanel.add(startGameButton);

                    // Set the new content pane with the actionPanel
                    window.setContentPane(actionPanel);

                    // Refresh the window to show the new components
                    window.revalidate();
                    window.repaint();

                    // Add action listener for editing username
                    editUsernameButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            // Prompt the user for a new username
                            String newUsername = JOptionPane.showInputDialog(window, "Enter a new username:");

                            if (newUsername != null && !newUsername.trim().isEmpty()) {
                                // Call a method to update the username in the database
                                updateUsername(username, newUsername);
                                JOptionPane.showMessageDialog(window, "Username updated successfully!");
                                startChessGame(window, player1, player2);
                            } else {
                                JOptionPane.showMessageDialog(window, "Invalid username. Please try again.");
                            }
                        }

                        // Method to update username in the database
                        private void updateUsername(String oldUsername, String newUsername) {
                            String query = "UPDATE users SET username = ? WHERE username = ?";

                            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + DatabaseInformation.DatabaseName, DatabaseInformation.DatabaseUser, DatabaseInformation.DatabasePassword);
                                 PreparedStatement stmt = conn.prepareStatement(query)) {

                                stmt.setString(1, newUsername);
                                stmt.setString(2, oldUsername);
                                stmt.executeUpdate();

                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(null, "Error updating username.");
                            }
                        }
                    });

                    // Add action listener for deleting username
                    deleteUsernameButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            int confirm = JOptionPane.showConfirmDialog(window, "Are you sure you want to delete this user?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

                            if (confirm == JOptionPane.YES_OPTION) {
                                // Call a method to delete the username from the database
                                deleteUsername(username);
                                JOptionPane.showMessageDialog(window, "User deleted successfully!");
                            }
                        }

                        // Method to delete username from the database
                        private void deleteUsername(String username) {
                            String query = "DELETE FROM users WHERE username = ?";

                            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + DatabaseInformation.DatabaseName, DatabaseInformation.DatabaseUser, DatabaseInformation.DatabasePassword);
                                 PreparedStatement stmt = conn.prepareStatement(query)) {

                                stmt.setString(1, username);
                                stmt.executeUpdate();

                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(null, "Error deleting username.");
                            }
                        }
                    });


                    // Add action listener for starting the chess game
                    startGameButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            // Start the chess game when the "Start Game" button is clicked
                            startChessGame(window, player1, player2);
                        }
                    });
                } else {
                    JOptionPane.showMessageDialog(window, "Invalid Username. Please try again.");
                }
            }
        });


    }

    // Display registration page
    private static void displayRegisterPage(JFrame window, JPanel homePanel) {
        window.getContentPane().removeAll();

        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new GridLayout(3, 1));

        JTextField usernameField = new JTextField();
        JButton registerSubmitButton = new JButton("Register");

        registerPanel.add(new JLabel("Enter New Username:"));
        registerPanel.add(usernameField);
        registerPanel.add(registerSubmitButton);

        window.add(registerPanel);
        window.revalidate();
        window.repaint();

        registerSubmitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();

                if (checkUsernameExists(username)) {
                    JOptionPane.showMessageDialog(window, "Username already exists. Please try a different name.");
                } else {
                    addNewUser(username);
                    JOptionPane.showMessageDialog(window, "User registered successfully! Please login.");
                    displayLoginPage(window);
                }
            }
        });
    }

    // Check if the username exists in the database
    private static boolean checkUsernameExists(String username) {
        try {
            String query = "SELECT * FROM users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next(); // If a result exists, return true
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Add a new user to the database
    private static void addNewUser(String username) {
        try {
            String query = "INSERT INTO users (username) VALUES (?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Check if the login credentials are correct
    private static boolean checkLogin(String username) {
        try {
            String query = "SELECT * FROM users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Start the chess game after login
    private static void startChessGame(JFrame window, String player1, String player2) {
        window.getContentPane().removeAll();

        JFrame gameWindow = new JFrame("Chess Game - " + player1 + " (White) vs " + player2 + " (Black)");
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameWindow.setResizable(false);


        GamePanel gp = new GamePanel();
        gp.setPlayer1Name(player1);
        gp.setPlayer2Name(player2);

        gameWindow.add(gp);
        gameWindow.pack();
        gameWindow.setLocationRelativeTo(null);
        gameWindow.setVisible(true);

        gp.launchGame();
    }

}
