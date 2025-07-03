package tictactoe.server;

import java.util.ArrayList;
import java.util.UUID;

public class GameManager {
    public static GameManager instance;
    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }
    private GameManager() {}

    private final ArrayList<Game> games = new ArrayList<>();


    public ArrayList<Game> getGames() {
        return games;
    }

    public Game createGame(String player1, String player2) {
        Game game = new Game(player1, player2);
        games.add(game);
        return game;
    }
    public Game getGame(UUID id) {
       return games.stream().filter(game -> game.getId().equals(id)).findFirst().orElse(null);
    }
}
