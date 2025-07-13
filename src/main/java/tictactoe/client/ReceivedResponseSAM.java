package tictactoe.client;

import com.google.gson.JsonObject;

import java.io.IOException;

public interface ReceivedResponseSAM {
    void receivedResponse(JsonObject response) throws IOException;
}
