package lando.systems.ld47.utils;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class RenderUtils {

    public static void draw(SpriteBatch batch, TextureRegion texture, Rectangle bounds, float padding) {
        batch.draw(texture,
                bounds.x + padding,
                bounds.y + padding,
                bounds.width - 2f * padding,
                bounds.height - 2f * padding);
    }

    public static void draw(SpriteBatch batch, TextureRegion texture, Rectangle bounds) {
        draw(batch, texture, bounds, 0f);
    }

    public static void draw(SpriteBatch batch, NinePatch ninepatch, Rectangle bounds) {
        draw(batch, ninepatch, bounds, false);
    }

    public static void draw(SpriteBatch batch, NinePatch ninepatch, Rectangle bounds, boolean flipHorizontal) {
        ninepatch.draw(batch, bounds.x, bounds.y,
                bounds.width / 2f, bounds.height / 2f,
                bounds.width, bounds.height,
                flipHorizontal ? -1f : 1f, 1f,
                0f);
    }


}
