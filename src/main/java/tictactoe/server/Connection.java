package tictactoe.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import tictactoe.server.requestwrappers.NetworkRequest;
import tictactoe.server.requestwrappers.RequestParser;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class Connection {
    private final Socket socket;
    public Connection(Socket socket) {
        this.socket = socket;
    }

    public boolean isOpen(){
        return !socket.isClosed();
    }

    public void sendMessage(JsonObject message) throws IOException {
        System.out.println("Sending: " + message.toString());
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bw.write(message.toString());
        bw.newLine();
        bw.flush();
    }

    public JsonObject readRequest() throws SocketException {
        String request;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            request = br.readLine();
        }
        catch (SocketException e) {
            throw e;
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        if (request == null) {
            System.out.println("Received a null request");
            return null;
        }
        return new Gson().fromJson(request, JsonObject.class);
    }
    public void listen() {
        while (isOpen()) {
            JsonObject message;
            try {
                 message = readRequest();
                System.out.println("received request: " + message.toString());
            } catch (SocketException e) {
                System.out.println("Socket closed");
                return;
            }
            JsonObject output = new  JsonObject();
            int requestId = -1;
            if(message.has("request-id")) {
                requestId = message.get("request-id").getAsInt();
            }
            try {
                NetworkRequest wrappedRequest = RequestParser.parseRequest(message);
                output = new Gson().fromJson(wrappedRequest.execute(), JsonObject.class);
            } catch (Exception e){
                e.printStackTrace();
                output.addProperty("error", e.getMessage());
                output.addProperty("status", "error");
            }
            output.addProperty("request-id", requestId);
            try {
                sendMessage(output);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error sending response");
            }
        }
    }

    public void closeConnection() throws IOException {
        System.out.println("Closing connection...");
        socket.close();
    }
    public void initializeConnection() throws IOException {
        JsonObject request = readRequest();
        if(!request.has("request-type") || !request.has("username")
                || !request.get("request-type").getAsString().equals("connect")){
            JsonObject response = new  JsonObject();
            response.addProperty("status", "error");
            response.addProperty("error", "Invalid connection request");
            sendMessage(response);
            closeConnection();
            throw new IOException("Invalid initialization request.");
        }
        String username = request.get("username").getAsString();
        ConnectionManager.getInstance().registerConnection(username,this);
        JsonObject response = new  JsonObject();
        response.addProperty("status", "ok");
        System.out.println("Sending ok response");
        sendMessage(response);
        new Thread(this::listen).start();
    }
}
