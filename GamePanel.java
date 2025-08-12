import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {

    // Exposed UI bits
    final JLabel winnerLbl = new JLabel(" ", SwingConstants.CENTER);
    final JLabel ownerProfitLbl = new JLabel("Owner Profit: 0");
    final JButton rollBtn = new JButton("Roll Dice");
    final JButton nextRoundBtn = new JButton("Next Round");
    final JButton resetBtn = new JButton("Reset Game");

    final GundataBoardPanel board = new GundataBoardPanel();
    final DicePanel dicePanel = new DicePanel();

    // Framed game area (inside a scroll pane)
    private JPanel outerBoard;
    private JScrollPane scroller;

    // Top area (players + board-at-top-center)
    private JPanel topArea;        // BorderLayout
    private JPanel northBar;       // holds P1 (W), board (CENTER), P3 (E)
    private JPanel northLeftSlot;  // P1 slot
    private JPanel northRightSlot; // P3 slot
    private JPanel westStrip;      // kept slim
    private JPanel eastStrip;

    // Bottom area (under board)
    private JPanel bottomArea;     // BoxLayout.Y
    private JPanel southBar;       // P2 | (P5) | (P6) | P4
    private JPanel southLeftSlot;      // P2
    private JPanel southMidLeftSlot;   // P5
    private JPanel southMidRightSlot;  // P6
    private JPanel southRightSlot;     // P4
    private JPanel diceRow;

    // Invisible split to balance top/bottom
    private JSplitPane split;

    final List<PlayerPanel> playerPanels = new ArrayList<>();
    private GameController controller;

    private static final Color APP_GREEN   = new Color(6, 94, 47);
    private static final Color FRAME_WHITE = Color.WHITE;

    public GamePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(APP_GREEN);

        // Title
        JLabel header = new JLabel("Gundata Game Board", SwingConstants.CENTER);
        header.setForeground(Color.WHITE);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 26f));
        header.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        add(header, BorderLayout.NORTH);

        // === Framed game area (WHITE border) ===
        outerBoard = new JPanel(new BorderLayout(8, 8));
        outerBoard.setOpaque(true);
        outerBoard.setBackground(APP_GREEN);
        outerBoard.setBorder(BorderFactory.createLineBorder(FRAME_WHITE, 8, true));

        // Preferred sizes so scrollbars kick in when window is smaller
        outerBoard.setPreferredSize(new Dimension(960, 700));
        outerBoard.setMinimumSize(new Dimension(760, 560));

        // === Put the framed area inside a SCROLL PANE (both directions) ===
        scroller = new JScrollPane(
                outerBoard,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        scroller.setBorder(null);
        scroller.setOpaque(true);
        scroller.getViewport().setBackground(APP_GREEN);
        scroller.getVerticalScrollBar().setUnitIncrement(18);
        scroller.getHorizontalScrollBar().setUnitIncrement(18);
        add(scroller, BorderLayout.CENTER);

        // ===== TOP area =====
        topArea = new JPanel(new BorderLayout(8, 8));
        topArea.setOpaque(true);
        topArea.setBackground(APP_GREEN);

        // --- top bar: P1 (left), BOARD (center), P3 (right) ---
        northBar = new JPanel(new BorderLayout());
        northBar.setOpaque(true);
        northBar.setBackground(APP_GREEN);
        northBar.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 4));

        northLeftSlot  = makeSideBox(FlowLayout.LEFT);
        northRightSlot = makeSideBox(FlowLayout.RIGHT);

        JPanel boardCenterRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        boardCenterRow.setOpaque(false);
        boardCenterRow.add(board);   // board sits between P1 and P3, top-centered

        northBar.add(northLeftSlot, BorderLayout.WEST);
        northBar.add(boardCenterRow, BorderLayout.CENTER);
        northBar.add(northRightSlot, BorderLayout.EAST);

        // Increased from 187 to 205
        topArea.add(fixedHeightBox(northBar, 205), BorderLayout.NORTH);

        // Keep side strips slim
        westStrip = new JPanel();
        westStrip.setOpaque(true);
        westStrip.setBackground(APP_GREEN);
        westStrip.setPreferredSize(new Dimension(12, 10));

        eastStrip = new JPanel();
        eastStrip.setOpaque(true);
        eastStrip.setBackground(APP_GREEN);
        eastStrip.setPreferredSize(new Dimension(12, 10));

        topArea.add(westStrip, BorderLayout.WEST);
        topArea.add(eastStrip, BorderLayout.EAST);

        // ===== BOTTOM area =====
        bottomArea = new JPanel();
        bottomArea.setOpaque(true);
        bottomArea.setBackground(APP_GREEN);
        bottomArea.setLayout(new BoxLayout(bottomArea, BoxLayout.Y_AXIS));

        // Bottom bar: P2 | (P5) | (P6) | P4 — fixed-size slots (no stretching)
        southBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        southBar.setOpaque(true);
        southBar.setBackground(APP_GREEN);

        southLeftSlot     = makeSideBox(FlowLayout.CENTER);
        southMidLeftSlot  = makeSideBox(FlowLayout.CENTER);
        southMidRightSlot = makeSideBox(FlowLayout.CENTER);
        southRightSlot    = makeSideBox(FlowLayout.CENTER);

        southBar.add(southLeftSlot);
        southBar.add(southMidLeftSlot);
        southBar.add(southMidRightSlot);
        southBar.add(southRightSlot);

        // Increased from 187 to 205
        bottomArea.add(fixedHeightBox(southBar, 205));

        diceRow = new JPanel();
        diceRow.setOpaque(true);
        diceRow.setBackground(APP_GREEN);
        diceRow.add(dicePanel);
        setFixedHeight(diceRow, 64);
        bottomArea.add(diceRow);

        JPanel btnRow = new JPanel();
        btnRow.setOpaque(true);
        btnRow.setBackground(APP_GREEN);
        btnRow.add(rollBtn);
        btnRow.add(nextRoundBtn);
        btnRow.add(resetBtn);
        setFixedHeight(btnRow, 50);
        bottomArea.add(btnRow);

        winnerLbl.setForeground(Color.YELLOW);
        winnerLbl.setFont(winnerLbl.getFont().deriveFont(Font.BOLD, 18f));
        winnerLbl.setOpaque(true);
        winnerLbl.setBackground(APP_GREEN);
        setFixedHeight(winnerLbl, 26);
        bottomArea.add(winnerLbl);

        JPanel profitRow = new JPanel();
        profitRow.setOpaque(true);
        profitRow.setBackground(APP_GREEN);
        ownerProfitLbl.setForeground(Color.WHITE);
        ownerProfitLbl.setFont(ownerProfitLbl.getFont().deriveFont(Font.BOLD, 14f));
        profitRow.add(ownerProfitLbl);
        setFixedHeight(profitRow, 24);
        bottomArea.add(profitRow);

        // ===== Invisible split to balance top/bottom (no divider line) =====
        split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topArea, bottomArea);
        split.setResizeWeight(0.68);
        split.setContinuousLayout(true);
        split.setBorder(null);
        split.setOpaque(false);
        split.setUI(new BasicSplitPaneUI() {
            @Override public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    @Override public void setBorder(Border b) { /* no border */ }
                    @Override public void paint(Graphics g) { /* no paint */ }
                };
            }
        });
        split.setDividerSize(0);

        // Add the split to the framed area
        outerBoard.add(split, BorderLayout.CENTER);
    }

    // ---------- helpers ----------
    private JPanel fixedHeightBox(JComponent child, int h) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(true);
        p.setBackground(APP_GREEN);
        p.add(child, BorderLayout.CENTER);
        setFixedHeight(p, h);
        return p;
    }

    private void setFixedHeight(JComponent c, int h) {
        Dimension pref = new Dimension(1, h);
        c.setPreferredSize(pref);
        c.setMinimumSize(pref);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, h));
        c.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    public void hook(GameController controller) {
        this.controller = controller;
        board.setOnSelect(controller::onNumberSelected);
        rollBtn.addActionListener(e -> controller.onRollDice());
        nextRoundBtn.addActionListener(e -> controller.startRound());
        resetBtn.addActionListener(e -> controller.resetGame());
    }

    /** Place players; P5/P6 go between P2 and P4 at the bottom. */
    public void buildPlayerPanels(java.util.List<Player> players, GameController controller) {
        // clear slots
        northLeftSlot.removeAll();
        northRightSlot.removeAll();
        southLeftSlot.removeAll();
        southMidLeftSlot.removeAll();
        southMidRightSlot.removeAll();
        southRightSlot.removeAll();
        westStrip.removeAll();
        eastStrip.removeAll();
        playerPanels.clear();

        Point[] slots = slotsForCount(players.size());

        for (int i = 0; i < players.size(); i++) {
            final int idx = i;
            Player p = players.get(i);
            PlayerPanel pp = new PlayerPanel(
                    p.getName(),
                    v -> controller.onChipPressed(idx, v),
                    () -> controller.onConfirm(idx),
                    active -> {}
            );

          
            Dimension fixed = new Dimension(208, 185);
            pp.setPreferredSize(fixed);
            pp.setMinimumSize(new Dimension(198, 175));
            pp.setMaximumSize(fixed);


            pp.setTokens(p.getTokens());
            pp.setSelected(null);
            pp.setStake(0);
            playerPanels.add(pp);

            Point s = slots[i];
            if (s.x == 0 && s.y == 0)      northLeftSlot.add(pp);      // P1 NW
            else if (s.x == 2 && s.y == 0) northRightSlot.add(pp);     // P3 NE
            else if (s.x == 0 && s.y == 2) southLeftSlot.add(pp);      // P2 bottom-left
            else if (s.x == 2 && s.y == 2) southRightSlot.add(pp);     // P4 bottom-right
            else if (s.x == 0 && s.y == 1) southMidLeftSlot.add(pp);   // P5 between
            else if (s.x == 2 && s.y == 1) southMidRightSlot.add(pp);  // P6 between
        }

        revalidate();
        repaint();
    }

    private static JPanel makeSideBox(int align) {
        JPanel box = new JPanel(new FlowLayout(align, 0, 0));
        box.setOpaque(true);
        box.setBackground(APP_GREEN);
        return box;
    }

    private static Point[] slotsForCount(int n) {
        java.util.List<Point> list = new java.util.ArrayList<>();
        if (n == 1) list.add(new Point(0,0));
        if (n >= 2) { list.add(new Point(0,0)); list.add(new Point(2,2)); }
        if (n >= 3) list.add(new Point(2,0));
        if (n >= 4) list.add(new Point(0,2));
        if (n >= 5) list.add(new Point(0,1));   // P5 bottom mid-left
        if (n >= 6) list.add(new Point(2,1));   // P6 bottom mid-right
        return list.toArray(new Point[0]);
    }
}
