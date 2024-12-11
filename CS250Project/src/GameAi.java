/**
 * Class: GameAi
 * Author: Kimberle Ramirez,
 * Date: 12/11/2024
 * Assignment: Final
 * Description: 
 * This class represents the core logic of a Battleship game with AI
 * It manages both the player and AI grids, handles ship placement, shooting mechanics, and game flow
 * The user interacts with the GUI, placing their ships and shooting at the AI's grid 
 * The AI randomly places its ships and responds to the player's actions
 * The game continues until all ships from either the player or AI are sunk
 * 
 * Inputs:
 * - Player's grid coordinates for shooting.
 * - Player's ship placement selection.
 * 
 * Outputs:
 * - GUI updates to reflect game state.
 * - Action logs to track the player's and AI's actions.
 * - Messages to notify the user of hits, misses, and game outcomes.
 * Methods include:
 * - Loading the game map 
 * - boundary checks
 * - Resetting the game state
 */

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


//variables for amount of cell ships, arrays etc
public class GameAi extends JFrame {
    private static final int GRID_SIZE = 10; //10x10
    private JButton[][] shootingGridButtons = new JButton[GRID_SIZE][GRID_SIZE]; //top
    private JButton[][] shipPlacementGridButtons = new JButton[GRID_SIZE][GRID_SIZE]; //bottom
    private char[][] userViewGrid = new char[GRID_SIZE][GRID_SIZE]; //hits/misses on AI grid
    private int[][] aiGrid = new int[GRID_SIZE][GRID_SIZE]; //AI ship placement (1 = ship, 0 = water)//tracks ships
    private List<Point> playerShipLocations = new ArrayList<>(); //goes to handleship placement validates location
    private List<Point> aiHits = new ArrayList<>(); // Track recent hits
    private int aiShipsRemaining = 17; //AI ships remaining//itll only consider all cells shot if i mention there are 17 cells even if there is 16 cells in total
    private int playerShipsRemaining = 17; //player ships remaining
    private Random random = new Random(); //random helps ai randomize with some rules.regulations
    private JTextArea logsTextArea; //action logs
    int gridWidth = 10;  // Number of columns
    int gridHeight = 10; // Number of rows

    //layout for grids /sidebar
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
//registers both grids
    private void initializeGrids() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                userViewGrid[row][col] = 'U'; //untouched cell
                aiGrid[row][col] = 0; //no ship by default
            }
        }
    }
