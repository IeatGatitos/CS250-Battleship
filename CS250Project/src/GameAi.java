import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
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
    private int aiShipsRemaining = 16; //AI ships remaining
    private int playerShipsRemaining = 16; //player ships remaining
    private Random random = new Random(); //random helps ai randomize with some rules.regulations
    private JTextArea logsTextArea; //action logs

    //layout for grids /sidedbar
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
    
//sizing for rules and reset button
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
                
                Tutorial:
                User's Ships start from 5, 4, 3, 3, 2
                Please Start Shooting at Opponent's grid! (user always takes first shot!)
                Once all your ships have been placed
                """);
        sidebarPanel.add(new JScrollPane(rulesTextArea));
     // Initialize logsTextArea as a class field
        logsTextArea = new JTextArea(10, 20);
        logsTextArea.setEditable(false);
        logsTextArea.setLineWrap(true);
        logsTextArea.setWrapStyleWord(true);
        logsTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel logsLabel = new JLabel("Action Logs");
        logsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        sidebarPanel.add(logsLabel);
        sidebarPanel.add(new JScrollPane(logsTextArea));
        
        JButton resetGameButton = new JButton("Reset Game");
        resetGameButton.addActionListener(e -> resetGame());
        sidebarPanel.add(resetGameButton);
        return sidebarPanel;
        
    }
    private void logPlayerAction(String message) {
        logsTextArea.append("Player: " + message + "\n");
    }

    private void logAIAction(String message) {
        logsTextArea.append("AI: " + message + "\n");
    }

    private void logGameOutcome(String outcome) {
        logsTextArea.append("Game Over: " + outcome + "\n");
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
//helps with sequential placement / duplicates
    private boolean canPlaceShip(int row, int col, int size, boolean horizontal) { //aigrid
        // Check if the ship fits within the grid and does not overlap with other ships
        for (int i = 0; i < size; i++) {
            int r = row + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);

            // Ensure the ship doesn't go out of bounds
            if (r >= GRID_SIZE || c >= GRID_SIZE) {
                return false;
            }

            // Ensure the ship doesn't overlap with an existing ship
            if (aiGrid[r][c] == 1) {
                return false;
            }
        }
        return true; // Ship can be safely placed
    }
    
//handles shooting for user --> correlates to repaint
    private void handleShooting(int row, int col) {
        if (userViewGrid[row][col] != 'U') {
            JOptionPane.showMessageDialog(this, "You already attacked this position!");
            return;
        }

        if (aiGrid[row][col] == 1) { // Hit
            userViewGrid[row][col] = 'X';
            aiShipsRemaining--;
            repaintTopGrid();
            logPlayerAction("Hit at (" + (char)('A' + row) + "," + (col + 1) + ")");
            
            if (aiShipsRemaining == 0) {
                JOptionPane.showMessageDialog(this, "You win! All AI ships are sunk!");
                logGameOutcome("Player wins! All AI ships are sunk.");
            }
            return; 
        } else { // Miss
            userViewGrid[row][col] = 'O';
            repaintTopGrid();
            logPlayerAction("Miss at (" + (char)('A' + row) + "," + (col + 1) + ")");
            
            aiTurn(); // AI's turn after a miss
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
    
    //user grid 
    private boolean allShipsPlaced = false; // Flag to check if all ships are placed
//ship placement user handle 16 cells to plant 
    private void handleShipPlacement(int row, int col, JButton button) {
        // Check if all ships have been placed
        if (allShipsPlaced) {
            JOptionPane.showMessageDialog(this, "All ships are already placed! Ready to start the game.");
            return; // Stop further placement if all ships are already placed
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
        // Check if the current ship placement is valid
        if (shipIndex < shipSizes.length && button.getText().isEmpty()) {
            // Add the selected point to the current ship
            button.setText("S"); // Mark the cell with 'S' for the ship
            playerShipLocations.add(new Point(row, col));

            // Check if the current ship has been fully placed
            if (playerShipLocations.size() == totalCellsPlaced) {
                JOptionPane.showMessageDialog(this, "Ship of size " + shipSizes[shipIndex] + " placed!");
            }

            // If all ships have been placed (total size should equal 16 cells)
            if (playerShipLocations.size() == 16+1) {//this lets me actuallly register the last 
                allShipsPlaced = true;  // Set the flag to true
                JOptionPane.showMessageDialog(this, "All ships placed! Ready to start the game.");
            }
        }
         else {
            JOptionPane.showMessageDialog(this, "Invalid ship placement! Make sure ships are placed correctly.");
        }
    }
    
    private void aiTurn() {
        boolean shotTaken = false;
        while (!shotTaken) {
            int row, col;

            if (!aiHits.isEmpty()) {
                Point lastHit = aiHits.get(0);
                List<Point> potentialTargets = getAdjacentCells(lastHit);
                potentialTargets.removeIf(p -> isCellAlreadyShot(p.x, p.y));
                if (!potentialTargets.isEmpty()) {
                    Point target = potentialTargets.get(random.nextInt(potentialTargets.size()));
                    row = target.x;
                    col = target.y;
                } else {
                    aiHits.remove(0);
                    continue;
                }
            } else {
                row = random.nextInt(GRID_SIZE);
                col = random.nextInt(GRID_SIZE);
            }
            JButton targetButton = shipPlacementGridButtons[row][col];
            if (targetButton.getBackground() != Color.RED && targetButton.getBackground() != Color.GRAY) {
                if (targetButton.getText().equals("S")) { // Hit
                    targetButton.setBackground(Color.RED); // Mark hit
                    playerShipsRemaining--;
                    aiHits.add(new Point(row, col)); // Track hit
                    shipPlacementGridButtons[row][col].setText("X");
                    logAIAction("AI hit at (" + (char)('A' + row) + "," + (col + 1) + ")");
                    
                    if (playerShipsRemaining == 0) {
                        JOptionPane.showMessageDialog(this, "AI wins! All your ships are sunk!");
                        logGameOutcome("AI wins! All player's ships are sunk.");
                    }

                    continue;
                } else { // Miss
                    targetButton.setBackground(Color.GRAY); // Mark miss
                    shipPlacementGridButtons[row][col].setText("O");
                    logAIAction("AI missed at (" + (char)('A' + row) + "," + (col + 1) + ")");
                    shotTaken = true; // End AI's turn after a miss
                }
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
        return targetButton.getBackground() == Color.RED || targetButton.getBackground() == Color.GRAY;
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
        playerShipsRemaining = 16;
        aiShipsRemaining = 16;
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

