import java.util.*;
public class DiceRoller {
    private int[] results = new int[6];
    private Map<Integer, Integer> frequencies;

    public void rollDice() {
        Random rand = new Random();
        frequencies = new HashMap<>();
        for (int i = 1; i <= 6; i++) frequencies.put(i, 0);

        for (int i = 0; i < 6; i++) {
            results[i] = rand.nextInt(6) + 1;
            frequencies.put(results[i], frequencies.get(results[i]) + 1);
        }
    }

    public int[] getResults() {
        return results;
    }

    public Map<Integer, Integer> getFrequencies() {
        return frequencies;
    }

    public int[] getWinningNumbers() {
        int maxFreq = Collections.max(frequencies.values());
        boolean allEqual = new HashSet<>(frequencies.values()).size() == 1;

        if (allEqual) return new int[0];

        List<Integer> winners = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            if (frequencies.get(i) == maxFreq) winners.add(i);
        }
        return winners.stream().mapToInt(i -> i).toArray();
    }
}