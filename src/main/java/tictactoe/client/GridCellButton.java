package tictactoe.client;

import tictactoe.common.Game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GridCellButton extends JButton {
    Game.Symbol s;
    public GridCellButton(Game.Symbol s) {
        this.s = s;
        if (s != Game.Symbol.NONE)
            try {
                Image img = ImageIO.read(getClass().getResource("/icons/" + s.toString() + ".png"));
                Image newimg = img.getScaledInstance(50, 50,  java.awt.Image.SCALE_SMOOTH);
                setIcon(new ImageIcon(newimg));
            } catch (IOException e) {
             e.printStackTrace();
            }
    }
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(100, 100);
    }


    @Override
    public String getText() {
        return "";
    }
}
