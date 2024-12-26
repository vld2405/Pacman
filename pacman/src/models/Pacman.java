package models;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import javax.swing.*;

public class Pacman extends JPanel implements ActionListener, KeyListener {

    private final int rowCount = 21;
    private final int columnCount = 19;
    public final int tileSize = 32;
    private final int boardWidth = columnCount * tileSize;
    private final int boardHeight = rowCount * tileSize;

    private int score = 0;
    private int remainingLives = 3;
    private boolean gameOver = false;

    ArrayList<Integer> noWallsRows;
    ArrayList<Integer> noWallsColumns;

    private final Image wallImage;
    private final Image blueGhostImage;
    private final Image orangeGhostImage;
    private final Image pinkGhostImage;
    private final Image redGhostImage;

    private final Image pacmanUpImage;
    private final Image pacmanDownImage;
    private final Image pacmanLeftImage;
    private final Image pacmanRightImage;

    private HashSet<Block> walls;
    private HashSet<Block> foods;
    private HashSet<Block> ghosts;
    private Block pacman;

    private Timer gameLoop;
    private char[] directions = {'U', 'D', 'L', 'R'};
    private Random random = new Random();

    /// X = wall, O = skip, P = pacman, ' ' = food
    /// Ghosts: b = blue, o = orange, p = pink, r = red

    private String[] tileMap = {
            "XXXXXXXXXXXXXX XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "        bpo        ",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXX XX X",
            "X                 X",
            "XXXXXXXXXXXXXX XXXX"
    };

    public Pacman() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        //load images
        wallImage = new ImageIcon(getClass().getResource("../images/wall.png")).getImage();

