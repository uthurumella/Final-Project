import java.util.Random;

public class DiceRoller {
    private final Random rng = new Random();

    /**Returns 6 dice results, each in [1..6]. */
    public int[] rollSix() {
        int[] r = new int[6];
        for (int i = 0; i < 6; i++) r[i] = rng.nextInt(6) + 1;
        return r;
    }

    public int rollOne() { return rng.nextInt(6) + 1; }
}
