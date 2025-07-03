package tictactoe.server.requestwrappers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class RequestParser {
    private RequestParser() {}


    public static NetworkRequest parseRequest(String request) {
        JsonObject json;
        try {
            json = new Gson().fromJson(request, JsonObject.class);
        } catch (JsonSyntaxException e) {
            throw new JsonSyntaxException("Request is not valid json");
        }
        if(!json.has("username")) {
            throw new IllegalArgumentException("Request does not contain a username field");
        }
        if(!json.has("request-type")) {
            throw new IllegalArgumentException("Request does not contain a request-type field");
        }

        String username = json.get("username").getAsString();
        switch(json.get("request-type").getAsString()) {
            case "scoreboard":
                return new ScoreboardRequest(username);
            case "getgames":
                return new GetGamesRequest(username);
            case "newgame":
                if(!json.has("opponent")) {
                    throw new IllegalArgumentException("Invalid request - new games must have a player to play against");
                }
                return new NewGameRequest(username, json.get("opponent").getAsString());
            case "move":
                if(!json.has("gameid")) {
                    throw new IllegalArgumentException("Invalid request - moves must have a gameid");
                }
                if(!json.has("position")) {
                    throw new IllegalArgumentException("Invalid request - moves must have a position");
                }
                return new GameMoveRequest(username,json.get("gameid").getAsString(),json.get("position").getAsInt());
            default:
                throw new IllegalArgumentException("Invalid request - unknown request type");
        }
    }
}
