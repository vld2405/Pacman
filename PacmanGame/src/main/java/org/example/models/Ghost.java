package org.example.models;

import java.awt.*;
import java.util.HashSet;

public class Ghost extends Block{
    private Image[] ghostImages;

    public Ghost() {};
    public Ghost(Image[] images, int x, int y, int width, int height){
        super(images[0], x, y, width, height);
        this.ghostImages = images;
    }

    @Override
    public void updateDirection(char direction, HashSet<Block> walls, int tileSize) {
        char prevDirection = this.direction;
        this.direction = direction;

        updateVelocity(tileSize);
        updateOrientation(direction);

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

    public void updateOrientation(char direction) {
        switch(direction)
        {
            case 'U':
                image = ghostImages[0];
                break;
            case 'D':
                image = ghostImages[1];
                break;
            case 'L':
                image = ghostImages[2];
                break;
            case 'R':
                image = ghostImages[3];
                break;
        }
    }

    @Override
    void reset() {
        this.x = this.startX;
        this.y = this.startY;
    }
}
