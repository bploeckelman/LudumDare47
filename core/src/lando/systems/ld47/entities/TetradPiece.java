package lando.systems.ld47.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class TetradPiece {
    public int x;
    public int y;
    public Color color;
    public Float destroyTimer;
    public boolean remove;

    public TetradPiece(int x, int y, Color color) {

        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update(float dt) {
        if (destroyTimer != null){
            destroyTimer -= dt;
            if (destroyTimer <= 0) remove = true;
        }
    }

    public void setDestroyTimer(float time) {
        destroyTimer = time;
    }
}
