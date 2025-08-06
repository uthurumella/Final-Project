import java.util.*;
public class GundataGame {
    private int totalRounds;
    private int currentRound = 1;
    private Player[] players;
    private DiceRoller diceRoller;
    private Scanner scanner;
    private int ownerProfit = 0;

    public GundataGame(int totalRounds, Player[] players) {
        this.totalRounds = totalRounds;
        this.players = players;
        this.diceRoller = new DiceRoller();
        this.scanner = new Scanner(System.in);
    }

    public void playGame() {
        while (currentRound <= totalRounds) {
            System.out.println("\n--- Round " + currentRound + " ---");
            collectPredictions();
            diceRoller.rollDice();

            int[] results = diceRoller.getResults();
            System.out.print("Dice rolled: ");
            for (int i = 0; i < results.length; i++) {
                System.out.print("[Dice " + (i + 1) + ": " + results[i] + "] ");
            }
            System.out.println();

            System.out.println("\nNumber Frequencies:");
            Map<Integer, Integer> freq = diceRoller.getFrequencies();
            for (int i = 1; i <= 6; i++) {
                System.out.println("Number " + i + ": " + freq.get(i) + " time(s)");
            }

            int[] winningNumbers = diceRoller.getWinningNumbers();
            System.out.print("\n🎯 Winning number(s): ");
            for (int w : winningNumbers) System.out.print(w + " ");
            System.out.println("\n");

            for (Player p : players) {
                p.showPredictions();
                ownerProfit += p.evaluateRound(freq);
                p.printBalance();
            }
            System.out.println("🏦 Owner Profit after Round " + currentRound + ": " + ownerProfit + " tokens\n");
            currentRound++;
        }
        declareWinner();
        System.out.println("\n🏁 Final Owner Profit: " + ownerProfit + " tokens");
    }

    private void collectPredictions() {
        for (Player p : players) {
            if (p.getBalance() <= 0) {
                System.out.println("\n" + p.getName() + " has 0 tokens and is Eliminated.");
                continue;
            }

            System.out.println("\n" + p.getName() + "'s turn (Tokens: " + p.getBalance() + "):");

            while (true) {
                System.out.print("Enter number and tokens (e.g., '4 30'): ");
                int num = scanner.nextInt();
                int tokens = scanner.nextInt();

                if (num < 1 || num > 6 || tokens <= 0) {
                    System.out.println("Invalid number or token amount. Try again.");
                    continue;
                }

                if (!p.makePrediction(num, tokens)) {
                    System.out.println("Insufficient tokens. Try again.");
                } else {
                    break; // Accept only ONE valid prediction
                }
            }
        }
    }


    private void declareWinner() {
        int maxBalance = 0;
        for (Player p : players) {
            if (p.getBalance() > maxBalance) {
                maxBalance = p.getBalance();
            }
        }

        List<String> winners = new ArrayList<>();
        for (Player p : players) {
            if (p.getBalance() == maxBalance) {
                winners.add(p.getName());
            }
        }

        System.out.println("\n Winner(s): " + String.join(", ", winners) + " with " + maxBalance + " tokens!");
    }

}
