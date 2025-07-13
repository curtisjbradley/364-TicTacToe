package tictactoe.client;

import com.google.gson.JsonElement;
import tictactoe.common.Game;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GameManager {
    private static final GameManager instance = new GameManager();
    private final HashMap<UUID, Game> games = new HashMap<>();
    public static GameManager getInstance() {
        return instance;
    }
    public ArrayList<Game> getGames() {
        if(games.isEmpty()) cacheGames();
        return new ArrayList<>(games.values());
    }
    public Game getGame(UUID id) {
        if(games.containsKey(id)) return games.get(id);
        cacheGames();
        return games.get(id);
    }
    public void updateGame(Game game) {
        games.put(game.getId(), game);
        if (game.hasEnded()) {
            if(game.getWinner() != null) {
                String loser = game.getWinner().equals(game.getPlayer1()) ? game.getPlayer2() : game.getPlayer1();
                JOptionPane.showMessageDialog(Main.getFrame(), "Game has ended. " + game.getWinner() + " has beaten " + loser);
            } else {
                JOptionPane.showMessageDialog(Main.getFrame(), "Game has ended. " + game.getPlayer1() + " vs. " + game.getPlayer2() +" is a draw.");
            }
            games.remove(game.getId());
        } else if(GameView.instance == null ||  !GameView.instance.isVisible() || !GameView.instance.getGameUUID().equals(game.getId())) {
            JOptionPane.showMessageDialog(Main.getFrame(),"Game update in " + game.getPlayer1() + " vs. " + game.getPlayer2(), "Game Update", JOptionPane.INFORMATION_MESSAGE);
        }
        if(GameView.instance == null) {
            return;
        }
        if (GameView.instance.getGameUUID().equals(game.getId())){
            GameView.instance.update();
        }


    }
    public void setGames(List<Game> games) {
        System.out.println("setting games");
        this.games.clear();
        games.forEach(game -> this.games.put(game.getId(), game));
    }
    public void cacheGames(){
        try {
            Object waitLock = new Object();
            synchronized (waitLock) {
                ClientConnection.getInstance().getGames((response) -> {
                    if (response.has("error")) {
                        throw new RuntimeException();
                    }

                    synchronized (waitLock) {
                        setGames(response.get("games").getAsJsonArray().asList().stream().map(JsonElement::getAsJsonObject)
                                .map(Game::deserialize).collect(Collectors.toList()));
                        waitLock.notifyAll();
                    }
                });
                waitLock.wait();
            }
        } catch (InterruptedException e) {
            System.out.println("Could not retrieve games");
        }
    }
}
