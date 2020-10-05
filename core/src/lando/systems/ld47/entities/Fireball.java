package lando.systems.ld47.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Fireball {

    private float time = 0;
    public Vector2 position;
    private Animation<TextureRegion> animation;

    private float width, height, scaleX;
    private TextureRegion texture;

    private Opponent opponent;
    private Vector2 size;
    private Vector2 originOffset = new Vector2(32, 32);
    private Vector2 offset;

    public Fireball(Opponent opponent, Vector2 target) {
        this.opponent = opponent;
        animation = opponent.screen.assets.shot;
        texture = animation.getKeyFrame(0);

        width = texture.getRegionWidth();
        height = texture.getRegionHeight();
        size = new Vector2(width, height).scl(opponent.carScale);

        position = opponent.getFireballCenter();
        scaleX = (target.x < position.x) ? 1 : -1;

        float x = (scaleX > 0) ? -originOffset.x : originOffset.x - width;
        offset = new Vector2(x, -originOffset.y).scl(opponent.carScale);
    }

    public void update(float dt) {
        time += dt;
        texture = animation.getKeyFrame(time);
    }

    public void render(SpriteBatch batch) {
//        batch.setColor(Color.RED);
//
//        batch.draw(opponent.screen.assets.whitePixel, position.x + offset.x, position.y + offset.y, size.x, size.y);
        batch.setColor(Color.WHITE);

        batch.draw(texture, position.x + offset.x, position.y + offset.y,
                size.x / 2, 0, size.x, size.y, scaleX, 1, 0);

//        batch.setColor(Color.BLUE);
//        batch.draw(opponent.screen.assets.whitePixel, position.x -3, position.y -3, 6, 6);
//
//        batch.setColor(Color.WHITE);
    }
}
