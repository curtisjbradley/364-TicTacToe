package tictactoe.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import tictactoe.common.Game;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

public class MainView extends JPanel {
    JFrame frame;
    private final String username;
    public MainView(JFrame frame, String username) throws IOException {
        this.username = username;
        ClientConnection.init(username);
        this.frame = frame;
        addMenuOptions();
        showNewGame();
        frame.pack();
    }

    public void addMenuOptions(){
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        JMenuItem newGameButton = new JMenuItem("New Game");
        menuBar.add(newGameButton);
        newGameButton.addActionListener(e -> showNewGame());
        JMenuItem showGamesButton = new JMenuItem("Open Game");
        showGamesButton.addActionListener(e -> showGames());
        menuBar.add(showGamesButton);
        JMenuItem scoreboardButton = new JMenuItem("Scoreboard");
        scoreboardButton.addActionListener(e -> showScoreboard());
        menuBar.add(scoreboardButton);

    }

    public void showNewGame(){
        frame.getContentPane().removeAll();
        frame.add(new JLabel("New Game"));
        JPanel panel = new JPanel();
        frame.add(panel);
        frame.setLayout(new BoxLayout(frame.getContentPane(),BoxLayout.Y_AXIS));

        panel.add(new JLabel("Opponent:"));
        JTextField oppField = new JTextField(20);
        panel.add(oppField);
        JButton playButton = new JButton("Play");
        panel.add(playButton);

        playButton.addActionListener(e -> {
                    if (oppField.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Please enter your opponent");
                        return;
                    }
                    if(oppField.getText().equals(username)){
                        JOptionPane.showMessageDialog(frame, "You cannot play yourself");
                        return;
                    }
                    playButton.setEnabled(false);
                    ClientConnection.getInstance().sendNewGameRequest(oppField.getText(), (output) -> {
                        if (output.get("status").getAsString().equals("ok")) {
                            if (!output.has("id")) {
                                JOptionPane.showMessageDialog(frame, "Error creating new game");
                            } else {
                                UUID uuid = UUID.fromString(output.get("id").getAsString());
                                this.showGame(uuid);
                            }
                        } else {
                            System.out.println("Could not send newgame");
                        }
                        playButton.setEnabled(true);
                    });
                });

        frame.pack();
    }

    public void showGame(UUID gameUUID){
        frame.getContentPane().removeAll();
        frame.add(new GameView(gameUUID, (pos) -> {
            Game g =  GameManager.getInstance().getGame(gameUUID);
            if (g.hasEnded()) {
                JOptionPane.showMessageDialog(frame, "Game has already ended");
                return;
            }
            if ((g.isPlayer1Turn() && username.equals(g.getPlayer1())) ||
                    (!g.isPlayer1Turn() && username.equals(g.getPlayer2()))) {
                ClientConnection.getInstance().playMove(g,pos,(r) -> {
                    if (!r.has("error") && r.has("game")) {
                        GameManager.getInstance().updateGame(Game.deserialize(r.get("game").getAsJsonObject()));
                        showGame(gameUUID);
                    }
                });
            } else {
                JOptionPane.showMessageDialog(frame, "It is not your turn");
            }
        }));
        frame.pack();
    }

    public void showGames() {
        ArrayList<Game> games =  GameManager.getInstance().getGames();
        if(games == null){return;}

        frame.getContentPane().removeAll();
        frame.add(new JLabel("Games:"));
        for (Game game : games) {
            if (game == null) continue;
            if(game.hasEnded()) continue;
            JPanel gameInstance = new JPanel();
            gameInstance.add(new JLabel(game.getPlayer1() + " vs. " + game.getPlayer2()));
            JButton playButton = new JButton("Play");
            playButton.addActionListener(e -> showGame(game.getId()));
            gameInstance.add(playButton);
            frame.add(gameInstance);
        }
        frame.pack();

    }

    public void showScoreboard() {
        ClientConnection.getInstance().getScoreboard((response) -> {
            if(response.has("error")) {
                System.out.println("Error retrieving scoreboard");
                return;
            }
            HashMap<String,Integer> scores = new HashMap<>();
            for (JsonObject score : response.get("scores").getAsJsonArray().asList().stream().map(JsonElement::getAsJsonObject).collect(Collectors
                    .toList())) {
                scores.put(score.get("player").getAsString(), score.get("score").getAsInt());
            }
            JPanel scorePanel = new JPanel();
            scores.entrySet().stream().sorted((o1, o2) -> Integer.compare(o2.getValue(), o1.getValue())).forEach((entry) -> {
                System.out.println(entry.getKey() + " -" + entry.getValue());
                JLabel score = new JLabel( entry.getKey() + " - " + entry.getValue());
                score.setAlignmentX(JLabel.CENTER_ALIGNMENT);
                scorePanel.add(score);
            });
            scorePanel.setLayout(new BoxLayout(scorePanel,BoxLayout.Y_AXIS));
            frame.getContentPane().removeAll();
            frame.add(new JLabel("Scoreboard"));
            frame.add(new JScrollPane(scorePanel));
            frame.pack();
        });
    }
}
