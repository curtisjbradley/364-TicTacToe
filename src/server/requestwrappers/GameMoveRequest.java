package server.requestwrappers;

import com.google.gson.JsonObject;
import server.Game;
import server.GameManager;

import java.util.Objects;
import java.util.UUID;

public class GameMoveRequest extends NetworkRequest {
    private final UUID gameId;
    private final int position;

    public GameMoveRequest(String username, String gameID, int position) {
        super(username);
        try{
            this.gameId = UUID.fromString(gameID);
        } catch(IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid game ID");
        }
        this.position = position;
    }
    public UUID getGameID() {
        return gameId;
    }
    public int getPosition() {
        return position;
    }

    @Override
    public String execute() {
        Game g = GameManager.getInstance().getGame(getGameID());
        if(g == null){
            throw new IllegalArgumentException("Game does not exist");
        }
        String currentPlayer = g.isPlayer1Turn() ? g.getPlayer1() : g.getPlayer2();

        if(!Objects.equals(currentPlayer, getUsername())){
            throw new IllegalArgumentException("It is not your turn to move");
        }
        if (getPosition() < 0 || getPosition() > 8) {
            throw new IllegalArgumentException("Position must be between 0 and 8");
        }
        if(g.getBoard()[getPosition()] != Game.Symbol.NONE) {
            throw new IllegalArgumentException("Space is already occupied");
        }

        Game.Symbol symbol = g.isPlayer1Turn() ? Game.Symbol.X : Game.Symbol.O;
        g.getBoard()[getPosition()] = symbol;
        g.setPlayer1Turn(!g.isPlayer1Turn());
        JsonObject jo = new JsonObject();
        jo.addProperty("status", "ok");
        return jo.toString();
    }
}
