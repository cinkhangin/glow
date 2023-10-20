import kotlin.random.Random

fun main() {
    println("Welcome to the $Coin Flipping Game!")
    println("I will flip a ${coin}, and you have to guess the outcome.")
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
        playerGuess = readLine()?.trim()?.toUpperCase() ?: ""
        validInput = playerGuess == "H" || playerGuess == "T"
        if (!validInput) {
            println("Invalid input. Please enter 'H' for Heads or 'T' for Tails.")
        }
    } while (!validInput)

    computerResult = flipCoin()

    println("Flipping the coin...")
    Thread.sleep(2000) // Delay for 2 seconds to add some suspense

    println("The coin shows: $computerResult")

    if (playerGuess == "H" && computerResult == "Heads" || playerGuess == "T" && computerResult == "Tails") {
        println("Congratulations! You guessed correctly!")
    } else {
        println("Sorry, you guessed incorrectly.")
    }
}
