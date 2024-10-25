import javax.swing.*;
import java.awt.*;

public class GameAi extends JFrame {
    
    public GameAi() {
        setTitle("Battleship Game");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Example label to display game status
        JLabel titleLabel = new JLabel("You vs. Ai!", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Example game board panel
        JPanel gameBoard = new JPanel();
        gameBoard.setLayout(new GridLayout(10, 10)); // 10x10 grid

        // Create buttons for the game board
    }
}