//grid that holds ai ships and recieves shots 
    private JPanel createShootingGrid() { //ai grid
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

    private JPanel createShipPlacementGrid() {//this is recreated //user grid
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
        rulesTextArea.setEditable(false); ///down below are rules for the game for user clarification
        rulesTextArea.setText("""
                1. Place your ships on the bottom grid.
                2. Use the top grid to shoot at the AI's ships.
                3. A hit will be marked with 'X'.
                4. A miss will be marked with 'O'.
                5. The game ends when all ships are sunk.

                Tutorial:
                User's Ships start from 5, 4, 3, 3, 2
                Default position is vertical! Unless Previous game last cell was placed horizontal
                Allowed to place vertically horizontally ! Always press a button!
                Allowed to reset game whenever needed!
                Please Start Shooting at Opponent's grid!(user first)
                """);
        sidebarPanel.add(new JScrollPane(rulesTextArea));

        // Action Logs
        logsTextArea = new JTextArea(10, 20);
        logsTextArea.setEditable(false);
        logsTextArea.setLineWrap(true);
        logsTextArea.setWrapStyleWord(true);
        logsTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JLabel logsLabel = new JLabel("Action Logs"); //label
        logsLabel.setFont(new Font("Arial", Font.BOLD, 16)); //font
        sidebarPanel.add(logsLabel);
        sidebarPanel.add(new JScrollPane(logsTextArea));

        // Reset Button
        JButton resetGameButton = new JButton("Reset Game"); //label for button
        resetGameButton.addActionListener(e -> resetGame()); //send us to the action of reset
        // Orientation Buttons
        JButton verticalButton = new JButton("Vertical");
        verticalButton.addActionListener(e -> {
            isVertical = true;
            logsTextArea.append("Placement set to Vertical.\n"); //prompts to let user know v
        });

        JButton horizontalButton = new JButton("Horizontal"); //h
        horizontalButton.addActionListener(e -> {
            isVertical = false;
            logsTextArea.append("Placement set to Horizontal.\n"); //h
        });

        sidebarPanel.add(verticalButton);
        sidebarPanel.add(horizontalButton);
        sidebarPanel.add(resetGameButton);///reset
        return sidebarPanel;
    }

    private void logPlayerAction(String message) {
        logsTextArea.append("Player: " + message + "\n"); //playerlogs
    }

    private void logAIAction(String message) {
        logsTextArea.append("AI: " + message + "\n"); //ai did this
    }

    private void logGameOutcome(String outcome) {
        logsTextArea.append("Game Over: " + outcome + "\n"); //game over someone wins
    }
    
    private void resetGamelog() {
        // Reset logic for the game
        logsTextArea.setText(""); // Clear the logs when the game is reset
    }
    
 //array size ships
    private void placeAIShips() {
        int[] shipSizes = {5, 4, 3, 3, 2}; // Array for the AI's ship sizes
//checks
        for (int size : shipSizes) { // Place each ship size
            boolean placed = false;

            while (!placed) { //checks
                // Randomly generate starting coordinates and orientation
                int row = random.nextInt(GRID_SIZE);
                int col = random.nextInt(GRID_SIZE);
                boolean horizontal = random.nextBoolean();

                // Check if the ship can be placed here
                if (canPlaceShip(row, col, size, horizontal)) {
                    // Place the ship by marking the cells in `aiGrid`
                    for (int i = 0; i < size; i++) {
                        int r = row + (horizontal ? 0 : i); //calculate row of ship
                        int c = col + (horizontal ? i : 0); //calc column of ship
                        aiGrid[r][c] = 1; // Mark this cell as part of a ship
                    }
                    placed = true; // Ship successfully placed
                }
            }
        }
    }
        private boolean canPlaceShip(int row, int col, int shipSize, boolean horizontal) {
            // Check grid boundaries
            if (horizontal && col + shipSize > GRID_SIZE) return false;  // Horizontal boundary check
            if (!horizontal && row + shipSize > GRID_SIZE) return false; // Vertical boundary check
            
            // Check if the cells are available (no other ships placed)
            for (int i = 0; i < shipSize; i++) {
                int r = horizontal ? row : row + i;  // If h, keep row, else increase row for vertical
                int c = horizontal ? col + i : col;  // If v, keep column, else increase column for horizontal
                
                // Check if this cell is already occupied
                if (aiGrid[r][c] == 1) { // Check if the cell is already occupied by a ship
                    return false; // Collision detected, can't place ship here
                }
            }
            
            return true;  // Ship can be placed here
        }
        
//handles shooting for user --> correlates to repaint
     // This method updates the user view grid, processes hits or misses, and triggers the AI's turn if necessary
     private void handleShooting(int row, int col) {
         // Check if the user has already attacked the chosen position.
         if (userViewGrid[row][col] != 'U') { // 'U' represents an unvisited cell.
             JOptionPane.showMessageDialog(this, "You already attacked this position!");
             return;
         }

         // Check if the user's shot is a hit on an AI ship
         if (aiGrid[row][col] == 1) { // AI's ship is represented by '1' in aigrid
             userViewGrid[row][col] = 'X'; // Mark the cell as a hit ('X')
             aiShipsRemaining--; // Decrease the count of remaining AI ships
             repaintTopGrid(); // Refresh the top grid display to show the hit
             
             // Log the player's successful hit.
             logPlayerAction("Hit at (" + (char)('A' + row) + "," + (col + 1) + ")");
             
             // Check if the AI has no remaining ships (player wins the game).
             if (aiShipsRemaining == 0) {
                 JOptionPane.showMessageDialog(this, "You win! All AI ships are sunk!");
                 logGameOutcome("Player wins! All AI ships are sunk."); // Log the game outcome
             }
             return; // Exit after handling a hit
         } else { // The user's shot is a miss
             userViewGrid[row][col] = 'O'; // Mark the cell as a miss ('O')
             repaintTopGrid(); // Refresh the top grid display to show the miss
             
             // Log the player's miss.
             logPlayerAction("Miss at (" + (char)('A' + row) + "," + (col + 1) + ")");
             
             // Trigger the AI's turn after the user misses
             aiTurn();
         }
     }

//paints top grid to register hit and miss --> aigrid
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
    private boolean isVertical = true; // Default placement is vertical
    //user grid 
    private boolean allShipsPlaced = false; // Flag to check if all ships are placed
    private void handleShipPlacement(int row, int col, JButton button) {
        // Check if all ships have been placed
        if (allShipsPlaced) {
            JOptionPane.showMessageDialog(this, "All ships are already placed! Ready to start the game.");
            return;
        }

        // Define the sizes of the ships the player must place
        int[] shipSizes = {5, 4, 3, 3, 2};

        // Track the total number of cells placed
        int totalCellsPlaced = 0;

        // Determine which ship is being placed based on how many points have been placed
        int shipIndex = -1;
        for (int i = 0; i < shipSizes.length; i++) {
            totalCellsPlaced += shipSizes[i];
            if (playerShipLocations.size() < totalCellsPlaced) {
                shipIndex = i;
                break;
            }
        }
        // Validate and place the ship
        if (shipIndex < shipSizes.length) {
            int shipSize = shipSizes[shipIndex];

            // Check if the ship can be placed
            if (canPlaceShipUser(row, col, shipSize, isVertical)) {
                // Place the ship
                for (int i = 0; i < shipSize; i++) {
                    int r = isVertical ? row + i : row;
                    int c = isVertical ? col : col + i;

                    shipPlacementGridButtons[r][c].setText("S");
                    playerShipLocations.add(new Point(r, c));
                }
                // Notify the user
                JOptionPane.showMessageDialog(this, "Ship of size " + shipSize + " placed!");

                // Check if all ships are placed
                if (playerShipLocations.size() == 17) {
                    allShipsPlaced = true;
                    JOptionPane.showMessageDialog(this, "All ships placed! Ready to start the game.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid ship placement! Make sure ships fit on the grid.");
            }
        }
    }

    private boolean canPlaceShipUser(int row, int col, int shipSize, boolean isVertical) {
        // Check boundaries
        if (isVertical && row + shipSize > GRID_SIZE) return false;
        if (!isVertical && col + shipSize > GRID_SIZE) return false;

        // Check if cells are already occupied
        for (int i = 0; i < shipSize; i++) {
            int r = isVertical ? row + i : row;
            int c = isVertical ? col : col + i;

            if (!shipPlacementGridButtons[r][c].getText().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    //ai turn
 //The AI selects a target, takes a shot, and processes the result
    private void aiTurn() {
        boolean shotTaken = false; // Flag to track if the AI has taken a shot
        // Keep looping until the AI takes a shot 
        while (!shotTaken) {
            int row, col;

            // AI has previously hit a target, try to target other nearby cellls
            if (!aiHits.isEmpty()) {
                Point lastHit = aiHits.get(0); // Get the last hit location
                // Get a list of adjacent cells to the last hit
                List<Point> potentialTargets = getAdjacentCells(lastHit);
                potentialTargets.removeIf(p -> isCellAlreadyShot(p.x, p.y));  // Remove already shot cells from the potential target list
                // If there are valid targets, pick one at random
                if (!potentialTargets.isEmpty()) {
                    Point target = potentialTargets.get(random.nextInt(potentialTargets.size()));
                    row = target.x;
                    col = target.y;
                } else { 
                    // if no valid adjacent targets, remove the last hit from history 
                    aiHits.remove(0);
                    continue;
                }
            } else {
                // If the AI has no previous hits, select a random cell
                row = random.nextInt(GRID_SIZE);
                col = random.nextInt(GRID_SIZE);
            }
            // Get the target button on the player's grid.
            JButton targetButton = shipPlacementGridButtons[row][col];
            // Only shoot at cells that haven't been shot
                // Check if the AI hit a ship
                if (targetButton.getText().equals("S")) { // "S" 
                    playerShipsRemaining--; // Decrease the number of remaining player ships.
                    aiHits.add(new Point(row, col)); // Track the hit location.
                    shipPlacementGridButtons[row][col].setText("X"); // Mark the cell with "X" for hit.
                    
                    // Log the AI's successful hit.
                    logAIAction("AI hit at (" + (char)('A' + row) + "," + (col + 1) + ")");
                    
                    // Check if all the player's ships have been sunk (AI wins).
                    if (playerShipsRemaining == 0) {
                        JOptionPane.showMessageDialog(this, "AI wins! All your ships are sunk!");
                        logGameOutcome("AI wins! All player's ships are sunk.");
                    }
                    continue; // Skip the rest of the loop, as the AI needs to try again after a hit.
                } else { // Miss
                    targetButton.setBackground(Color.GRAY); // Mark the miss (gray).
                    shipPlacementGridButtons[row][col].setText("O"); // Mark the cell with "O" for miss.
                    
                    // Log the AI's miss.
                    logAIAction("AI missed at (" + (char)('A' + row) + "," + (col + 1) + ")");
                    
                    shotTaken = true; // End the AI's turn after a miss.
                }
            }
        }

    // Helper method to get adjacent cells
    private List<Point> getAdjacentCells(Point p) {
        List<Point> adjacent = new ArrayList<>();

        // Add adjacent cells (up, down, left, right)
        if (p.x > 0) adjacent.add(new Point(p.x - 1, p.y)); // Up
        if (p.x < GRID_SIZE - 1) adjacent.add(new Point(p.x + 1, p.y)); // Down
        if (p.y > 0) adjacent.add(new Point(p.x, p.y - 1)); // Left
        if (p.y < GRID_SIZE - 1) adjacent.add(new Point(p.x, p.y + 1)); // Right

        return adjacent;
    }

    // Helper method to check if a cell has already been shot
    private boolean isCellAlreadyShot(int row, int col) {
        JButton targetButton = shipPlacementGridButtons[row][col];
        return targetButton.getText().equals("X") || targetButton.getText().equals("O"); // Check if the cell has been sho
    }
    
//reset all to null/0
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
        playerShipsRemaining = 17;
        aiShipsRemaining = 17;
        // Clear the player ship locations
        playerShipLocations.clear();
        // Reinitialize the grids
        allShipsPlaced = false;//allow to place ships once again on user grid
        initializeGrids();
        placeAIShips();			
        //inform the user that the game is reset
        JOptionPane.showMessageDialog(this, "Game has been reset. You can place your ships again.");
        resetGamelog(); // Clear action logs calls the void statement
    }
    //main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameAi gameAi = new GameAi();
            gameAi.setVisible(true);
        });
    }
}

