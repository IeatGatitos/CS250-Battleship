import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GameAi extends JFrame {
    private static final int GRID_SIZE = 10;
    private JButton[][] shootingGridButtons = new JButton[GRID_SIZE][GRID_SIZE];
    private JButton[][] shipPlacementGridButtons = new JButton[GRID_SIZE][GRID_SIZE];
    private List<Point> playerShipLocations = new ArrayList<>(); // Stores ship locations
    private int shipsPlaced = 0; //counter for placed ships
    
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
        JPanel mainPanel = new JPanel(new GridLayout(2, 1)); // Two rows for the grids

        //shooting grid (top)
        JPanel shootingGrid = createShootingGrid();
        mainPanel.add(shootingGrid);

        //ship placement grid (bottom)
        JPanel shipPlacementGrid = createShipPlacementGrid();
        mainPanel.add(shipPlacementGrid);

        //main panel to the center of the frame
        add(mainPanel, BorderLayout.CENTER);

        //sidebar for rules and functionality
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.EAST);
    }

    private JPanel createShootingGrid() {
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new BorderLayout()); // Use BorderLayout to add labels

        // panel for the top labels (column numbers)
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new GridLayout(1, GRID_SIZE + 1)); // 10 columns + 1 for empty space
        labelPanel.add(new JLabel("")); // Empty corner

        //column numbers (1-10)
        for (int i = 1; i <= GRID_SIZE; i++) {
            labelPanel.add(new JLabel(String.valueOf(i), JLabel.CENTER));
        }
        gridPanel.add(labelPanel, BorderLayout.NORTH);

        // grid for shooting
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(GRID_SIZE, GRID_SIZE)); // 10x10 grid
        
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
        JPanel rowLabelPanel = new JPanel(new GridLayout(GRID_SIZE, 1)); // 10 rows
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
        labelPanel.setLayout(new GridLayout(1, GRID_SIZE + 1)); //10 columns + 1 for empty space
        labelPanel.add(new JLabel("")); 

        //column numbers (1-10)
        for (int i = 1; i <= GRID_SIZE; i++) {
            labelPanel.add(new JLabel(String.valueOf(i), JLabel.CENTER));
        }
        gridPanel.add(labelPanel, BorderLayout.NORTH);

        //the grid for placing ships
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(GRID_SIZE, GRID_SIZE)); // 10x10 grid
        
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JButton button = new JButton();
                button.setBackground(Color.CYAN); //color water //doesnt work???
                final int r = row;
                final int c = col;
                button.addActionListener(e -> handleShipPlacement(r, c, button));
                shipPlacementGridButtons[row][col] = button; //button reference
                buttonPanel.add(button);
            }
        }
        gridPanel.add(buttonPanel, BorderLayout.CENTER);

        // row labels (A-J) on the left side
        JPanel rowLabelPanel = new JPanel(new GridLayout(GRID_SIZE, 1)); // 10 rows
        for (char row = 'A'; row < 'A' + GRID_SIZE; row++) {
            rowLabelPanel.add(new JLabel(String.valueOf(row), JLabel.CENTER));
        }
        gridPanel.add(rowLabelPanel, BorderLayout.WEST);

        return gridPanel;
    }

    private JPanel createSidebar() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS)); //vertical layout

        //]rules label
        JLabel rulesLabel = new JLabel("Ships & Rules");
        rulesLabel.setFont(new Font("Arial", Font.BOLD, 16));
        sidebarPanel.add(rulesLabel);
        
        //rules text area
        JTextArea rulesTextArea = new JTextArea(10, 20);
        rulesTextArea.setEditable(false);
        rulesTextArea.setText("1. Place your ships on the bottom grid.\n"
                + "2. Use the top grid to shoot at the AI's ships.\n"
                + "3. A hit will change the color of the button.\n"
                + "4. The game ends when all ships are sunk.");
        sidebarPanel.add(new JScrollPane(rulesTextArea));

        //Button to place a ship
        JButton placeShipButton = new JButton("Place Ship");
        placeShipButton.addActionListener(e -> {
            //logic for placing a ship can go here
            JOptionPane.showMessageDialog(this, "Ship placed!"); //placeholder action
        });
        sidebarPanel.add(placeShipButton);
        
        //reset game
        JButton resetGameButton = new JButton("Reset Game");
        resetGameButton.addActionListener(e -> {
            //open the BattleShipGame frame
            BattleShipGame battleShipGame = new BattleShipGame();
            battleShipGame.setVisible(true);
            dispose(); //close the current window
        });
        sidebarPanel.add(resetGameButton);

        return sidebarPanel;
    }

    private void handleShooting(int row, int col, JButton button) {
        if (button.getBackground() == Color.CYAN) { // If it's water.. doesnt work
            button.setBackground(Color.GRAY); //mark as missed
            //additional logic can be added to check for hits against AI's ships !!!!!! (work on this)
            JOptionPane.showMessageDialog(this, "Miss!");
        } else {
            JOptionPane.showMessageDialog(this, "You already shot here!");
        }
    }

    private void handleShipPlacement(int row, int col, JButton button) {
        if (button.getText().isEmpty()) { // button is unoccupied
            button.setText("S"); //mark as occupied by a ship with "S"
            playerShipLocations.add(new Point(row, col)); //store ship location
            shipsPlaced++;
            //check if a certain number of ships have been placed
            if (shipsPlaced >= 4) { //change this number based on ship requirements
                JOptionPane.showMessageDialog(this, "All ships placed!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "This location is already occupied!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameAi gameAi = new GameAi();
            gameAi.setVisible(true);
        });
    }
}

