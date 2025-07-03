package tictactoe.server.requestwrappers;

import tictactoe.server.GameManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class GetGamesRequest extends NetworkRequest {
    public GetGamesRequest(String username) {
        super(username);
    }

    @Override
    public String execute() {
        JsonObject jsonObject = new JsonObject();
        JsonArray games = new JsonArray();
        GameManager.getInstance().getGames().stream().filter(game -> game.getPlayer1().equals(getUsername()) || game.getPlayer2().equals(getUsername())).forEach(game -> games.add(game.serialize()));

        jsonObject.add("games", games);
        jsonObject.addProperty("status", "ok");
        return jsonObject.toString();
    }
}
