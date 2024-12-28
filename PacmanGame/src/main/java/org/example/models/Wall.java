package org.example.models;

import java.awt.*;
import java.util.HashSet;

public class Wall extends Block{

    public Wall() {};
    public Wall(Image image, int x, int y, int width, int height){
        super(image, x, y, width, height);
    }

    @Override
    public void updateDirection(char direction, HashSet<Block> walls, int tileSize) {}
    @Override
    void reset() {}
}
