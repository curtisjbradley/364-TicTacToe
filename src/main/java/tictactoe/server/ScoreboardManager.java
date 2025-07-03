package tictactoe.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;

public class ScoreboardManager {
    private static ScoreboardManager instance = new ScoreboardManager();
    public static ScoreboardManager getInstance() {
        return instance;
    }
    private ScoreboardManager() {}

    private HashMap<String, Integer> scores = new HashMap<String, Integer>();

    public int getScore(String player) {
        if (scores.containsKey(player)) {
            return scores.get(player);
        }
        return 0;
    }
    public void addScore(String player, int score) {
        int currentScore = scores.getOrDefault(player,0);
        scores.put(player,currentScore + score);
    }
    public JsonElement serializeScores() {
        JsonArray array = new JsonArray();
        for (String key : scores.keySet()) {
            JsonObject object = new JsonObject();
            object.addProperty("player", key);
            object.addProperty("score", scores.get(key));
            array.add(object);
        }
        return array;
    }
}
