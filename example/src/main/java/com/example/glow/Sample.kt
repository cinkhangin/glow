@file:Suppress("unused")

package com.example.glow

object Sample {
    val Java = """
        import java.util.Random;
        import java.util.Scanner;

        public class CoinFlippingGame {
            public static String flipCoin() {
                HashMap<String, Integer> map = new HashMap<>();
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
    """.trimIndent()

    val JavaScript = """
        function flipCoin() {
          const randomValue = Math.random();

          if (randomValue < 0.5) {
            return "Heads!";
          } else {
            return "Tails!";
          }
        }

        function playGame() {
          console.log("Coin Flipping Game");
          console.log("Press Enter to flip the coin. Type 'exit' to quit.");

          const readline = require("readline");
          const rl = readline.createInterface({
            input: process.stdin,
            output: process.stdout,
          });

          rl.on("line", (input) => {
            if (input.toLowerCase() === "exit") {
              rl.close();
              console.log("Game over!");
            } else {
              const result = flipCoin();
              console.log("Flipping the coin...");
              setTimeout(() => {
                console.log(`The coin shows: ${"$"}{result}`);
                console.log("Press Enter to flip again or type 'exit' to quit.");
              }, 1000);
            }
          });
        }

        playGame();
    """.trimIndent()

    val Kotlin = """
        import java.util.Locale
        import kotlin.random.Random

        fun main() {
            val coin = "Coin"
            println("Welcome to the ${"$"}coin Flipping Game!")
            println("I will flip a ${"$"}{coin}, and you have to guess the outcome.")
            println("Enter 'H' for Heads \n or 'T' for Tails.")

            var playerGuess: String
            var computerResult: String
            var validInput: Boolean

            fun flipCoin(): String {
                val randomValue = Random.nextInt(2)
                return if (randomValue == 0) "Heads" else "Tails"
            }

            do {
                print("Enter your guess (H/T): ")
                playerGuess = readlnOrNull()?.trim()?.uppercase(Locale.getDefault()) ?: ""
                validInput = playerGuess == "H" || playerGuess == "T"
                if (!validInput) {
                    println("Invalid input. Please enter 'H' for Heads or 'T' for Tails.")
                }
            } while (!validInput)

            computerResult = flipCoin()

            println("Flipping the coin...")
            Thread.sleep(2000) // Delay for 2 seconds to add some suspense

            println("The coin shows: ${"$"}computerResult")

            if (playerGuess == "H" && computerResult == "Heads" || playerGuess == "T" && computerResult == "Tails") {
                println("Congratulations! You guessed correctly!")
            } else {
                println("Sorry, you guessed incorrectly.")
            }
        }
    """.trimIndent()

    val Python = """
        import random

        def flip_coin():
            return random.choice(["Heads", "Tails"])
        
        def main():
            print('Coin Flipping Game')
            print("Press Enter to flip the coin. Type 'exit' to quit.")
        
            while True:
                user_input = input()
                if user_input.lower() == "exit":
                    print("Game over!")
                    break
                elif user_input == "":
                    result = flip_coin()
                    print("Flipping the coin...")
                    print(f"The coin shows: {result}")
                    print("Press Enter to flip again or type 'exit' to quit.")
        
        if __name__ == "__main__":
            main()
    """.trimIndent()
}