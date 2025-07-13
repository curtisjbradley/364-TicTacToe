package tictactoe.client;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main {
    private static JFrame frame;
    public static JFrame getFrame() {
        return frame;
    }
    public static void main(String[] args) {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("TicTacToe Client");
        Container pane = frame.getContentPane();

        pane.setLayout(new FlowLayout());

        pane.add(new JLabel("Enter your name:"));
        JTextField nameInput = new JTextField(20);
        pane.add(nameInput);
        JButton connectButton = new JButton("Connect");
        pane.add(connectButton);
        frame.pack();
        frame.setVisible(true);
        connectButton.addActionListener((actionEvent) -> {
            if (nameInput.getText().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter your name");
            }
            else {
                try {
                    new MainView(frame, nameInput.getText());
                    frame.getContentPane().removeAll();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(frame, "Connection Failed. Try again later.");
                }
            }
        });
    }
}
