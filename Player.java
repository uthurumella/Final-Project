import java.util.*;
public class Player {
    private String name;
    private int balance;
    private Map<Integer, Integer> predictions = new HashMap<>();

    public Player(String name, int balance) {
        this.name = name;
        this.balance = balance;
    }

    public boolean makePrediction(int number, int tokens) {
        if (balance >= tokens && tokens > 0) {
            predictions.clear(); // Only one prediction per round
            predictions.put(number, tokens);
            balance -= tokens;
            return true;
        }
        return false;
    }


    public int evaluateRound(Map<Integer, Integer> freq) {
        int totalLost = 0;
        int totalWon = 0;
        boolean allEqual = new HashSet<>(freq.values()).size() == 1;

        for (Map.Entry<Integer, Integer> entry : predictions.entrySet()) {
            int number = entry.getKey();
            int bet = entry.getValue();
            int count = freq.getOrDefault(number, 0);
            int multiplier = 0;

            if (!allEqual) {
                switch (count) {
                    case 2: multiplier = 2; break;
                    case 3: multiplier = 3; break;
                    case 5: multiplier = 5; break;
                }
            }

            int reward = bet * multiplier;
            if (reward == 0) totalLost += bet;
            else totalWon += reward;
            balance += reward;
        }
        predictions.clear();
        return totalLost;
    }

    public void printBalance() {
        System.out.println(name + " Tokens: " + balance);
    }

    public void showPredictions() {
        System.out.println(name + "'s predictions this round:");
        if (predictions.isEmpty()) {
            System.out.println("  No tokes placed.");
        } else {
            for (Map.Entry<Integer, Integer> entry : predictions.entrySet()) {
                System.out.println("  Number " + entry.getKey() + " → " + entry.getValue() + " token(s)");
            }
        }
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }
}
