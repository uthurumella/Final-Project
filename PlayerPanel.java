import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class PlayerPanel extends JPanel {

    private final JLabel nameLbl = new JLabel("Player");
    private final JLabel tokensLbl = new JLabel("Tokens: 0");
    private final JLabel selectedLbl = new JLabel("Selected: —");
    private final JLabel stakeLbl = new JLabel("Stake: 0");
    private final JLabel msgLbl = new JLabel(" ");

    private final JButton confirmBtn = new JButton("✓ Confirm");
    private final JButton[] chipBtns;

    private final IntConsumer onChip;
    private final Runnable onConfirm;
    private final Consumer<Boolean> onActiveChanged;

    private boolean active = false;
    private boolean zeroTokens = false;
    private boolean postRollLocked = false;
    private boolean winner = false;
    private boolean zeroMark = false;

    public PlayerPanel(String name, IntConsumer onChip, Runnable onConfirm, Consumer<Boolean> onActiveChanged) {
        this.onChip = onChip;
        this.onConfirm = onConfirm;
        this.onActiveChanged = onActiveChanged;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Slightly larger fonts for clarity
        float nameFontSize = 16.5f;
        float labelFontSize = 13.2f;
        float msgFontSize = 12.1f;
        float chipFontSize = 11.5f;

        nameLbl.setText(name);
        nameLbl.setFont(nameLbl.getFont().deriveFont(Font.BOLD, nameFontSize));
        tokensLbl.setFont(tokensLbl.getFont().deriveFont(labelFontSize));
        selectedLbl.setFont(selectedLbl.getFont().deriveFont(labelFontSize));
        stakeLbl.setFont(stakeLbl.getFont().deriveFont(labelFontSize));
        msgLbl.setFont(msgLbl.getFont().deriveFont(Font.BOLD, msgFontSize));
        msgLbl.setForeground(Color.RED);
        add(pad(nameLbl, 5));

        JPanel infoRow = new JPanel(new BorderLayout());
        infoRow.setOpaque(false);
        JPanel leftWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftWrap.setOpaque(false);
        leftWrap.add(tokensLbl);
        JPanel rightWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightWrap.setOpaque(false);
        rightWrap.add(stakeLbl);
        infoRow.add(leftWrap, BorderLayout.WEST);
        infoRow.add(rightWrap, BorderLayout.EAST);
        add(pad(infoRow, 3));

        add(pad(selectedLbl, 3));

        JPanel chips = new JPanel(new GridLayout(1, 5, 8, 0));
        chips.setOpaque(false);
        int[] vals = {1, 5, 10, 20, 50};
        chipBtns = new JButton[vals.length];
        for (int i = 0; i < vals.length; i++) {
            int v = vals[i];
            JButton b = new JButton(String.valueOf(v));
            b.setFont(b.getFont().deriveFont(chipFontSize));
            b.setMargin(new Insets(2, 3, 2, 3));
            Dimension cs = new Dimension(33, 22); // slightly larger buttons
            b.setPreferredSize(cs);
            b.setMinimumSize(cs);
            b.setMaximumSize(cs);
            b.addActionListener(e -> { if (!zeroTokens && !postRollLocked && !zeroMark) onChip.accept(v); });
            chipBtns[i] = b;
            chips.add(b);
        }
        add(pad(chips, 6)); // a touch more spacing

        confirmBtn.setFont(confirmBtn.getFont().deriveFont(chipFontSize));
        confirmBtn.setPreferredSize(new Dimension(143, 26));
        confirmBtn.addActionListener(e -> onConfirm.run());
        add(pad(confirmBtn, 8));

        add(pad(msgLbl, 2)); // extra bottom space for readability

        // Increased overall size for clearer text
        setPreferredSize(new Dimension(208, 185));
        setMinimumSize(new Dimension(198, 175));
        setMaximumSize(new Dimension(208, 185));

    }

    private static JComponent pad(JComponent c, int t) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(t, 12, t, 12));
        p.add(c);
        return p;
    }

    public void setWarning(String msg) {
        msgLbl.setText((msg == null || msg.isEmpty()) ? " " : msg);
    }

    public void setTokens(int tokens) {
        tokensLbl.setText("Tokens: " + Math.max(0, tokens));
        zeroTokens = tokens <= 0;
        updateControlsEnabled();
    }

    public void setSelected(Integer n) { selectedLbl.setText("Selected: " + (n == null ? "—" : n)); }
    public void setStake(int stake) { stakeLbl.setText("Stake: " + Math.max(0, stake)); }

    public void setWinner(boolean isWinner) { this.winner = isWinner; repaint(); }

    public void setZeroTokenMark(boolean isZero) {
        this.zeroMark = isZero;
        if (zeroMark) msgLbl.setText("Better luck next time");
        updateControlsEnabled();
        repaint();
    }

    public void setActive(boolean active) {
        this.active = active;
        updateControlsEnabled();
        if (onActiveChanged != null) onActiveChanged.accept(active);
        repaint();
    }

    public void setConfirmedUI() { updateControlsEnabled(); }

    private void updateControlsEnabled() {
        boolean chipsEnabled = active && !zeroTokens && !postRollLocked && !zeroMark;
        for (JButton b : chipBtns) b.setEnabled(chipsEnabled);
        boolean confirmEnabled = active && !postRollLocked && !zeroMark;
        confirmBtn.setEnabled(confirmEnabled);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        Color borderColor = Color.GRAY;
        if (winner) borderColor = Color.GREEN;
        else if (zeroMark) borderColor = Color.RED;
        else if (active) borderColor = Color.BLUE;

        // Background fill
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);

        // Border (thicker and fully visible)
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(4f)); // thicker for visibility
        g2.drawRect(2, 2, w - 5, h - 5); // inset so all sides show

        g2.dispose();
    }
}