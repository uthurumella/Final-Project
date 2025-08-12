import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GameController {

    private final List<Player> players;
    private final GamePanel ui;
    private final DiceRoller dice = new DiceRoller();

    private int activeIndex = 0;
    private int ownerProfitTotal = 0;

    public GameController(List<Player> players, GamePanel ui) {
        this.players = new ArrayList<>(players);
        this.ui = ui;
        this.ui.buildPlayerPanels(players, this);
        ui.rollBtn.setEnabled(false);
        ui.nextRoundBtn.setEnabled(false);
        ui.winnerLbl.setText(" ");
        updateOwnerProfitLabel(); // show Profit/Loss based on current total (0 => Profit: 0)
    }

    public void startRound() {
        for (Player p : players) p.resetForRound();
        ui.board.clearHighlights();
        ui.winnerLbl.setText(" ");
        ui.rollBtn.setEnabled(false);
        ui.nextRoundBtn.setEnabled(false);

        // Prepare panels and auto-lock zero-token players so the round can proceed
        boolean allZeroStart = true;
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            PlayerPanel pp = ui.playerPanels.get(i);

            pp.setTokens(p.getTokens());
            pp.setSelected(p.getChosenNumber());
            pp.setStake(p.getCurrentStake());
            pp.setWarning(" ");
            pp.setActive(false);
            pp.setWinner(false);

            if (p.getTokens() <= 0) {
                // Keep red border + message, disable participation, and auto-confirm
                pp.setZeroTokenMark(true);
                p.setConfirmed(true);
                pp.setConfirmedUI();
                pp.setWarning("Better luck next time");
            } else {
                allZeroStart = false;
                pp.setZeroTokenMark(false);
            }
        }

        if (allZeroStart) {
            // Nobody can play; keep both buttons disabled and guide to Reset.
            ui.winnerLbl.setText("All players are out of tokens. Please click Reset Game.");
            ui.rollBtn.setEnabled(false);
            ui.nextRoundBtn.setEnabled(false);
            return;
        }

        activeIndex = 0;
        stepToNextActive();
    }

    public void resetGame() {
        SwingUtilities.getWindowAncestor(ui).dispose();
        new GundataGameFrame().setVisible(true);
    }

    private void stepToNextActive() {
        int count = players.size();
        for (int k = 0; k < count; k++) {
            int idx = (activeIndex + k) % count;
            if (!players.get(idx).isConfirmed()) {
                setActive(idx);
                return;
            }
        }
        setActive(-1);
        ui.rollBtn.setEnabled(true);
        ui.winnerLbl.setText("All players confirmed. Ready to roll.");
    }

    private void setActive(int idx) {
        for (int i = 0; i < ui.playerPanels.size(); i++) {
            ui.playerPanels.get(i).setActive(i == idx);
            if (i == idx) {
                ui.playerPanels.get(i).setWarning("Your turn: choose a number and stake, then Confirm.");
            } else if (!players.get(i).isConfirmed()) {
                ui.playerPanels.get(i).setWarning("Waiting...");
            } else {
                ui.playerPanels.get(i).setWarning("Confirmed");
            }
        }
        activeIndex = idx;
        ui.board.setSelectedNumber(null);
    }

    public void onNumberSelected(int n) {
        if (activeIndex < 0) return;
        Player p = players.get(activeIndex);
        if (p.isConfirmed()) return;
        p.setChosenNumber(n);
        ui.board.setSelectedNumber(n);
        ui.playerPanels.get(activeIndex).setSelected(n);
        ui.playerPanels.get(activeIndex).setWarning("Number selected: " + n);
    }

    public void onChipPressed(int idx, int value) {
        if (idx != activeIndex) return;
        Player p = players.get(idx);
        if (p.isConfirmed()) return;

        if (p.tryDeduct(value)) {
            p.addToStake(value);
            ui.playerPanels.get(idx).setTokens(p.getTokens());
            ui.playerPanels.get(idx).setStake(p.getCurrentStake());
            ui.playerPanels.get(idx).setWarning("Added " + value + " tokens to stake.");
        } else {
            ui.playerPanels.get(idx).setWarning("Insufficient tokens.");
        }
    }

    public void onConfirm(int idx) {
        if (idx != activeIndex) return;
        Player p = players.get(idx);
        if (p.isConfirmed()) return;

        if (p.getTokens() == 0 && p.getCurrentStake() == 0) {
            // This case should be rare now; zero-token players are auto-confirmed in startRound.
            p.setConfirmed(true);
            ui.playerPanels.get(idx).setConfirmedUI();
            ui.winnerLbl.setText(p.getName() + " has no tokens this round.");
            stepToNextActive();
            return;
        }
        if (p.getChosenNumber() == null) {
            ui.playerPanels.get(idx).setWarning("Pick a number on the board first.");
            return;
        }
        if (p.getCurrentStake() <= 0) {
            ui.playerPanels.get(idx).setWarning("You must stake tokens before confirming.");
            return;
        }

        p.setConfirmed(true);
        ui.playerPanels.get(idx).setConfirmedUI();
        stepToNextActive();
    }

    public void onRollDice() {
        ui.rollBtn.setEnabled(false);
        ui.nextRoundBtn.setEnabled(false);
        ui.board.setWinningNumber(null);
        ui.winnerLbl.setText("Rolling...");
        performRollWithTieResolution();
    }

    private void performRollWithTieResolution() {
        int[] faces = dice.rollSix();
        ui.dicePanel.animateTo(faces, () -> {
            int[] freq = new int[7]; // 1..6
            for (int f : faces) freq[f]++;

            int max = 0, winner = 1, ties = 0;
            for (int n = 1; n <= 6; n++) {
                if (freq[n] > max) { max = freq[n]; winner = n; ties = 1; }
                else if (freq[n] == max) ties++;
            }

            if (ties > 1) {
                ui.winnerLbl.setText("Tie on highest frequency. Re-rolling...");
                Timer delay = new Timer(650, e -> performRollWithTieResolution());
                delay.setRepeats(false);
                delay.start();
            } else {
                resolveRound(winner, freq);
            }
        });
    }

    private void resolveRound(int winner, int[] freq) {
        ui.board.setWinningNumber(winner); // keep the visual highlight on the board

        int totalStaked = 0;
        int totalPayout = 0;
        List<String> winners = new ArrayList<>();

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            totalStaked += p.getCurrentStake();

            if (p.getChosenNumber() != null
                    && p.getChosenNumber() == winner
                    && p.getCurrentStake() > 0) {

                int stake = p.getCurrentStake();
                int payout = stake * freq[winner];
                int profitForPlayer = payout - stake; // show profit

                p.addTokens(payout);
                totalPayout += payout;

                winners.add(p.getName() + " earned a profit of " + profitForPlayer + " tokens");
            }
        }

        int profit = totalStaked - totalPayout;
        ownerProfitTotal += profit;
        updateOwnerProfitLabel();

        // Refresh tokens and apply highlights (green for winners, red for zero-tokens)
        boolean allZeroEnd = true;
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            ui.playerPanels.get(i).setTokens(p.getTokens());

            boolean isRoundWinner = (p.getChosenNumber() != null
                    && p.getChosenNumber() == winner
                    && p.getCurrentStake() > 0);
            ui.playerPanels.get(i).setWinner(isRoundWinner);

            boolean zeroTokensNow = p.getTokens() <= 0;
            ui.playerPanels.get(i).setZeroTokenMark(zeroTokensNow);

            if (p.getTokens() > 0) {
                allZeroEnd = false;
            }
        }

        // Message with no winning-number text and no frequency info.
        String message = winners.isEmpty()
                ? "No winners this round."
                : "Congratulations: " + String.join(", ", winners);

        // If everyone is out of tokens, disable Next Round and guide to Reset.
        if (allZeroEnd) {
            ui.winnerLbl.setText(message + " All players are out of tokens. Please click Reset Game.");
            ui.nextRoundBtn.setEnabled(false);
        } else {
            ui.winnerLbl.setText(message);
            ui.nextRoundBtn.setEnabled(true);
        }
    }

    // --- helper to show Profit/Loss with correct wording ---
    private void updateOwnerProfitLabel() {
        if (ownerProfitTotal >= 0) {
            ui.ownerProfitLbl.setText("Owner Profit: " + ownerProfitTotal+ " tokens");
        } else {
            ui.ownerProfitLbl.setText("Owner Loss: " + Math.abs(ownerProfitTotal)+ " tokens");
        }
    }
}
