package lando.systems.ld47.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;


public class TetradPiece implements IShootable {
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

    public TetradPiece(TetradPiece clone) {
        this.owner = clone.owner;
        this.x = clone.x;
        this.y = clone.y;
        this.color = clone.color;
        this.removeReason = RemoveReason.NOT_REMOVED;
    }

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



//    // for shooting
//    // returns y position in grid
//    private TetradPiece targetBlock;
//    public Vector2 selectRandomBlock() {
//        targetBlock = points.random();
//        return gameBoard.getScreenCoordOfTetradPiece(targetBlock);
//    }

    @Override
    public void hit() {
        setDestroyTimer(0, .3f, TetradPiece.RemoveReason.SHOT);
    }

    @Override
    public Vector2 getTarget() {
        GameBoard gameBoard = owner.gameBoard;
        return (gameBoard != null) ? gameBoard.getScreenCoordOfTetradPiece(this) : null;
    }
}
