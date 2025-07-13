package tictactoe.server.requestwrappers;

import com.google.gson.JsonObject;
import tictactoe.common.Game;
import tictactoe.server.GameManager;

public class NewGameRequest extends NetworkRequest {
    private final String challengedPlayer;
    public NewGameRequest(String username, String challengedPlayer) {
        super(username);
        this.challengedPlayer =challengedPlayer;
    }

    public String getChallengedPlayer() {
        return challengedPlayer;
    }

    @Override
    public JsonObject execute() {
        Game g = GameManager.getInstance().createGame(getUsername(), getChallengedPlayer());
        JsonObject obj = new JsonObject();
        obj.addProperty("id",  g.getId().toString());
        obj.addProperty("status", "ok");
        return obj;
    }
}
