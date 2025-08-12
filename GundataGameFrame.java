import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GundataGameFrame extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);

    private WelcomePanel welcomePanel;
    private PlayerSetupPanel setupPanel;
    private GamePanel gamePanel;

    public GundataGameFrame() {
        super("Gundata Dice Game (Beta)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Medium working size
        setSize(1200, 760);
        // IMPORTANT: enforce a taller minimum so the center board never gets squeezed
        // (top players ~200 + board >=240 + bottom stack ~200+  -> ~800)
        setMinimumSize(new Dimension(1120, 820));
        setLocationRelativeTo(null);

        welcomePanel = new WelcomePanel(this::onPlayersChosen);
        setupPanel = new PlayerSetupPanel(this::onSetupDone);

        root.add(welcomePanel, "WELCOME");
        root.add(setupPanel, "SETUP");
        add(root);

        cards.show(root, "WELCOME");
    }

    private void onPlayersChosen(int count) {
        setupPanel.prepareFor(count);
        cards.show(root, "SETUP");
    }

    private void onSetupDone(List<Player> players) {
        gamePanel = new GamePanel();
        GameController controller = new GameController(players, gamePanel);
        gamePanel.hook(controller);

        root.add(gamePanel, "GAME");
        cards.show(root, "GAME");
        controller.startRound();
    }
}
