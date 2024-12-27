package org.example.models;

import java.awt.*;
import java.util.HashSet;

public class Ghost extends Block{

    public Ghost() {};
    public Ghost(Image image, int x, int y, int width, int height){
        super(image, x, y, width, height);
    }

    @Override
    public void updateDirection(char direction, HashSet<Block> walls, int tileSize) {
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
}
