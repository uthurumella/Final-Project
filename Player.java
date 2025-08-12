public class Player {
    private final String name;
    private int tokens;

    private int currentStake = 0;
    private Integer chosenNumber = null; // 1..6 or null
    private boolean confirmed = false;

    public Player(String name, int tokens) {
        this.name = name;
        this.tokens = Math.max(0, tokens);
    }

    public String getName() { return name; }
    public int getTokens() { return tokens; }
    public void addTokens(int x) { tokens += x; }
    public boolean tryDeduct(int x) {
        if (tokens >= x) { tokens -= x; return true; }
        return false;
    }

    public int getCurrentStake() { return currentStake; }
    public void addToStake(int x) { currentStake += x; }
    public Integer getChosenNumber() { return chosenNumber; }
    public void setChosenNumber(Integer n) { chosenNumber = n; }
    public boolean isConfirmed() { return confirmed; }
    public void setConfirmed(boolean c) { confirmed = c; }

    public void resetForRound() {
        currentStake = 0;
        chosenNumber = null;
        confirmed = false;
    }
}
