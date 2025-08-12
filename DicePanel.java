import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class DicePanel extends JPanel {

    private final DieFace[] faces = new DieFace[6];
    private final Random rng = new Random();

    public DicePanel() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.CENTER, 12, 6));
        for (int i = 0; i < faces.length; i++) {
            faces[i] = new DieFace();
            faces[i].setPreferredSize(new Dimension(44, 44)); // medium
            add(faces[i]);
        }
    }

    /** Instantly set the 6 dice faces (values 1..6). */
    public void setFaces(int[] vals) {
        for (int i = 0; i < faces.length; i++) faces[i].setValue(vals[i]);
        repaint();
    }

    /** Simple animation that scrambles ~800ms then lands on target. */
    public void animateTo(final int[] target, final Runnable onStop) {
        final long start = System.currentTimeMillis();
        final Timer t = new Timer(50, null);
        t.addActionListener(e -> {
            long dt = System.currentTimeMillis() - start;
            if (dt < 800) {
                for (DieFace f : faces) f.setValue(rng.nextInt(6) + 1);
                repaint();
            } else {
                t.stop();
                setFaces(target);
                if (onStop != null) onStop.run();
            }
        });
        t.start();
    }

    /** Scale dice size when window resizes (called by GamePanel.applyScale). */
    public void applyScale(double s) {
        s = Math.max(0.80, Math.min(1.05, s));
        int side = (int)(44 * s);
        Dimension d = new Dimension(side, side);
        for (DieFace f : faces) {
            f.setPreferredSize(d);
            f.setMinimumSize(d);
        }
        revalidate();
        repaint();
    }

    // ---------- inner component that draws a die face ----------
    private static class DieFace extends JComponent {
        private int value = 1;

        public void setValue(int v) {
            value = Math.max(1, Math.min(6, v));
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            g2.setColor(new Color(250, 250, 250));
            g2.fillRoundRect(2, 2, w - 4, h - 4, 10, 10);
            g2.setColor(new Color(80, 80, 80));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(2, 2, w - 4, h - 4, 10, 10);

            int cx = w / 2, cy = h / 2;
            int off = Math.min(w, h) / 4;
            drawPips(g2, cx, cy, off, value);

            g2.dispose();
        }

        private void drawPips(Graphics2D g2, int cx, int cy, int off, int v) {
            g2.setColor(Color.BLACK);
            int r = Math.max(2, Math.min(5, Math.min(getWidth(), getHeight()) / 10));
            java.util.function.BiConsumer<Integer,Integer> pip =
                    (x, y) -> g2.fillOval(cx + x - r, cy + y - r, 2*r, 2*r);

            switch (v) {
                case 1 -> pip.accept(0, 0);
                case 2 -> { pip.accept(-off, -off); pip.accept(off, off); }
                case 3 -> { pip.accept(-off, -off); pip.accept(0, 0); pip.accept(off, off); }
                case 4 -> { pip.accept(-off, -off); pip.accept(off, -off); pip.accept(-off, off); pip.accept(off, off); }
                case 5 -> { pip.accept(-off, -off); pip.accept(off, -off); pip.accept(0, 0); pip.accept(-off, off); pip.accept(off, off); }
                case 6 -> { pip.accept(-off, -off); pip.accept(off, -off); pip.accept(-off, 0); pip.accept(off, 0); pip.accept(-off, off); pip.accept(off, off); }
            }
        }
    }
}
