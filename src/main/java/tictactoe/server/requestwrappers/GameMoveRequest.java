package tictactoe.server.requestwrappers;

import com.google.gson.JsonObject;
import tictactoe.common.Game;
import tictactoe.server.ConnectionManager;
import tictactoe.server.GameManager;
import tictactoe.server.ScoreboardManager;

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
    public JsonObject execute() {
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

        String opponent = getUsername().equals(g.getPlayer1()) ? g.getPlayer2() : g.getPlayer1();
        JsonObject update = new JsonObject();
        update.addProperty("status", "update");
        update.add("game", g.serialize());

        try {
            ConnectionManager.getInstance().getConnection(opponent).sendMessage(update);
        } catch(Exception e) {
            System.out.println("Could not send update to other player");
        }
        if (g.hasEnded()) {
            if(g.getWinner() != null) {
                ScoreboardManager.getInstance().addScore(g.getWinner(),2);
            } else {
                ScoreboardManager.getInstance().addScore(g.getPlayer1(),1);
                ScoreboardManager.getInstance().addScore(g.getPlayer2(),1);
            }
        }
        JsonObject jo = new JsonObject();
        jo.addProperty("status", "ok");
        jo.add("game", g.serialize());
        return jo;
    }
}
