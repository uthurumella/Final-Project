import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GundataGameFrame frame = new GundataGameFrame();
            frame.setVisible(true);
        });
    }
}

