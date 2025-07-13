package tictactoe.server.requestwrappers;

import com.google.gson.JsonObject;

public abstract class NetworkRequest {
    private final String username;
    public NetworkRequest(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }

    public abstract JsonObject execute();
}
