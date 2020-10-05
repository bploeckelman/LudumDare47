package lando.systems.ld47.entities;

import com.badlogic.gdx.math.Vector2;

public interface IShootable {
    void hit();
    Vector2 getTarget();
}
