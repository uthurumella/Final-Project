import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.IntConsumer;

public class GundataBoardPanel extends JPanel {

    private final Color[] colors = {
            new Color(220, 80, 60),   // 1 red
            new Color(240, 185, 60),  // 2 yellow
            new Color(67, 163, 97),   // 3 green
            new Color(57, 141, 255),  // 4 blue
            new Color(155, 89, 182),  // 5 purple
            new Color(241, 148, 138)  // 6 pink
    };

    private static final Stroke WIN_STROKE = new BasicStroke(3f);
    private static final Stroke SEL_STROKE = new BasicStroke(2f);

    private Integer selectedNumber = null;
    private Integer winningNumber = null;
    private IntConsumer onSelect;

    public GundataBoardPanel() {
        // Reduced footprint so it sits nicely between P1 (left) and P3 (right)
        setPreferredSize(new Dimension(340, 160));
        setMinimumSize(new Dimension(260, 140));
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int n = cellAt(e.getX(), e.getY());
                if (n >= 1 && n <= 6 && onSelect != null) onSelect.accept(n);
            }
        });
    }

    public void setOnSelect(IntConsumer cb) { this.onSelect = cb; }
    public void setSelectedNumber(Integer n) { this.selectedNumber = n; repaint(); }
    public void setWinningNumber(Integer n) { this.winningNumber = n; repaint(); }
    public void clearHighlights() { selectedNumber = null; winningNumber = null; repaint(); }

    private int cellAt(int x, int y) {
        Insets in = getInsets();
        int w = getWidth() - in.left - in.right;
        int h = getHeight() - in.top - in.bottom;
        if (w <= 0 || h <= 0) return -1;
        int cellW = w / 3, cellH = h / 2;
        int col = Math.min(2, Math.max(0, (x - in.left) / Math.max(1, cellW)));
        int row = Math.min(1, Math.max(0, (y - in.top) / Math.max(1, cellH)));
        return row * 3 + col + 1; // 1..6
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        Insets in = getInsets();
        int w = getWidth() - in.left - in.right;
        int h = getHeight() - in.top - in.bottom;
        int cellW = Math.max(1, w / 3);
        int cellH = Math.max(1, h / 2);

        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 3; c++) {
                int idx = r * 3 + c;
                int n = idx + 1;
                int x = in.left + c * cellW;
                int y = in.top + r * cellH;

                g2.setColor(colors[idx]);
                g2.fillRoundRect(x + 5, y + 5, cellW - 10, cellH - 10, 16, 16);

                boolean sel = selectedNumber != null && selectedNumber == n;
                boolean win = winningNumber != null && winningNumber == n;
                if (win) {
                    g2.setColor(Color.WHITE);
                    g2.setStroke(WIN_STROKE);
                    g2.drawRoundRect(x + 5, y + 5, cellW - 10, cellH - 10, 16, 16);
                } else if (sel) {
                    g2.setColor(new Color(255, 255, 255, 200));
                    g2.setStroke(SEL_STROKE);
                    g2.drawRoundRect(x + 5, y + 5, cellW - 10, cellH - 10, 16, 16);
                }

                g2.setFont(getFont().deriveFont(Font.BOLD, (float)(Math.min(cellW, cellH) * 0.42)));
                g2.setColor(Color.WHITE);
                String s = String.valueOf(n);
                FontMetrics fm = g2.getFontMetrics();
                int tx = x + (cellW - fm.stringWidth(s)) / 2;
                int ty = y + (cellH + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(s, tx, ty);
            }
        }
        g2.dispose();
    }
}
