package org.example.models;

import java.awt.*;
import java.util.HashSet;

public class Pacman extends Block{
    private char desiredDirection;
    Image[] pacmanImages;
    private int animationFrame = 0;

    public Pacman(){}

    public Pacman(Image[] images, int x, int y, int width, int height){
        super(images[3], x, y, width, height);
        this.pacmanImages = images;
    }

    public void setDesiredDirection(char direction) {
        this.desiredDirection = direction;
    }

    @Override
    public void updateDirection(char direction, HashSet<Block> walls, int tileSize) {
        this.desiredDirection = direction; // Update desired direction
    }

    private boolean isInFrame(int boardWidth, int boardHeight) {
        return this.x >= 0 && this.x < boardWidth && this.y >= 0 && this.y < boardHeight;
    }

    public void tryChangeDirection(HashSet<Block> walls, int tileSize) {
        if (this.direction != this.desiredDirection && isInFrame(Game.boardWidth, Game.boardHeight)) {
            char prevDirection = this.direction;
            this.direction = this.desiredDirection;
            updateVelocity(tileSize);

            this.x += this.velocityX;
            this.y += this.velocityY;

            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity(tileSize);
                    return;
                }
            }
        }
        updateImage();
    }

    public void updateAnimation() {
        animationFrame = (animationFrame + 1) % 2;
        updateImage();
    }

    private void updateImage() {
        switch (this.direction) {
            case 'U':
                this.image = pacmanImages[animationFrame == 0 ? 0 : 4];
                break;
            case 'D':
                this.image = pacmanImages[animationFrame == 0 ? 1 : 4];
                break;
            case 'L':
                this.image = pacmanImages[animationFrame == 0 ? 2 : 4];
                break;
            case 'R':
                this.image = pacmanImages[animationFrame == 0 ? 3 : 4];
                break;
        }
    }

    @Override
    void reset() {
        this.x = this.startX;
        this.y = this.startY;
        this.image = this.pacmanImages[3];
    }
}
