import models.Pacman;

import javax.swing.JFrame;

// TODO: fa sa poti sa schimbi directia fara sa spamezi ca cretinu
// TODO: sa poata manca si sa creasca scorul !!DONE
// TODO: sa fac collision-ul si pentru pacman si fantome si sa scada vietile !!DONE
// TODO: sa adaug powerup ca sa poti sa mananci fantome si EVENTUAL sa adaug si respawn pentru fantome (cu shortest path ca sa se intoarca la baza)


public class Main {
    public static void main(String[] args) {
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

        Pacman pacmanGame = new Pacman();
        frame.add(pacmanGame);
        frame.pack();
        pacmanGame.requestFocus();
        frame.setVisible(true);
    }
}