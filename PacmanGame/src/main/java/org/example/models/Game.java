package org.example.models;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class Game extends JPanel implements ActionListener, KeyListener {
    static int boardWidth;
    static int boardHeight;
    private int rowCount;
    private int columnCount;
    public int tileSize = 32;

    private int mapIndex;
    private int mapCount;

    private int score = 0;
    private int remainingLives = 3;
    private int foodsEaten = 0;

    private boolean gameOver = false;

    ArrayList<Integer> noWallsRows;
    ArrayList<Integer> noWallsColumns;

    private final Image wallImage;
    private final Image[] blueGhostImages;
    private final Image[] orangeGhostImages;
    private final Image[] pinkGhostImages;
    private final Image[] redGhostImages;
    static Image scaredGhostImage;

    private final Image powerFoodImage;
    private final Image cherryImage;

    private final Image[] pacmanImages; // 0 - UP, 1 - DOWN, 2- LEFT, 3 - RIGHT

    private HashSet<Block> walls;
    private HashSet<Block> foods;
    private HashSet<Block> ghosts;
    private Pacman pacman;

    private Timer gameLoop;
    private char[] directions = {'U', 'D', 'L', 'R'};
    private Random random = new Random();

    private Timer powerFoodTimer;
    private Timer cherryTimer;

    boolean isCherryTimerStarted;
    boolean powerFoodEaten;

    private String[][] tileMaps;

    /// X = wall, O = skip, P = pacman, ' ' = food
    /// Ghosts: b = blue, o = orange, p = pink, r = red

    public Game() throws IOException {
        readFromJson();

        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        mapIndex = random.nextInt(tileMaps.length);

        //load images
        wallImage = new ImageIcon(getClass().getResource("/images/wall.png")).getImage();
        powerFoodImage = new ImageIcon(getClass().getResource("/images/powerFood.png")).getImage();
        cherryImage = new ImageIcon(getClass().getResource("/images/cherry.png")).getImage();

        blueGhostImages = new Image[] {
                new ImageIcon(getClass().getResource("/images/BlueGhostImages/blueGhostUp.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/BlueGhostImages/blueGhostDown.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/BlueGhostImages/blueGhostLeft.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/BlueGhostImages/blueGhostRight.png")).getImage()
        };

        orangeGhostImages = new Image[] {
                new ImageIcon(getClass().getResource("/images/OrangeGhostImages/orangeGhostUp.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/OrangeGhostImages/orangeGhostDown.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/OrangeGhostImages/orangeGhostLeft.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/OrangeGhostImages/orangeGhostRight.png")).getImage()
        };

        pinkGhostImages = new Image[] {
                new ImageIcon(getClass().getResource("/images/PinkGhostImages/pinkGhostUp.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/PinkGhostImages/pinkGhostDown.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/PinkGhostImages/pinkGhostLeft.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/PinkGhostImages/pinkGhostRight.png")).getImage()
        };

        redGhostImages = new Image[] {
                new ImageIcon(getClass().getResource("/images/RedGhostImages/redGhostUp.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/RedGhostImages/redGhostDown.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/RedGhostImages/redGhostLeft.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/RedGhostImages/redGhostRight.png")).getImage()
        };

        scaredGhostImage = new ImageIcon(getClass().getResource("/images/scaredGhost.png")).getImage();

        pacmanImages = new Image[] {
                new ImageIcon(getClass().getResource("/images/PacmanImages/pacmanUp.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/PacmanImages/pacmanDown.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/PacmanImages/pacmanLeft.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/PacmanImages/pacmanRight.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/PacmanImages/pacmanClosed.png")).getImage()
        };

        noWallsRows = new ArrayList<Integer>();
        noWallsColumns = new ArrayList<Integer>();

        loadMap();

        for(Block ghost: ghosts)
        {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection, walls, tileSize);
        }

        gameLoop = new Timer(50, this); // 20fps (1000/50)
        gameLoop.start();
    }

    private void readFromJson() throws IOException {
        try {
            String data = new String(Files.readAllBytes(Paths.get("./map.json")));
            JSONObject jsonObject = new JSONObject(data);

            rowCount = jsonObject.getInt("rowCount");
            columnCount = jsonObject.getInt("columnCount");
            tileSize = jsonObject.getInt("tileSize");

            boardWidth = columnCount * tileSize;
            boardHeight = rowCount * tileSize;

            JSONArray tileMapsArray = jsonObject.getJSONArray("tileMaps");
            tileMaps = new String[tileMapsArray.length()][];
            mapCount = tileMapsArray.length();

            for(int i = 0; i < tileMapsArray.length(); i++) {
                JSONArray selectedMap = tileMapsArray.getJSONArray(i);
                tileMaps[i] = new String[selectedMap.length()];

                for(int j = 0; j < selectedMap.length(); j++) {
                    tileMaps[i][j] = selectedMap.getString(j);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error while reading map.json: " + e.getMessage());
        }
    }

    private void findNoWallsRows() {
        for(int i=0; i < rowCount; ++i) {
            boolean found = true;

            for(int j=0; j < columnCount; ++j) {
                if(tileMaps[mapIndex][i].charAt(j) == 'X') {
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
                if(tileMaps[mapIndex][j].charAt(i) == 'X') {
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

        noWallsColumns.clear();
        noWallsRows.clear();

        isCherryTimerStarted = false;
        powerFoodEaten = false;

        foodsEaten = 0;

        for(int row = 0; row < rowCount; ++row) {
            for(int col = 0; col < columnCount; ++col) {
                String rowString = tileMaps[mapIndex][row];
                char tileMapChar = rowString.charAt(col);

                int x = col * tileSize;
                int y = row * tileSize;

                switch(tileMapChar) {
                    case 'X':
                        walls.add(new Wall(wallImage, x, y, tileSize, tileSize));
                        break;
                    case 'b':
                        ghosts.add(new Ghost(blueGhostImages, x, y, tileSize, tileSize));
                        break;
                    case 'o':
                        ghosts.add(new Ghost(orangeGhostImages, x, y, tileSize, tileSize));
                        break;
                    case 'p':
                        ghosts.add(new Ghost(pinkGhostImages, x, y, tileSize, tileSize));
                        break;
                    case 'r':
                        ghosts.add(new Ghost(redGhostImages, x, y, tileSize, tileSize));
                        break;
                    case 'P':
                        pacman = new Pacman(pacmanImages, x, y, tileSize, tileSize);
                        break;
                    case ' ':
                        foods.add(new Food(null, x + 14, y + 14, 4, 4, 10));
                        break;
                    case 'C':
                        foods.add(new Cherry(cherryImage, x, y , tileSize, tileSize, 100));
                        break;
                    case 'F':
                        foods.add(new PowerFood(powerFoodImage, x + 8, y + 8, 16, 16, 0));
                        break;
                }
            }
        }

        findNoWallsRows();
        findNoWallsColumns();
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
            if (food instanceof Cherry) {
                if (foodsEaten >= 20) {
                    if(!isCherryTimerStarted)
                    {
                        cherryTimer = new Timer(10000, e -> {
                            foods.remove(food);
                            cherryTimer.stop();
                            repaint();
                        });
                        cherryTimer.start();
                        isCherryTimerStarted = true;
                    }
                    g.drawImage(food.image, food.x, food.y, food.width, food.height, null);
                }
            } else {
                if (food.image != null) {
                    g.drawImage(food.image, food.x, food.y, food.width, food.height, null);
                } else {
                    g.fillRect(food.x, food.y, food.width, food.height);
                }
            }

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

    public boolean stillInFrame(Block block) {
        return block.x < boardWidth && block.x + block.width > 0 && block.y < boardHeight && block.y + block.height > 0;
    }

    public void move() {
        pacman.tryChangeDirection(walls, tileSize);

        pacman.updateAnimation();

        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        foodCollision();

        if(foods.isEmpty())
        {
            mapIndex++;
            mapIndex %= mapCount;
            loadMap();
            resetPositions();
        }

        changePosIfOutOfFrame(pacman);

        for (Block wall : walls) {
            if (Block.collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        for (Block ghost : ghosts) {
            if (Block.collision(ghost, pacman)) {
                if(!((Ghost) ghost).getScaredStatus())
                {
                    remainingLives -= 1;
                    if (remainingLives == 0) {
                        gameOver = true;
                        return;
                    }
                    resetPositions();
                }
                else {
                    ghost.reset();
                    score += 100;
                }
            }

            if(stillInFrame(ghost))
            {
                if (noWallsColumns.contains(ghost.x / tileSize) && ghost.direction != 'L' && ghost.direction != 'R') {
                    char newDirection = directions[random.nextInt(2) + 2];
                    ghost.updateDirection(newDirection, walls, tileSize);
                } else if (noWallsRows.contains(ghost.y / tileSize) && ghost.direction != 'U' && ghost.direction != 'D') {
                    char newDirection = directions[random.nextInt(2)];
                    ghost.updateDirection(newDirection, walls, tileSize);
                }
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
                Food foodItem = (Food) food;
                if (food instanceof PowerFood) {
                    powerFoodEaten = true;
                    iterator.remove();

                    for (Block ghost : ghosts) {
                        ((Ghost) ghost).setScaredStatus(true);
                    }
                    repaint();

                    if (powerFoodTimer != null) {
                        powerFoodTimer.stop();
                    }
                    powerFoodTimer = new Timer(10000, e -> {
                        for (Block ghost : ghosts) {
                            ((Ghost) ghost).setScaredStatus(false);
                        }
                        powerFoodTimer.stop();
                        repaint();
                    });
                    powerFoodTimer.setRepeats(false);
                    powerFoodTimer.start();

                } else if (food instanceof Cherry) {
                    if (foodsEaten >= 20) {
                        score += foodItem.foodScore;
                        iterator.remove();
                    }
                } else {
                    score += foodItem.foodScore;
                    foodsEaten++;
                    iterator.remove();
                }
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
    public void keyPressed(KeyEvent e) {}

    @Override

    public void keyReleased(KeyEvent e) {
        if(gameOver) {
            if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                gameOver = false;
                mapIndex = random.nextInt(tileMaps.length);
                loadMap();
                resetPositions();
                remainingLives = 3;
                score = 0;
                gameLoop.start();
            }
        }

        if(e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.setDesiredDirection('U');
            pacman.updateDirection('U', walls, tileSize);
        }

        if(e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.setDesiredDirection('D');
            pacman.updateDirection('D', walls, tileSize);
        }

        if(e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.setDesiredDirection('L');
            pacman.updateDirection('L', walls, tileSize);
        }

        if(e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.setDesiredDirection('R');
            pacman.updateDirection('R', walls, tileSize);
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
    public String[][] getTileMaps() {
        return tileMaps;
    }
}
