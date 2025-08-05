import java.util.*;
public class Main {
    public static void main(String[] args) {
    	Scanner scanner = new Scanner(System.in);
    	System.out.print("Enter number of players: ");
    	int numPlayers = scanner.nextInt();
    	System.out.print("Enter number of rounds: ");
    	int rounds = scanner.nextInt();
    	System.out.print("Enter starting tokens per player: ");
    	int tokens = scanner.nextInt();
    	scanner.nextLine(); // consume the newline after nextInt()

    	Player[] players = new Player[numPlayers];
    	for (int i = 0; i < numPlayers; i++) {
    	    System.out.print("Enter name for Player " + (i + 1) + ": ");
    	    String name = scanner.nextLine(); // accepts full line including spaces
    	    players[i] = new Player(name, tokens);
    	}


        GundataGame game = new GundataGame(rounds, players);
        game.playGame();
    }
}
