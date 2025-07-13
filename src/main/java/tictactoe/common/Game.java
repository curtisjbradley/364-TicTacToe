package tictactoe.common;

import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.UUID;

public class Game {
    public enum Symbol {
        X,
        O,
        NONE
    }

    private final UUID id;
    private final String player1;
    private final String player2;
    private boolean isPlayer1Turn;
    private final Symbol[] board = new Symbol[9];

    public Game (String player1, String player2) {
        this(player1, player2, UUID.randomUUID());
    }

    public Game (String player1, String player2, UUID id) {
        this.player1 = player1;
        this.player2 = player2;
        this.isPlayer1Turn = true;
        this.id = id;
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
    public boolean hasEnded() {
        return false;
    }

    public UUID getId() {
        return id;
    }

    public JsonObject serialize() {
        JsonObject gameJson = new JsonObject();
        gameJson.addProperty("player1", getPlayer1());
        gameJson.addProperty("player2", getPlayer2());
        gameJson.addProperty("isPlayer1Turn", isPlayer1Turn());
        gameJson.addProperty("id", id.toString());
        JsonObject boardJson = new JsonObject();
        for (int i = 0; i < 9; i++) {
            boardJson.addProperty(i + "", getBoard()[i].toString());
        }

        gameJson.add("board", boardJson);
        return gameJson;
    }
    public static Game deserialize(JsonObject jo) {
        try {
            Game parsedGame = new Game(jo.get("player1").getAsString(), jo.get("player2").getAsString(),
                    UUID.fromString(jo.get("id").getAsString()));
            parsedGame.setPlayer1Turn(jo.get("isPlayer1Turn").getAsBoolean());
            for (int i = 0; i < 9; i++) {
                JsonObject boardJson = jo.getAsJsonObject("board");
                String symbol = boardJson.get(i + "").getAsString();
                parsedGame.getBoard()[i] = Symbol.valueOf(symbol);
            }

            return parsedGame;
        } catch (Exception e){
            throw new IllegalArgumentException("Bad serialised game.",e);
        }
    }
}
