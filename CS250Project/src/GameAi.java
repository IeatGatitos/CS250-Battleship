import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameAi extends JFrame {
    private static final int GRID_SIZE = 10;
    private JButton[][] shootingGridButtons = new JButton[GRID_SIZE][GRID_SIZE];
    private JButton[][] shipPlacementGridButtons = new JButton[GRID_SIZE][GRID_SIZE];
    private List<Point> playerShipLocations = new ArrayList<>(); //stores player ship locations
    private List<Point> aiShipLocations = new ArrayList<>(); //stores AI ship locations
    private int shipsPlaced = 0; //counter for player-placed ships
    private int aiShipsRemaining = 4; //#of AI ships remaining
    private int playerShipsRemaining = 4; //# of player ships remaining
    private Random random = new Random();

    public GameAi() {
        setTitle("Battleship Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //label for the game
        JLabel titleLabel = new JLabel("Battleship Game", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        //main game panel
        JPanel mainPanel = new JPanel(new GridLayout(2, 1)); //2 rows for the grids

        //shooting grid (top)
        JPanel shootingGrid = createShootingGrid();
        mainPanel.add(shootingGrid);

        //ship placement grid (bottom)
        JPanel shipPlacementGrid = createShipPlacementGrid();
        mainPanel.add(shipPlacementGrid);

        //add main panel to the center of the frame
        add(mainPanel, BorderLayout.CENTER);

        //sidebar for rules and functionality
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.EAST);

        //place AI ships
        placeAIShips();
    }

    private JPanel createShootingGrid() {
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new BorderLayout());

        //panel for the top labels (column numbers)
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new GridLayout(1, GRID_SIZE + 1));
        labelPanel.add(new JLabel("")); // Empty corner

        //column numbers (1-10)
        for (int i = 1; i <= GRID_SIZE; i++) {
            labelPanel.add(new JLabel(String.valueOf(i), JLabel.CENTER));
        }
        gridPanel.add(labelPanel, BorderLayout.NORTH);

        //grid for shooting
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JButton button = new JButton();
                button.setBackground(Color.CYAN); // Initial color for water
                final int r = row;
                final int c = col;
                button.addActionListener(e -> handleShooting(r, c, button));
                shootingGridButtons[row][col] = button; // Store button reference
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

    private JPanel createShipPlacementGrid() {
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new BorderLayout());

        //panel for the top labels (column numbers)
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new GridLayout(1, GRID_SIZE + 1));
        labelPanel.add(new JLabel("")); // Empty corner

        //column numbers (1-10)
        for (int i = 1; i <= GRID_SIZE; i++) {
            labelPanel.add(new JLabel(String.valueOf(i), JLabel.CENTER));
        }
        gridPanel.add(labelPanel, BorderLayout.NORTH);

        // The grid for placing ships
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JButton button = new JButton();
                button.setBackground(Color.CYAN);
                final int r = row;
                final int c = col;
                button.addActionListener(e -> handleShipPlacement(r, c, button));
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
        rulesTextArea.setText("1. Place your ships on the bottom grid.\n"
                + "2. Use the top grid to shoot at the AI's ships.\n"
                + "3. A hit will change the color of the button.\n"
                + "4. The game ends when all ships are sunk.");
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
                        aiShipLocations.add(new Point(r, c));
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
            if (r >= GRID_SIZE || c >= GRID_SIZE || aiShipLocations.contains(new Point(r, c))) {
                return false;
            }
        }
        return true;
    }

    private void handleShooting(int row, int col, JButton button) {
        if (button.getBackground() == Color.CYAN) { //if it's water
            Point shot = new Point(row, col);
            if (aiShipLocations.contains(shot)) {
                button.setBackground(Color.RED); //hit
                aiShipLocations.remove(shot);
                aiShipsRemaining--;
                JOptionPane.showMessageDialog(this, "You hit an AI ship!");
                if (aiShipsRemaining == 0) {
                    JOptionPane.showMessageDialog(this, "You win!");
                    resetGame();
                    return;
                }
            } else {
                button.setBackground(Color.GRAY); // Miss
                JOptionPane.showMessageDialog(this, "You missed!");
            }
            aiTurn(); //AI shoots after player's turn
        } else {
            JOptionPane.showMessageDialog(this, "You already shot here!");
        }
    }

    private void aiTurn() {
        boolean shotTaken = false;

        while (!shotTaken) {
            int row = random.nextInt(GRID_SIZE);
            int col = random.nextInt(GRID_SIZE);
            JButton targetButton = shipPlacementGridButtons[row][col];

            if (targetButton.getBackground() == Color.CYAN) { //unshot location
                Point shot = new Point(row, col);
                if (playerShipLocations.contains(shot)) {
                    targetButton.setBackground(Color.RED); //hit
                    playerShipLocations.remove(shot);
                    playerShipsRemaining--;
                    JOptionPane.showMessageDialog(this, "AI hit your ship at " + (char) ('A' + row) + (col + 1) + "!");
                    if (playerShipsRemaining == 0) {
                        JOptionPane.showMessageDialog(this, "AI wins!");
                        resetGame();
                        return;
                    }
                } else {
                    targetButton.setBackground(Color.GRAY); //miss
                    JOptionPane.showMessageDialog(this, "AI missed!");
                }
                shotTaken = true;
            }
        }
    }

    private void handleShipPlacement(int row, int col, JButton button) {
        if (button.getText().isEmpty()) { //button is unoccupied
            button.setText("S");
            playerShipLocations.add(new Point(row, col));
            shipsPlaced++;
            if (shipsPlaced >= 4) { //change based on the number of player ships
                JOptionPane.showMessageDialog(this, "All ships placed!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "This location is already occupied!");
        }
    }

    private void resetGame() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            GameAi gameAi = new GameAi();
            gameAi.setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameAi gameAi = new GameAi();
            gameAi.setVisible(true);
        });
    }
}

