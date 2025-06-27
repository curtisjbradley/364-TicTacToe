package server.requestwrappers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import server.GameManager;

public class GetGamesRequest extends NetworkRequest {
    public GetGamesRequest(String username) {
        super(username);
    }

    @Override
    public String execute() {
        JsonObject jsonObject = new JsonObject();
        JsonArray games = new JsonArray();
        GameManager.getInstance().getGames().stream().filter(game -> game.getPlayer1().equals(getUsername()) || game.getPlayer2().equals(getUsername())).forEach(game -> {
            JsonObject gameJson = new JsonObject();
            gameJson.addProperty("player1", game.getPlayer1());
            gameJson.addProperty("player2", game.getPlayer2());
            gameJson.addProperty("isPlayer1Turn", game.isPlayer1Turn());
            JsonObject boardJson = new JsonObject();
            for (int i = 0; i < 9; i++) {
                boardJson.addProperty(i + "", game.getBoard()[i].toString());
            }

            gameJson.add("board", boardJson);

            games.add(gameJson);
        });

        jsonObject.add("games", games);
        jsonObject.addProperty("status", "ok");
        return jsonObject.toString();
    }
}
