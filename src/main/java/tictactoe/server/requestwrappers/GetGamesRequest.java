package tictactoe.server.requestwrappers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import tictactoe.server.GameManager;

public class GetGamesRequest extends NetworkRequest {
    public GetGamesRequest(String username) {
        super(username);
    }

    @Override
    public JsonObject execute() {
        JsonObject jsonObject = new JsonObject();
        JsonArray games = new JsonArray();
        GameManager.getInstance().getGames().stream().filter(game -> game.getPlayer1().equals(getUsername()) || game.getPlayer2().equals(getUsername())).forEach(game -> games.add(game.serialize()));

        jsonObject.add("games", games);
        jsonObject.addProperty("status", "ok");
        return jsonObject;
    }
}
