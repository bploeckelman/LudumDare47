package lando.systems.ld47.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld47.Game;

public class Tetrad {
    public static float POINT_WIDTH = 20;

    private GameBoard gameBoard;
    private Game game;
    public Array<Vector2> points;

    public Vector2 position;
    public Vector2 origin;
    public Color color;

    private static float hue = 0;


    public Tetrad (GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        this.game = gameBoard.game;

        position = new Vector2(0,0);


        points = new Array<>();
        // Test just an I for now
        points.add(new Vector2(0, 1));
        points.add(new Vector2(1, 1));
        points.add(new Vector2(2, 1));
        points.add(new Vector2(3, 1));
        color = new Color(Color.WHITE);
        hue += 37;
        color.fromHsv(hue, 1, 1);
    }

    public void update (float dt) {
        // Allow tetrads to live outside of the gameboard
        if (origin != null) {
            position.set(gameBoard.gameBounds.x + origin.x * POINT_WIDTH, gameBoard.gameBounds.y + origin.y * POINT_WIDTH);
        }
    }

    public void render(SpriteBatch batch) {
        batch.setColor(color);
        for (Vector2 point : points) {
            batch.draw(game.assets.tetradSquare, position.x + (POINT_WIDTH * point.x), position.y + (POINT_WIDTH * point.y), POINT_WIDTH, POINT_WIDTH);
        }
        batch.setColor(Color.WHITE);
    }

    public void rotate(int dir){
        int minX = 4;
        int maxX = 0;
        int minY = 4;
        int maxY = 0;
        for (Vector2 point : points){
            if (point.x < minX) minX = (int)point.x;
            if (point.x > maxX) maxX = (int)point.x;
            if (point.y < minY) minY = (int)point.y;
            if (point.y > maxY) maxY = (int)point.y;
        }
        int centerX = (maxX - minX)/2 + minX;
        int centerY = (maxY - minY)/2 + minY;

        for (Vector2 point : points){
            point.sub(centerX, centerY);
            if (dir < 0){
                point.set(point.y, -point.x);
            } else {
                point.set(point.y, point.x);
            }
            point.add(centerX, centerY);
        }

    }

    public void insertIntoBoard(){
        // TODO make this push down if the piece is vertical after it drops out
        origin = new Vector2(4, 18);
    }

    public void  setAbsolutePosition(float x, float y) {
        origin = null;
        position.set(x, y);
    }
}
