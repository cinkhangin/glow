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
        console.log(`The coin shows: ${result}`);
        console.log("Press Enter to flip again or type 'exit' to quit.");
      }, 1000);
    }
  });
}

playGame();
