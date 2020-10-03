package lando.systems.ld47.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class TextureUtils {
    public static TextureRegion createRegion(Texture source, int tileSize, int cellX, int cellY) {
        return createRegion(source, tileSize, tileSize, cellX, cellY);
    }

    public static TextureRegion createRegion(Texture source, int tileWidth, int tileHeight, int cellX, int cellY) {
        return createRegion(source, tileWidth, tileHeight, cellX, cellY, 0, 0);
    }

    public static TextureRegion createRegion(Texture source, int tileWidth, int tileHeight, int cellX, int cellY, int offsetX, int offsetY) {
        return new TextureRegion(source,
                cellX * (tileWidth  + offsetX),
                cellY * (tileHeight + offsetY),
                tileWidth, tileHeight);
    }

    public static Array<TextureRegion> newArrayOfTextureRegions(TextureRegion... regions) {
        return new Array<>(regions);
    }

    public static TextureRegion getRandomTextureRegion(Array<TextureRegion> regions) {
        return regions.get(MathUtils.random(0, regions.size - 1));
    }
}
