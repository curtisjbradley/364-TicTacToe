package tictactoe.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ConnectionManager {
    private final HashMap<String, Connection> connections = new HashMap<>();
    private final Object lock = new Object();

    private static ConnectionManager instance = null;
    private ConnectionManager(){}
    public static ConnectionManager getInstance() {
        if (instance == null) instance = new ConnectionManager();
        return instance;
    }


    public void registerConnection(String username, Connection connection) {
            if(connections.containsKey(username)){
                endConnection(username);
            }
            synchronized (lock) {
                connections.put(username, connection);
            }
    }

    public synchronized void endConnection(Connection connection) {
        if(connection == null) return;
        if(connections.containsValue(connection)) {
            synchronized (lock) {
                ArrayList<String> toEnd = new ArrayList<>();
                connections.forEach((user,con) -> {
                    if(con.equals(connection)) toEnd.add(user);
                });
                toEnd.forEach(connections::remove);
            }
        }
        try {
            if(!connection.isOpen()) {
                connection.closeConnection();
            }
        } catch (IOException e){
            e.printStackTrace();
        }

    }
    public void endConnection(String username) {
        endConnection(connections.getOrDefault(username,null));
    }
    public synchronized Connection getConnection(String username){
        return connections.get(username);
    }
}
