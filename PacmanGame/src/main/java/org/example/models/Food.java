package org.example.models;

import java.awt.*;
import java.util.HashSet;

public class Food extends Block{

    public int foodScore;

    public Food() {};
    public Food(Image image, int x, int y, int width, int height, int score){
        super(image, x, y, width, height);
        foodScore = score;
    }

    @Override
    public void updateDirection(char direction, HashSet<Block> walls, int tileSize) {}
    @Override
    void reset() {}
}
