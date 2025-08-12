import javax.swing.*;
import java.awt.*;
import java.util.function.IntConsumer;

public class WelcomePanel extends JPanel {

    public WelcomePanel(IntConsumer onPlayersChosen) {
        setLayout(new BorderLayout());
        setBackground(new Color(6, 94, 47));

        JLabel title = new JLabel("Welcome to Gundata Game", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 36f));
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        JPanel btns = new JPanel(new GridLayout(2, 3, 16, 16));
        btns.setOpaque(false);
        for (int i = 1; i <= 6; i++) {
            final int count = i;
            JButton b = new JButton(i + " Players");
            b.setFont(b.getFont().deriveFont(Font.BOLD, 22f));
            b.addActionListener(e -> onPlayersChosen.accept(count));
            btns.add(b);
        }

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new GridBagLayout());
        center.add(btns);
        add(center, BorderLayout.CENTER);

        JLabel hint = new JLabel("Choose the number of players (1–6) to begin", SwingConstants.CENTER);
        hint.setForeground(new Color(230, 255, 230));
        hint.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        add(hint, BorderLayout.SOUTH);
    }
}
