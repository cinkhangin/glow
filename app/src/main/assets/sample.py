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
