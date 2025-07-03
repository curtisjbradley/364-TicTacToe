package tictactoe.client;

import tictactoe.common.Common;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", Common.PORT);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        JsonObject json = new JsonObject();
        json.addProperty("request-type", "getgames");
        json.addProperty("username", "curtis");
        out.write(json.toString());
        out.newLine();
        out.flush();
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line = in.readLine();
        System.out.println(line);
    }
}
