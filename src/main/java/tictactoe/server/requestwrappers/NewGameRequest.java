package tictactoe.server.requestwrappers;

import com.google.gson.JsonObject;
import tictactoe.common.Game;
import tictactoe.server.ConnectionManager;
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

        JsonObject update = new JsonObject();
        update.addProperty("status", "update");
        update.add("game", g.serialize());

        try {
            ConnectionManager.getInstance().getConnection(challengedPlayer).sendMessage(update);
        } catch(Exception e) {
            System.out.println("Could not send update to other player");
        }

        return obj;
    }
}
