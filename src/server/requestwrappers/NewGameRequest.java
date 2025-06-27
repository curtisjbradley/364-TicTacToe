package server.requestwrappers;

import com.google.gson.JsonObject;
import server.Game;
import server.GameManager;

public class NewGameRequest extends NetworkRequest {
    private String challengedPlayer;
    public NewGameRequest(String username, String challengedPlayer) {
        super(username);
        this.challengedPlayer =challengedPlayer;
    }

    public String getChallengedPlayer() {
        return challengedPlayer;
    }

    @Override
    public String execute() {
        Game g = GameManager.getInstance().createGame(getUsername(), getChallengedPlayer());
        JsonObject obj = new JsonObject();
        obj.addProperty("id",  g.getId().toString());
        obj.addProperty("status", "ok");
        return obj.toString();
    }
}
