package tictactoe.server.requestwrappers;

import com.google.gson.JsonObject;
import tictactoe.common.Game;
import tictactoe.server.GameManager;

import java.util.UUID;

public class GameInfoRequest extends NetworkRequest {
    UUID gameId;
    public GameInfoRequest(String username, UUID gameId) {
        super(username);
        this.gameId = gameId;
    }
    @Override
    public JsonObject execute() {
        JsonObject jsonObject = new JsonObject();
        Game foundGame =  GameManager.getInstance().getGames().stream()
                .filter(game -> game.getId().equals(gameId)).findFirst().orElse(null);
        if(foundGame == null) {
            jsonObject.addProperty("error", "Game not found");
            jsonObject.addProperty("status", "error");
            return jsonObject;
        }
        jsonObject.addProperty("status", "ok");
        jsonObject.add("game",foundGame.serialize());
        return jsonObject;
    }
}
