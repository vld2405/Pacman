package org.example;

import org.example.models.Game;

import javax.swing.JFrame;
import java.io.IOException;

// TODO: sa adaug powerup ca sa poti sa mananci fantome si EVENTUAL sa adaug si respawn pentru fantome (cu shortest path ca sa se intoarca la baza)
// TODO: sa adaug mai multe harti
// TODO: sa adaug animatii pentru fantome si pentru pacman
// TODO: ghost movement diferit (isi aleg un punct random pe harta si merg pana la el dupa aleg altul random si tot asa (BFS sau A* ca sa ajunga))

public class Main {
    public static void main(String[] args) throws IOException {
        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32;
        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize;

        JFrame frame = new JFrame("Pacman Game");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Game pacmanGame = new Game();
        frame.add(pacmanGame);
        frame.pack();
        pacmanGame.requestFocus();
        frame.setVisible(true);
    }
}