package tictactoe.server.requestwrappers;

public abstract class NetworkRequest {
    private String username;
    public NetworkRequest(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }

    public abstract String execute();
}
