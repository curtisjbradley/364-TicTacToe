package tictactoe.server;

import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.UUID;

public class Game {
    public enum Symbol {
        X,
        O,
        NONE
    }

    private final UUID id = UUID.randomUUID();
    private final String player1;
    private final String player2;
    private boolean isPlayer1Turn;
    private final Symbol[] board = new Symbol[9];

    public Game (String player1, String player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.isPlayer1Turn = true;
        Arrays.fill(board, Symbol.NONE);
    }

    public String getPlayer1() {
        return player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public boolean isPlayer1Turn() {
        return isPlayer1Turn;
    }

    public Symbol[] getBoard() {
        return board;
    }

    public void setPlayer1Turn(boolean player1Turn) {
        isPlayer1Turn = player1Turn;
    }

    public UUID getId() {
        return id;
    }

    public JsonObject serialize() {
        JsonObject gameJson = new JsonObject();
        gameJson.addProperty("player1", getPlayer1());
        gameJson.addProperty("player2", getPlayer2());
        gameJson.addProperty("isPlayer1Turn", isPlayer1Turn());
        JsonObject boardJson = new JsonObject();
        for (int i = 0; i < 9; i++) {
            boardJson.addProperty(i + "", getBoard()[i].toString());
        }

        gameJson.add("board", boardJson);
        return gameJson;
    }
}
