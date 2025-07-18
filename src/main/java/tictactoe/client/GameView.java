package tictactoe.client;

import tictactoe.common.Game;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

public class GameView extends JPanel {

    public interface MoveHandlerSAM {
        void playMove(int location);
    }

    public static GameView instance;
    private final MoveHandlerSAM handler;
    private final UUID gameUUID;
    public GameView(UUID game, MoveHandlerSAM moveHandler) {
        this.instance = this;
        this.handler = moveHandler;
        this.gameUUID = game;
        update();
    }
    public UUID getGameUUID() {
        return gameUUID;
    }

    public void update() {
        this.removeAll();
        this.setLayout(new GridLayout(3,3));
        Game game = GameManager.getInstance().getGame(gameUUID);
        for (int i = 0; i < 9; i++) {
            Game.Symbol s  = game.getBoard()[i];
            JButton button = new GridCellButton(s);

            final int finalLoc = i;
            button.addActionListener(e -> handler.playMove(finalLoc));
            String playerToPlay = game.isPlayer1Turn() ? game.getPlayer1() : game.getPlayer2();
            button.setEnabled(!game.hasEnded() && ClientConnection.getInstance().getUsername().equals(playerToPlay)
                    && s ==  Game.Symbol.NONE);
            this.add(button);
        }
        Main.getFrame().pack();
    }
}
