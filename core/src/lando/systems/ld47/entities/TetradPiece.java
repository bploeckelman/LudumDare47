package lando.systems.ld47.entities;

import com.badlogic.gdx.graphics.Color;

public class TetradPiece {
    public int x;
    public int y;
    public Color color;

    public TetradPiece(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
