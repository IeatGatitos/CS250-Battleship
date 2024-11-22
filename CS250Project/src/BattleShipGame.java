import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class BattleShipGame extends JFrame {
    private static final int GRID_SIZE = 10;
    private JButton[][] playerGrid;
    private JButton[][] opponentGrid;
    private boolean isPlacingShips = true;
    private int shipToPlace = 5; //start with 5 length then go down when placing ships
    private boolean isHorizontal = true; //is turned false after ships are placed
    private List<BattleShip> playerShips = new ArrayList<>();
    private List<BattleShip> opponentShips = new ArrayList<>();

    
    private final ImageIcon[] shipSprites = {
        new ImageIcon("ship5block.png"),
        new ImageIcon("ship4blocks.png"),
        new ImageIcon("ship3blocks.png"),
        new ImageIcon("ship2blocks.png")
    };

    public BattleShipGame() {
        setTitle("Battleship Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new GridLayout(1, 2));

        playerGrid = createGrid("Your Grid");
        opponentGrid = createGrid("Opponent's Grid");

        add(createGridPanel(playerGrid, "Your Grid"));
        add(createGridPanel(opponentGrid, "Opponent's Grid"));

        JButton toggleOrientationButton = new JButton("Toggle Orientation");
        toggleOrientationButton.addActionListener(e -> isHorizontal = !isHorizontal);
        add(toggleOrientationButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    //The 10x10 play area is represented by a 10x10 grid of buttons
    private JButton[][] createGrid(String title) {
        JButton[][] grid = new JButton[GRID_SIZE][GRID_SIZE];
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JButton button = new JButton();
                button.setBackground(Color.CYAN);
                button.addActionListener(new GridButtonListener(grid, row, col));
                grid[row][col] = button;
            }
        }
        return grid;
    }

    //makes a panel using grid above
    private JPanel createGridPanel(JButton[][] grid, String title) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        panel.add(label, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                gridPanel.add(grid[row][col]);
            }
        }
        panel.add(gridPanel, BorderLayout.CENTER);
        return panel;
    }

    //called in the grid listener
    private boolean isValidPlacement(int startX, int startY) {
    	//check if out of bounds
        if (isHorizontal) {
            if (startX + shipToPlace > GRID_SIZE) {
                return false;
            }
        } else {
            if (startY + shipToPlace > GRID_SIZE) {
                return false;
            }
        }

        //check overlap
        for (BattleShip ship : playerShips) {
            for (int i = 0; i < shipToPlace; i++) {
                int x = startX;
                int y = startY;
                if (isHorizontal) {
                    x += i;
                } else {
                    y += i;
                }
                for (int[] part : ship.getPosition()) {
                    if (part[0] == x && part[1] == y) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void placeShip(int startX, int startY) {
        BattleShip newShip = new BattleShip(shipToPlace, isHorizontal);
        newShip.placeShip(startX, startY);
        playerShips.add(newShip);

        //place sprite and mark positions
        for (int i = 0; i < shipToPlace; i++) {
            int x = startX;
            int y = startY;
            if (isHorizontal) {
                x += i;
            } else {
                y += i;
            }
            JButton button = playerGrid[x][y];
            button.setBackground(Color.GRAY);
            if (i == 0) {
                button.setIcon(shipSprites[5 - shipToPlace]); //ship starts on the space clicked
            }
        }
    }

    private class GridButtonListener implements ActionListener {
        private final JButton[][] grid;
        private final int row;
        private final int col;

        public GridButtonListener(JButton[][] grid, int row, int col) {
            this.grid = grid;
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = grid[row][col];
            
            
            if (isPlacingShips) {
                if (isValidPlacement(row, col)) {
                    placeShip(row, col);
                    JOptionPane.showMessageDialog(BattleShipGame.this, "Ship placed!");
                    shipToPlace--;
                    if (shipToPlace < 2) {
                        isPlacingShips = false;
                        JOptionPane.showMessageDialog(BattleShipGame.this, "All ships placed! Start attacking.");
                    }
                } else {
                    JOptionPane.showMessageDialog(BattleShipGame.this, "Invalid placement. Try again.");
                }
            } else {
                //handling attacks is unfinished
                if (grid == opponentGrid) {
                    if (button.getBackground() == Color.CYAN) {
                        button.setBackground(Color.RED); //hit
                        JOptionPane.showMessageDialog(BattleShipGame.this, "Hit!");
                    } else if (button.getBackground() == Color.GRAY) {
                        button.setBackground(Color.BLUE); //not doing green for miss because I'm colorblind
                        JOptionPane.showMessageDialog(BattleShipGame.this, "Miss!");
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BattleShipGame::new);
    }
}
