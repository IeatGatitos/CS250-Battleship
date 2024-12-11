/**
 * Class: BattleShipGame
 * Author: Kimberle Ramirez, Jack
 * Date: 12/11/2024
 * Assignment: Final
 * Description:
 * This class creates the main window of the Battleship game. It allows the user to choose between playing against the AI or another player
 * Ready?" button is clicked, the game starts depending on the user's choice. If "AI" is selected, it opens the game screen for AI gameplay. If "Player" is selected, it shows a message that a player-vs-player game will start but never does
 * 
 * Inputs:
 * - The player selects "AI" or "Player" from the combo box.
 * 
 * Outputs:
 * - A new game window opens based on the selected option (AI or Player)
 * - Rules are displayed in the text area for the player to read.
 * 
 * Methods:
 * - main(String[] args): invoking the BattleShipGame frame
 * - BattleShipGame(): initialize combo box, "Ready?" button, and rules text area
 * - actionPerformed(ActionEvent e): Listens for clicks on the "Ready?" button and initiates the appropriate game mode (AI or Player)
 */

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BattleShipGame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JComboBox<String> comboAIorPlayer;
    private JButton btnReady;
    private JTextArea rulesTextArea; //display rules

    /**
     * Launch
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                BattleShipGame frame = new BattleShipGame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * frame
     */
    public BattleShipGame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        
        //BoxLayout to stack components vertically
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        
        //the combo box
        comboAIorPlayer = new JComboBox<>();
        comboAIorPlayer.setModel(new DefaultComboBoxModel<>(new String[] {"AI", "Player"}));
        contentPane.add(comboAIorPlayer);
        
        //Ready button
        btnReady = new JButton("Ready?");
        contentPane.add(btnReady);
        
        //Initialize and add rules text area
        rulesTextArea = new JTextArea();
        rulesTextArea.setEditable(false); // Make the text area read-only
        rulesTextArea.setText("Game Rules:\n"
                + "1. Place your ships on the grid.\n"
                + "2. Use your turns to guess the locations of the opponent's ships.\n"
                + "3. A hit will be SHOT, and if all parts of a ship are hit, it is sunk.\n"
                + "4. The game ends when all ships of one player have sunk.\n"
                + "5. Have fun!");

        //scroll pane to the text area
        JScrollPane scrollPane = new JScrollPane(rulesTextArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(400, 100)); //size for the scroll pane
        contentPane.add(scrollPane);

        //action listener to the button
        btnReady.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) comboAIorPlayer.getSelectedItem();
                if ("AI".equals(selected)) {
                    //open the game screen for AI
                    GameAi gameai = new GameAi();
                    gameai.setVisible(true);
                    dispose(); //close the current window
                } else {
                    //starting a player vs player game can go here
                    JOptionPane.showMessageDialog(null, "Starting player vs player game!");
                }
            }
        });
    }
}
