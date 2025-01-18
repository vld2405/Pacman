package org.example.models;

import java.awt.*;
import java.util.HashSet;

public abstract class Block {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected Image image;

    protected int startX;
    protected int startY;
    protected char direction; // Up Down Left Right
    protected int velocityX = 0;
    protected int velocityY = 0;

    public Block() {};

    public Block(Image image, int x, int y, int width, int height) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.startX = x;
        this.startY = y;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

    public static boolean collision(Block a, Block b) {
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public abstract void updateDirection(char direction, HashSet<Block> walls, int tileSize);

    protected void updateVelocity(int tileSize) {
        if(direction == 'U') {       // up
            this.velocityX = 0;
            this.velocityY = -tileSize / 8;
        }
        else if(direction == 'D') {  // down
            this.velocityX = 0;
            this.velocityY = tileSize / 8;
        }
        else if(direction == 'L') {  // left
            this.velocityX = -tileSize / 8;
            this.velocityY = 0;
        }
        else if(direction == 'R') {  // right
            this.velocityX = tileSize / 8;
            this.velocityY = 0;
        }
    }

    abstract void reset();
}