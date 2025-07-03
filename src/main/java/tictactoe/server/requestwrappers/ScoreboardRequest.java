package tictactoe.server.requestwrappers;

import com.google.gson.JsonObject;
import tictactoe.server.ScoreboardManager;

public class ScoreboardRequest extends NetworkRequest {
    public ScoreboardRequest(String username) {
        super(username);
    }

    @Override
    public String execute() {
        JsonObject jo =new JsonObject();
        jo.addProperty("status", "ok");
        jo.add("scores" , ScoreboardManager.getInstance().serializeScores());
        return jo.toString();
    }
}
