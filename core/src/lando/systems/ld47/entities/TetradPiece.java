package lando.systems.ld47.entities;

import com.badlogic.gdx.graphics.Color;


public class TetradPiece {
    public enum RemoveReason { NOT_REMOVED, CLEARED, STOLEN, SHOT};
    public int x;
    public int y;
    public Color color;
    private Float destroyTimer;
    public float maxDestroyTimer;
    private float destroyDelay;
    public boolean remove;
    public RemoveReason removeReason;
    public Tetrad owner;

    public TetradPiece(Tetrad owner, int x, int y, Color color) {
        this.owner = owner;
        this.x = x;
        this.y = y;
        this.color = color;
        this.removeReason = RemoveReason.NOT_REMOVED;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update(float dt) {
        if (destroyTimer != null){
            if (destroyDelay > 0){
                destroyDelay -= dt;
            } else {
                destroyTimer -= dt;
                if (destroyTimer <= 0) {
                    remove = true;
                }
            }
        }
    }

    public void setDestroyTimer(float delay, float ttl, RemoveReason reason) {
        destroyDelay = delay;
        destroyTimer = ttl;
        maxDestroyTimer = ttl;
        this.removeReason = reason;
    }

    public Float getDestroyTimer() {
        return destroyTimer;
    }
}
