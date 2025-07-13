package tictactoe.server;

import tictactoe.common.Common;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {


    public static void main(String[] args) throws IOException {
        System.out.println("Starting server...");
        try (ServerSocket serverSocket = new ServerSocket(Common.PORT)) {
            while (true) {
                Socket connection = serverSocket.accept();
                handleConnection(connection);
            }
        }
    }

    private static void handleConnection(Socket connection) {
        new Thread(() -> {
            try {
                new Connection(connection).initializeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}