        blueGhostImage = new ImageIcon(getClass().getResource("../images/blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("../images/orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("../images/pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("../images/redGhost.png")).getImage();

        pacmanUpImage = new ImageIcon(getClass().getResource("../images/pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("../images/pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("../images/pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("../images/pacmanRight.png")).getImage();

        noWallsRows = new ArrayList<Integer>();
        noWallsColumns = new ArrayList<Integer>();

        loadMap();
        findNoWallsRows();
        findNoWallsColumns();

        System.out.print("no walls rows: ");
        for(int index: noWallsRows){
            System.out.print(index + " ");
        }
        System.out.print("\nno walls column: ");
        for(int index: noWallsColumns){
            System.out.print(index + " ");
        }

        for(Block ghost: ghosts)
        {
            char newDirection = directions[random.nextInt(4)];
            System.out.println("Ghost dir: " + newDirection);
            ghost.updateDirection(newDirection, walls, tileSize);
        }

        // how long it takes to start timer, milliseconds gone between frames
        gameLoop = new Timer(50, this); // 20fps (1000/50)
        gameLoop.start();
    }

    private void findNoWallsRows() {
        for(int i=0; i < rowCount; ++i) {
            boolean found = true;

            for(int j=0; j < columnCount; ++j) {
                if(tileMap[i].charAt(j) == 'X') {
                    found = false;
                    break;
                }
            }
            if(found)
                noWallsRows.add(i);
        }
    }

    private void findNoWallsColumns() {
        for(int i=0; i < columnCount; ++i) {
            boolean found = true;

            for(int j=0; j < rowCount; ++j) {
                if(tileMap[j].charAt(i) == 'X') {
                    found = false;
                    break;
                }
            }
            if(found)
                noWallsColumns.add(i);
        }
    }

    private void loadMap() {
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        for(int row = 0; row < rowCount; ++row) {
            for(int col = 0; col < columnCount; ++col) {
                String rowString = tileMap[row];
                char tileMapChar = rowString.charAt(col);

                int x = col * tileSize;
                int y = row * tileSize;

                switch(tileMapChar) {
                    case 'X':
                        walls.add(new Block(wallImage, x, y, tileSize, tileSize));
                        break;
                    case 'b':
                        ghosts.add(new Block(blueGhostImage, x, y, tileSize, tileSize));
                        break;
                    case 'o':
                        ghosts.add(new Block(orangeGhostImage, x, y, tileSize, tileSize));
                        break;
                    case 'p':
                        ghosts.add(new Block(pinkGhostImage, x, y, tileSize, tileSize));
                        break;
                    case 'r':
                        ghosts.add(new Block(redGhostImage, x, y, tileSize, tileSize));
                        break;
                    case 'P':
                        pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                        break;
                    case ' ':
                        foods.add(new Block(null, x + 14, y + 14, 4, 4));
                        break;
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for(Block ghost: ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for(Block wall: walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setColor(Color.WHITE);
        for(Block food: foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        g.setFont(new Font("Arial", Font.BOLD, 20));

        if(gameOver) {
            g.drawString("Game Over: " + String.valueOf(score), tileSize / 2, tileSize / 2);
        }
        else {
            g.drawString("x" + String.valueOf(remainingLives) + " Score: " + String.valueOf(score), tileSize / 2, tileSize / 2);
        }
    }

    private void changePosIfOutOfFrame(Block block) {
        if(block.x > boardWidth) {
            block.x = -block.width;
        }
        if(block.y > boardHeight) {
            block.y = -block.width;
        }
        if(block.x + block.width < 0) {
            block.x = boardWidth;
        }
        if(block.y + block.width < 0) {
            block.y = boardHeight;
        }
    }

    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        foodCollision();

        changePosIfOutOfFrame(pacman);

        //check wall collisions
        for (Block wall : walls) {
            if (Block.collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        //check ghost collisions
        for (Block ghost : ghosts) {
            if (Block.collision(ghost, pacman)) {
                remainingLives -= 1;
                if (remainingLives == 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
            }

            if (noWallsColumns.contains(ghost.x / tileSize) && ghost.direction != 'L' && ghost.direction != 'R') {
                char newDirection = directions[random.nextInt(2) + 2];
                ghost.updateDirection(newDirection, walls, tileSize);
            }
            else if (noWallsRows.contains(ghost.y / tileSize) && ghost.direction != 'U' && ghost.direction != 'D') {
                char newDirection = directions[random.nextInt(2)];
                ghost.updateDirection(newDirection, walls, tileSize);
            }

            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            changePosIfOutOfFrame(ghost);

            for (Block wall : walls) {
                if (Block.collision(ghost, wall)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection, walls, tileSize);
                }
            }
        }
    }

    private void foodCollision() {
        Iterator<Block> iterator = foods.iterator();
        while (iterator.hasNext()) {
            Block food = iterator.next();
            if (Block.collision(pacman, food)) {
                score += 10;
                iterator.remove(); // Safe removal during iteration
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        //keyReleased(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(gameOver) {
            if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                gameOver = false;
                loadMap();
                resetPositions();
                remainingLives = 3;
                score = 0;
                gameLoop.start();
            }
        }

        if(e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U', walls, tileSize);
        }

        if(e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D', walls, tileSize);
        }

        if(e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L', walls, tileSize);
        }

        if(e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R', walls, tileSize);
        }

        if(pacman.direction == 'U') {
            pacman.image = pacmanUpImage;
        }
        else if(pacman.direction == 'D') {
            pacman.image = pacmanDownImage;
        }
        else if(pacman.direction == 'L') {
            pacman.image = pacmanLeftImage;
        }
        else if(pacman.direction == 'R') {
            pacman.image = pacmanRightImage;
        }
    }

    private void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;

        for(Block ghost: ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection, walls, tileSize);
        }
    }

    public int getRowCount() {
        return rowCount;
    }
    public int getColumnCount() {
        return columnCount;
    }
    public HashSet<Block> getWalls() {
        return walls;
    }
    public HashSet<Block> getGhosts() {
        return ghosts;
    }
    public HashSet<Block> getFoods() {
        return foods;
    }
    public String[] getTileMap() {
        return tileMap;
    }
    public void setTileMap(String[] tileMap) {
        this.tileMap = tileMap;
    }
}
