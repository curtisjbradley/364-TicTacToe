package tictactoe.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import tictactoe.common.Common;
import tictactoe.common.Game;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ClientConnection {
    private final String username;
    private Socket socket = null;
    private final HashMap<Integer, ReceivedResponseSAM> responseHandlers = new HashMap<>();
    private int requestIdCounter = 0;
    private static ClientConnection instance;

    public static ClientConnection getInstance() {return instance;}
    public static void init(String username) throws IOException{
        instance = new  ClientConnection(username);
    }
    private ClientConnection(String username) throws IOException {
        this.username = username;
        reconnect();
    }
    public String getUsername() {
        return username;
    }

    private void listen() {
        while (!socket.isClosed() && socket.isConnected()) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                JsonObject jo =  new Gson().fromJson(in.readLine(), JsonObject.class);
                new Thread(() -> processResponse(jo)).start();
            }  catch (SocketException e) {
                System.out.println("Socket closed");
                return;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void processResponse(JsonObject message) {
        System.out.println("Received: " + message.toString());
        if(message.has("error")){
            throw new RuntimeException(message.get("error").getAsString());
        }

        if (message.has("request-id")){
            int requestId = message.get("request-id").getAsInt();
            ReceivedResponseSAM responseSAM = responseHandlers.getOrDefault(requestId, null);
            if (responseSAM != null) {
                try {
                    responseSAM.receivedResponse(message);
                    responseHandlers.remove(requestId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (message.has("status") && message.get("status").getAsString().equals("update")){
            if (message.has("game")){
                Game g = Game.deserialize(message.get("game").getAsJsonObject());
                GameManager.getInstance().updateGame(g);
            }
        }
        //Else drop the packet

    }

    private void reconnect() throws IOException {
        if(this.socket != null && !this.socket.isClosed()) {
            this.socket.close();
        }
        this.socket =  new Socket("localhost", Common.PORT);
        new Thread(this::listen).start();
        sendConnectionRequest();
    }

    private void sendConnectionRequest() throws IOException {
        JsonObject connectRequest = new JsonObject();
        connectRequest.addProperty("username", username);
        connectRequest.addProperty("request-type", "connect");
        send(connectRequest);
    }
    private void sendMessage(JsonObject request, ReceivedResponseSAM responseHandler) throws IOException {
        responseHandlers.put(requestIdCounter, responseHandler);
        try {
            send(request);
        } catch(SocketException e) {
            reconnect();
            send(request);
        }
    }
    private void send(JsonObject message) throws IOException {
        message.addProperty("request-id", requestIdCounter++);

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        out.write(message.toString());
        out.newLine();
        out.flush();
    }

    public void sendNewGameRequest(String opponent, ReceivedResponseSAM responseHandler) {
        JsonObject message = new JsonObject();
        message.addProperty("request-type", "newgame");
        message.addProperty("username", username);
        message.addProperty("opponent", opponent);
        try {
             sendMessage(message, responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getGame(UUID uuid, ReceivedResponseSAM responseHandler) {
        JsonObject gameInfoRequest = new JsonObject();
        gameInfoRequest.addProperty("request-type", "game");
        gameInfoRequest.addProperty("username", username);
        gameInfoRequest.addProperty("id", uuid.toString());
        try {
            sendMessage(gameInfoRequest, responseHandler);
        } catch (Exception e){
            e.printStackTrace();
        }

    }
    public void playMove(Game g, int location, ReceivedResponseSAM responseHandler) {
        JsonObject gameInfoRequest = new JsonObject();
        gameInfoRequest.addProperty("request-type", "move");
        gameInfoRequest.addProperty("username", username);
        gameInfoRequest.addProperty("gameid", g.getId().toString());
        gameInfoRequest.addProperty("position", location);
        try {
            sendMessage(gameInfoRequest, responseHandler);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void getGames(ReceivedResponseSAM responseHandler) {
        System.out.println("requesting games");
        JsonObject gameInfoRequest = new JsonObject();
        gameInfoRequest.addProperty("request-type", "getgames");
        gameInfoRequest.addProperty("username", username);
        try {
            sendMessage(gameInfoRequest, responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
