package tictactoe.server.requestwrappers;

import com.google.gson.JsonObject;

import java.util.UUID;

public class RequestParser {
    private RequestParser() {}


    public static NetworkRequest parseRequest(JsonObject json) {

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
            case "game":
                if (!json.has("id")) {
                    throw new IllegalArgumentException("Request does not contain a id field");
                }
                return new GameInfoRequest(username, UUID.fromString(json.get("id").getAsString()));
            case "newgame":
                if(!json.has("opponent")) {
                    throw new IllegalArgumentException("Invalid request - new games must have a player to play against");
                }
                if(json.get("opponent").getAsString().equals(username)) {
                    throw new IllegalArgumentException("Invalid request - cannot play against themselves");
                }
                return new NewGameRequest(username, json.get("opponent").getAsString());
            case "move":
                if(!json.has("gameid")) {
                    throw new IllegalArgumentException("Invalid request - moves must have a gameid");
                }
                if(!json.has("position")) {
                    throw new IllegalArgumentException("Invalid request - moves must have a position");
                }
                return new GameMoveRequest(username, json.get("gameid").getAsString(),json.get("position").getAsInt());
            default:
                throw new IllegalArgumentException("Invalid request - unknown request type");
        }
    }
}
