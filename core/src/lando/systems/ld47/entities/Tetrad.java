package lando.systems.ld47.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld47.Game;

public class Tetrad {
    public static float POINT_WIDTH = 25;
    private static float GLOBAL_HUE = 0;

    private GameBoard gameBoard;
    private Game game;
    public Array<TetradPiece> points;
    private Vector2 center = new Vector2();

    public Vector2 position;
    public Vector2 origin;
    public Color color;
    private int bounds;
    public boolean flashing;
    private float accum = 0;
    private float hue;

    private Animation<TextureRegion> animation;

    public Tetrad(Game game) {
        this.game = game;
        position = new Vector2(0, 0);

        // get your fucking shine block
        animation = game.assets.blueBlock;

        points = new Array<>();
        buildNewPiece();
        color = new Color(Color.WHITE);
        GLOBAL_HUE += 137;
        hue = GLOBAL_HUE;
        color.fromHsv(hue, 1, 1);
    }

    public void update(float dt) {
        accum += dt;
        for (TetradPiece point : points) {
            point.update(dt);
        }
        if (flashing){
            color.fromHsv(hue, MathUtils.sin(accum*10f), 1);
        } else {
            color.fromHsv(hue, 1, 1);
        }
        // Allow tetrads to live outside of the gameboard
        if (origin != null) {
            position.set(gameBoard.gameBounds.x + origin.x * POINT_WIDTH, gameBoard.gameBounds.y + origin.y * POINT_WIDTH);
        }
    }

    public void render(SpriteBatch batch) {
        batch.setColor(color);
        TextureRegion blockImage = animation.getKeyFrame(accum);
        for (TetradPiece point : points) {
            if (!point.remove) {
                batch.draw(blockImage, position.x + (POINT_WIDTH * point.x), position.y + (POINT_WIDTH * point.y), POINT_WIDTH, POINT_WIDTH);
            }
        }
        batch.setColor(Color.WHITE);
    }

    public void rotate(int dir) {
        for (TetradPiece point : points) {
            if (dir < 0) {
                point.set(point.y, bounds - point.x);
            } else {
                point.set(bounds - point.y, point.x);
            }
        }
    }

    public boolean containsPoint(int x, int y) {
        for (TetradPiece point : points) {
            if (point.x + origin.x == x && point.y + origin.y == y) return true;
        }
        return false;
    }

    public TetradPiece getPieceAt(int x, int y) {
        for (TetradPiece point : points) {
            if (point.x + origin.x == x && point.y + origin.y == y) return point;
        }
        return null;
    }

    public boolean resolvingTetrad() {
        for (TetradPiece piece : points) {
            if (piece.destroyTimer != null && piece.destroyTimer > 0) return true;
        }
        return false;
    }

    public void deleteRow(int y) {
        if (y < origin.y) {
            origin.y--;
        } else {
            for (int i = points.size - 1; i >= 0; i--) {
                TetradPiece point = points.get(i);
                if (y == origin.y + point.y) {
                    points.removeIndex(i);
                } else if (y < origin.y + point.y) {
                    point.y -= 1;
                }
            }
        }
    }

    public boolean isEmpty() {
        return points.size == 0;
    }

    public void insertIntoBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        int height = 0;
        for (TetradPiece point : points) {
            if (point.y > height) height = point.y;
        }
        origin = new Vector2(4, 19 - height);
    }

    public void setAbsolutePosition(float x, float y) {
        origin = null;
        position.set(x, y);
    }

    public void center(Vector2 center) {
        position.set(center.x - this.center.x * POINT_WIDTH, center.y - this.center.y * POINT_WIDTH);
    }

    private void buildNewPiece() {
        int type = MathUtils.random(6);
        switch (type) {
            case 0:
                // I
                points.add(new TetradPiece(0, 2, color));
                points.add(new TetradPiece(1, 2, color));
                points.add(new TetradPiece(2, 2, color));
                points.add(new TetradPiece(3, 2, color));
                center.set(2, 2.5f);
                bounds = 3;
                break;
            case 1:
                // L
                points.add(new TetradPiece(0, 2, color));
                points.add(new TetradPiece(0, 1, color));
                points.add(new TetradPiece(1, 1, color));
                points.add(new TetradPiece(2, 1, color));
                center.set(1.5f, 2f);
                bounds = 2;
                break;
            case 2:
                // Other L
                points.add(new TetradPiece(0, 1, color));
                points.add(new TetradPiece(1, 1, color));
                points.add(new TetradPiece(2, 1, color));
                points.add(new TetradPiece(2, 2, color));
                center.set(1.5f, 2f);
                bounds = 2;
                break;
            case 3:
                // square
                points.add(new TetradPiece(0, 0, color));
                points.add(new TetradPiece(0, 1, color));
                points.add(new TetradPiece(1, 0, color));
                points.add(new TetradPiece(1, 1, color));
                center.set(1f, 1f);
                bounds = 1;
                break;
            case 4:
                // S
                points.add(new TetradPiece(0, 1, color));
                points.add(new TetradPiece(1, 1, color));
                points.add(new TetradPiece(1, 2, color));
                points.add(new TetradPiece(2, 2, color));
                center.set(1.5f, 2f);
                bounds = 2;
                break;
            case 5:
                // other S
                points.add(new TetradPiece(0, 2, color));
                points.add(new TetradPiece(1, 2, color));
                points.add(new TetradPiece(1, 1, color));
                points.add(new TetradPiece(2, 1, color));
                center.set(1.5f, 2f);
                bounds = 2;
                break;
            case 6:
                // T
                points.add(new TetradPiece(0, 1, color));
                points.add(new TetradPiece(1, 1, color));
                points.add(new TetradPiece(1, 2, color));
                points.add(new TetradPiece(2, 1, color));
                center.set(1.5f, 2f);
                bounds = 2;
                break;
        }
        // I

    }
}
