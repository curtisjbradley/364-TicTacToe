import socket
import json
import time

HOST = "127.0.0.1"
PORT = 4000

def send_request(request_obj):
    try:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.connect((HOST, PORT))
            s.sendall((json.dumps(request_obj) + "\n").encode())
            response = b""
            while True:
                chunk = s.recv(4096)
                if not chunk:
                    break
                response += chunk
        return json.loads(response.decode())
    except Exception as e:
        print("Connection error:", e)
        return {"status": "error", "error": str(e)}

def pretty_board(board):
    b = [board[str(i)] for i in range(9)]
    symbols = {"X": "X", "O": "O", "NONE": " "}
    lines = [
        f" {symbols[b[0]]} | {symbols[b[1]]} | {symbols[b[2]]} ",
        "---+---+---",
        f" {symbols[b[3]]} | {symbols[b[4]]} | {symbols[b[5]]} ",
        "---+---+---",
        f" {symbols[b[6]]} | {symbols[b[7]]} | {symbols[b[8]]} "
    ]
    return "\n".join(lines)

def check_winner(board, symbol):
    wins = [
        [0,1,2], [3,4,5], [6,7,8],  # rows
        [0,3,6], [1,4,7], [2,5,8],  # cols
        [0,4,8], [2,4,6]            # diagonals
    ]
    for line in wins:
        if all(board[str(i)] == symbol for i in line):
            return True
    return False

def is_draw(board):
    return all(board[str(i)] != "NONE" for i in range(9))

def find_my_game(games, username, opponent):
    for game in games:
        if (game["player1"] == username and game["player2"] == opponent) or \
           (game["player2"] == username and game["player1"] == opponent):
            return game
    return None

def main():
    username = input("Enter your username: ").strip()
    opponent = input("Enter opponent username: ").strip()

    # Step 1: Create a new game
    newgame_resp = send_request({
        "request-type": "newgame",
        "username": username,
        "opponent": opponent
    })
    if newgame_resp.get("status") != "ok":
        print("Failed to create game:", newgame_resp.get("error"))
        return
    print("Game created. Waiting for gameplay...")
    gameid = newgame_resp.get("id")

    while True:
        # Step 2: Get list of games
        games_resp = send_request({
            "request-type": "getgames",
            "username": username
        })
        if games_resp.get("status") != "ok":
            print("Failed to fetch games:", games_resp.get("error"))
            time.sleep(1)
            continue

        games = games_resp.get("games", [])
        my_game = find_my_game(games, username, opponent)
        if not my_game:
            print("Game not found yet. Waiting...")
            time.sleep(1)
            continue

        print("\nCurrent Board:")
        print(pretty_board(my_game["board"]))

        is_player1 = my_game["player1"] == username
        my_symbol = "X" if is_player1 else "O"
        opponent_symbol = "O" if is_player1 else "X"
        is_my_turn = my_game["isPlayer1Turn"] == is_player1

        # Check for win/draw
        if check_winner(my_game["board"], my_symbol):
            print("✅ You won!")
            break
        elif check_winner(my_game["board"], opponent_symbol):
            print("❌ You lost.")
            break
        elif is_draw(my_game["board"]):
            print("🤝 It's a draw!")
            break

        if is_my_turn:
            pos = input("Your move (0-8): ").strip()
            if not pos.isdigit() or not 0 <= int(pos) <= 8:
                print("Invalid move.")
                continue

            move_req = {
                "request-type": "move",
                "username": username,
                "gameid": gameid,
                "position": int(pos)
            }
            move_resp = send_request(move_req)
            if move_resp.get("status") == "ok":
                print("Move sent successfully.")
            else:
                print("Move failed:", move_resp.get("error"))
        else:
            print("Waiting for opponent's move...")
            time.sleep(2)

if __name__ == "__main__":
    main()
