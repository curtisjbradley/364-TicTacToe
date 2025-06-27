package server;

import com.google.gson.JsonObject;
import server.requestwrappers.NetworkRequest;
import server.requestwrappers.RequestParser;

import java.io.*;
import java.net.Socket;

public class RequestHandler {
    private final Socket socket;
    public RequestHandler(Socket socket) {
        this.socket = socket;
    }
    public void handle(){
        String request;
        try {
            BufferedReader br = new BufferedReader( new InputStreamReader(socket.getInputStream()));
            request = br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (request == null) {
            System.out.println("Received a null request");
            return;
        }
        System.out.println("Received request: " + request);
        String output = "";

        try {
        NetworkRequest wrappedRequest = RequestParser.parseRequest(request);


            output = wrappedRequest.execute();
        } catch (Exception e) {
            e.printStackTrace();
            JsonObject jo = new JsonObject();
            jo.addProperty("error", e.getMessage());
            jo.addProperty("status", "error");
            output = jo.toString();
        }

        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write(output);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            System.out.println("Error writing to output stream");
        }

    }
}
