import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PlayerSetupPanel extends JPanel {

    private int playerCount = 2;
    private final JPanel namesPanel = new JPanel(new GridLayout(0, 2, 8, 8));
    private final List<JTextField> nameFields = new ArrayList<>();
    private final JSpinner tokenSpinner = new JSpinner(new SpinnerNumberModel(100, 0, 100000, 1));
    private final Consumer<List<Player>> onSetupDone;

    public PlayerSetupPanel(Consumer<List<Player>> onSetupDone) {
        this.onSetupDone = onSetupDone;
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(12, 122, 61));

        JLabel title = new JLabel("Player Setup", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 30f));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JPanel tokensRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tokensRow.setOpaque(false);
        JLabel tLbl = new JLabel("Starting tokens (applies to all players):");
        tLbl.setForeground(Color.WHITE);
        tokensRow.add(tLbl);
        tokenSpinner.setPreferredSize(new Dimension(100, 28));
        tokensRow.add(tokenSpinner);
        form.add(tokensRow);

        namesPanel.setOpaque(false);
        namesPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        form.add(namesPanel);

        JButton cont = new JButton("Continue");
        cont.setFont(cont.getFont().deriveFont(Font.BOLD, 18f));
        cont.addActionListener(e -> buildPlayers());
        JPanel btnRow = new JPanel();
        btnRow.setOpaque(false);
        btnRow.add(cont);
        form.add(btnRow);

        add(form, BorderLayout.CENTER);
    }

    public void prepareFor(int playerCount) {
        this.playerCount = playerCount;
        namesPanel.removeAll();
        nameFields.clear();

        for (int i = 1; i <= playerCount; i++) {
            JLabel l = new JLabel("Player " + i + " name:");
            l.setForeground(Color.WHITE);
            JTextField tf = new JTextField(14);
            tf.setText("P" + i);
            nameFields.add(tf);
            namesPanel.add(l);
            namesPanel.add(tf);
        }
        revalidate();
        repaint();
    }

    private void buildPlayers() {
        int startTokens = (Integer) tokenSpinner.getValue();
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            String name = nameFields.get(i).getText().trim();
            if (name.isEmpty()) name = "P" + (i + 1);
            players.add(new Player(name, startTokens));
        }
        onSetupDone.accept(players);
    }
}

