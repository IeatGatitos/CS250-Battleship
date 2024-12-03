import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameAi extends JFrame {
    private static final int GRID_SIZE = 10;
    private JButton[][] shootingGridButtons = new JButton[GRID_SIZE][GRID_SIZE];
    private JButton[][] shipPlacementGridButtons = new JButton[GRID_SIZE][GRID_SIZE];
    private char[][] userViewGrid = new char[GRID_SIZE][GRID_SIZE]; //hits/misses on AI grid
    private int[][] aiGrid = new int[GRID_SIZE][GRID_SIZE]; //AI ship placement (1 = ship, 0 = water)
    private List<Point> playerShipLocations = new ArrayList<>();
    private int aiShipsRemaining = 4; //AI ships remaining
    private int playerShipsRemaining = 4; //player ships remaining
    private Random random = new Random();

    public GameAi() {
        setTitle("Battleship Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Label for the game
        JLabel titleLabel = new JLabel("Battleship Game", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Main game panel
        JPanel mainPanel = new JPanel(new GridLayout(2, 1));
        mainPanel.add(createShootingGrid()); // Shooting grid (top)
        mainPanel.add(createShipPlacementGrid()); // Ship placement grid (bottom)
        add(mainPanel, BorderLayout.CENTER);

        // rules and functionality
        add(createSidebar(), BorderLayout.EAST);

        initializeGrids(); //Initialize userViewGrid and aiGrid
        placeAIShips(); //place AI ships on its grid
    }

    private void initializeGrids() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                userViewGrid[row][col] = 'U'; //untouched cell
                aiGrid[row][col] = 0; //no ship by default
            }
        }
    }

    private JPanel createShootingGrid() {
        JPanel gridPanel = new JPanel(new BorderLayout());

        // Top labels for column numbers
        JPanel labelPanel = new JPanel(new GridLayout(1, GRID_SIZE + 1));
        labelPanel.add(new JLabel("")); // Empty corner
        for (int i = 1; i <= GRID_SIZE; i++) {
            labelPanel.add(new JLabel(String.valueOf(i), JLabel.CENTER));
        }
        gridPanel.add(labelPanel, BorderLayout.NORTH);

        // Shooting grid buttons
        JPanel buttonPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JButton button = new JButton();
                final int r = row;
                final int c = col;
                button.addActionListener(e -> handleShooting(r, c));
                shootingGridButtons[row][col] = button;
                buttonPanel.add(button);
            }
        }
        gridPanel.add(buttonPanel, BorderLayout.CENTER);

        // Row labels (A-J)
        JPanel rowLabelPanel = new JPanel(new GridLayout(GRID_SIZE, 1));
        for (char row = 'A'; row < 'A' + GRID_SIZE; row++) {
            rowLabelPanel.add(new JLabel(String.valueOf(row), JLabel.CENTER));
        }
        gridPanel.add(rowLabelPanel, BorderLayout.WEST);

        return gridPanel;
    }

    private JPanel createShipPlacementGrid() {//this is recreated
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new BorderLayout());

        // Panel for the top labels (column numbers)
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new GridLayout(1, GRID_SIZE + 1));
        labelPanel.add(new JLabel("")); // Empty corner

        // Column numbers (1-10)
        for (int i = 1; i <= GRID_SIZE; i++) {
            labelPanel.add(new JLabel(String.valueOf(i), JLabel.CENTER));
        }
        gridPanel.add(labelPanel, BorderLayout.NORTH);

        // The grid for placing ships
        JPanel buttonPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                final int r = row; // create a final copy of row
                final int c = col; //create a final copy of col
                JButton button = new JButton();
                button.addActionListener(e -> handleShipPlacement(r, c, button)); // final variables
                shipPlacementGridButtons[row][col] = button;
                buttonPanel.add(button);
            }
        }
        gridPanel.add(buttonPanel, BorderLayout.CENTER);

        //row labels (A-J) on the left side
        JPanel rowLabelPanel = new JPanel(new GridLayout(GRID_SIZE, 1));
        for (char row = 'A'; row < 'A' + GRID_SIZE; row++) {
            rowLabelPanel.add(new JLabel(String.valueOf(row), JLabel.CENTER));
        }
        gridPanel.add(rowLabelPanel, BorderLayout.WEST);

        return gridPanel;
    }


    private JPanel createSidebar() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));

        JLabel rulesLabel = new JLabel("Ships & Rules");
        rulesLabel.setFont(new Font("Arial", Font.BOLD, 16));
        sidebarPanel.add(rulesLabel);

        JTextArea rulesTextArea = new JTextArea(10, 20);
        rulesTextArea.setEditable(false);
        rulesTextArea.setText("""
                1. Place your ships on the bottom grid.
                2. Use the top grid to shoot at the AI's ships.
                3. A hit will be marked with 'X'.
                4. A miss will be marked with 'O'.
                5. The game ends when all ships are sunk.
                """);
        sidebarPanel.add(new JScrollPane(rulesTextArea));

        JButton resetGameButton = new JButton("Reset Game");
        resetGameButton.addActionListener(e -> resetGame());
        sidebarPanel.add(resetGameButton);

        return sidebarPanel;
    }

    private void placeAIShips() {
        for (int size = 4; size >= 2; size--) { //ship sizes 4 to 2
            boolean placed = false;
            while (!placed) {
                int row = random.nextInt(GRID_SIZE);
                int col = random.nextInt(GRID_SIZE);
                boolean horizontal = random.nextBoolean();

                if (canPlaceShip(row, col, size, horizontal)) {
                    for (int i = 0; i < size; i++) {
                        int r = row + (horizontal ? 0 : i);
                        int c = col + (horizontal ? i : 0);
                        aiGrid[r][c] = 1; //mark ship in aiGrid
                    }
                    placed = true;
                }
            }
        }
    }

    private boolean canPlaceShip(int row, int col, int size, boolean horizontal) {
        for (int i = 0; i < size; i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            if (r >= GRID_SIZE || c >= GRID_SIZE || aiGrid[r][c] == 1) {
                return false;
            }
        }
        return true;
    }

    private void handleShooting(int row, int col) {
        if (userViewGrid[row][col] != 'U') { // Already attacked
            JOptionPane.showMessageDialog(this, "You already attacked this position!");
            return;
        }

        if (aiGrid[row][col] == 1) { // Hit
            userViewGrid[row][col] = 'X';
            aiShipsRemaining--;
            JOptionPane.showMessageDialog(this, "Hit! You can shoot again.");
            repaintTopGrid();

            if (aiShipsRemaining == 0) { // Player wins
                JOptionPane.showMessageDialog(this, "You win!");
                resetGame();
            }
            return; // Allow the player to shoot again
        } else { // Miss
            userViewGrid[row][col] = 'O';
            JOptionPane.showMessageDialog(this, "Miss! AI's turn.");
            repaintTopGrid();

            aiTurn(); // AI takes its turn after a miss
        }
    }

    private void repaintTopGrid() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JButton button = shootingGridButtons[row][col];
                char value = userViewGrid[row][col];

                if (value == 'X' || value == 'O') {
                    button.setText(String.valueOf(value));
                } else {
                    button.setText("");
                }
            }
        }
    }

    private void handleShipPlacement(int row, int col, JButton button) {
        if (playerShipLocations.size() < 4 && button.getText().isEmpty()) {
            button.setText("S");
            playerShipLocations.add(new Point(row, col));
            if (playerShipLocations.size() == 4) {
                JOptionPane.showMessageDialog(this, "All ships placed!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid ship placement!");
        }
    }
    private void aiTurn() {
        boolean shotTaken = false;

        while (!shotTaken) {
            int row = random.nextInt(GRID_SIZE);
            int col = random.nextInt(GRID_SIZE);
            JButton targetButton = shipPlacementGridButtons[row][col];

            // Check if the AI has already shot here
            if (targetButton.getBackground() != Color.RED && targetButton.getBackground() != Color.GRAY) {
                if (targetButton.getText().equals("S")) { // AI hits a ship
                    targetButton.setBackground(Color.RED); // Mark hit on the user's grid
                    playerShipsRemaining--;
                    JOptionPane.showMessageDialog(this, "AI hit your ship!");

                    // Show hit on user's grid
                    shipPlacementGridButtons[row][col].setText("X");

                    if (playerShipsRemaining == 0) { // AI wins
                        JOptionPane.showMessageDialog(this, "AI wins!");
                        resetGame();
                    }
                } else { // Miss
                    targetButton.setBackground(Color.GRAY); // Mark miss on the user's grid
                    JOptionPane.showMessageDialog(this, "AI missed! Your turn.");

                    // Show miss on user's grid
                    shipPlacementGridButtons[row][col].setText("O");
                }
                shotTaken = true; // End AI's turn after one shot
            }
        }
        if (playerShipsRemaining == 0) {
            JOptionPane.showMessageDialog(this, "AI wins!");
        }
    }

    private void resetGame() {
        // Clear the ship placement grid and the shooting grid
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                // Reset the ship placement grid buttons to empty
                shipPlacementGridButtons[row][col].setText("");
                shipPlacementGridButtons[row][col].setBackground(null);
                
                // Reset the shooting grid buttons
                shootingGridButtons[row][col].setText("");
                shootingGridButtons[row][col].setBackground(null);
            }
        }
        // Reset player and AI ship counts
        playerShipsRemaining = 4;
        aiShipsRemaining = 4;
        // Clear the player ship locations
        playerShipLocations.clear();
        // Reinitialize the grids
        initializeGrids();
        placeAIShips();
        //inform the user that the game is reset
        JOptionPane.showMessageDialog(this, "Game has been reset. You can place your ships again.");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameAi gameAi = new GameAi();
            gameAi.setVisible(true);
        });
    }
}

