import java.util.Random;
import java.util.Scanner;

public class CoinFlippingGame {
    public static String flipCoin() {
        Random random = new Random();
        int result = random.nextInt(2); // 0 for Heads, 1 for Tails
        return (result == 0) ? "Heads" : "Tails";
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Coin Flipping Game");
        System.out.println("Press Enter to flip the coin. Type 'exit' to quit.");

        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Game over!");
                break;
            } else if (input.isEmpty()) {
                String result = flipCoin();
                System.out.println("Flipping the coin...");
                try {
                    Thread.sleep(1000); // Wait for 1 second for suspense
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("The coin shows: " + result);
                System.out.println("Press Enter to flip again or type 'exit' to quit.");
            }
        }

        scanner.close();
    }
}
