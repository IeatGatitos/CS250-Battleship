import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BattleShipGame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JPanel panel;
    private JComboBox<String> comboAIorPlayer;
    private JButton btnReady;

    /**
     * Launch the application.
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
     * Create the frame.
     */
    public BattleShipGame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new GridLayout(1, 0, 0, 0));
        
        panel = new JPanel();
        contentPane.add(panel);
        
        comboAIorPlayer = new JComboBox<>();
        comboAIorPlayer.setModel(new DefaultComboBoxModel<>(new String[] {"AI", "Player"}));
        panel.add(comboAIorPlayer);
        
        btnReady = new JButton("Ready?");
        panel.add(btnReady);

        // Add action listener to the button
        btnReady.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) comboAIorPlayer.getSelectedItem();
                if ("AI".equals(selected)) {
                    // Open the game screen for AI
                    GameAi gameai = new GameAi();
                    gameai.setVisible(true);
                    dispose(); // Close the current window
                } else {
                    // Logic for starting a player vs player game can go here
                    JOptionPane.showMessageDialog(null, "Starting player vs player game!");
                    // Optionally, create another game screen for player vs player
                }
            }
        });
    }
}
