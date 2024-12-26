package models;

import java.awt.*;
import java.util.HashSet;

public class Block {
    int x;
    int y;
    int width;
    int height;
    Image image;

    int startX;
    int startY;
    char direction = 'U'; // Up Down Left Right
    int velocityX = 0;
    int velocityY = 0;

    public Block(Image image, int x, int y, int width, int height) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.startX = x;
        this.startY = y;
    }

    public static boolean collision(Block a, Block b) {
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    void updateDirection(char direction, HashSet<Block> walls, int tileSize) {
        //if(this.direction == direction) return;    // - ASTA DOAR PENTRU PACMAN NU SI PENTRU FANTOMITE

        char prevDirection = this.direction;
        this.direction = direction;

        updateVelocity(tileSize);

        this.x += this.velocityX;
        this.y += this.velocityY;

        for(Block wall: walls)
        {
            if(collision(this, wall))
            {
                this.x -= this.velocityX;
                this.y -= this.velocityY;

                this.direction = prevDirection;

                updateVelocity(tileSize);
            }
        }
    }

    private void updateVelocity(int tileSize) {
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

    void reset() {
        this.x = this.startX;
        this.y = this.startY;
    }
}