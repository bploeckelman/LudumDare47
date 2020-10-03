package lando.systems.ld47.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld47.Game;

public class Tetrad {
    public static float POINT_WIDTH = 25;

    private GameBoard gameBoard;
    private Game game;
    public Array<Vector2> points;

    public Vector2 position;
    public Vector2 origin;
    public Color color;
    private int bounds;

    private static float hue = 0;


    public Tetrad (Game game) {
        this.game = game;

        position = new Vector2(0,0);


        points = new Array<>();
        buildNewPiece();
        color = new Color(Color.WHITE);
        hue += 137;
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


        for (Vector2 point : points){
//            point.sub(centerX, centerY);
            if (dir < 0){
                point.set(point.y, bounds-point.x);
            } else {
                point.set(bounds-point.y, point.x);
            }
//            point.add(centerX, centerY);
        }

    }

    public boolean containsPoint(int x, int y) {
        for (Vector2 point : points){
            if (point.x + origin.x == x && point.y + origin.y == y) return true;
        }
        return false;
    }

    public void deleteRow(int y){
        for (int i = points.size - 1; i >= 0; i -- ){
            Vector2 point = points.get(i);
            if (y == origin.y + point.y){
                points.removeIndex(i);
            } else if (y < origin.y + point.y){
                point.y -= 1;
            }

        }
    }

    public void insertIntoBoard(GameBoard gameBoard){
        this.gameBoard = gameBoard;
        int height = 0;
        for (Vector2 point : points){
            if (point.y > height) height = (int)point.y;
        }
        origin = new Vector2(4, 19 - height);
    }

    public void  setAbsolutePosition(float x, float y) {
        origin = null;
        position.set(x, y);
    }

    public void center(Vector2 center) {
        // I'm sorry
        position.set(center.x - 50, center.y - 50);
    }

    private void buildNewPiece(){
        int type = MathUtils.random(6);
        switch(type){
            case 0:
                // I
                points.add(new Vector2(0, 2));
                points.add(new Vector2(1, 2));
                points.add(new Vector2(2, 2));
                points.add(new Vector2(3, 2));
                bounds = 3;
                break;
            case 1:
                // L
                points.add(new Vector2(0, 2));
                points.add(new Vector2(0, 1));
                points.add(new Vector2(1, 1));
                points.add(new Vector2(2, 1));
                bounds = 2;
                break;
            case 2:
                // Other L
                points.add(new Vector2(0, 1));
                points.add(new Vector2(1, 1));
                points.add(new Vector2(2, 1));
                points.add(new Vector2(2, 2));
                bounds = 2;
                break;
            case 3:
                // square
                points.add(new Vector2(0, 0));
                points.add(new Vector2(0, 1));
                points.add(new Vector2(1, 0));
                points.add(new Vector2(1, 1));
                bounds = 1;
                break;
            case 4:
                // S
                points.add(new Vector2(0, 1));
                points.add(new Vector2(1, 1));
                points.add(new Vector2(1, 2));
                points.add(new Vector2(2, 2));
                bounds = 2;
                break;
            case 5:
                // other S
                points.add(new Vector2(0, 2));
                points.add(new Vector2(1, 2));
                points.add(new Vector2(1, 1));
                points.add(new Vector2(2, 1));
                bounds = 2;
                break;
            case 6:
                // T
                points.add(new Vector2(0, 1));
                points.add(new Vector2(1, 1));
                points.add(new Vector2(1, 2));
                points.add(new Vector2(2, 1));
                bounds = 2;
                break;
        }
        // I

    }
}
