package lando.systems.ld47.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class OrbitPointLight extends PointLight{

    Vector3 axis;
    float accum;
    Vector3 initialPos;

    public OrbitPointLight(int index, Vector3 pos, Color color, Vector3 axis) {
        super(index, pos, color);
        this.axis = axis;
        accum = 0;
        this.initialPos = new Vector3(pos);
        update(0);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        accum += dt;
        pos.set(this.initialPos).rotateRad(axis, accum).add(5, 10, 0);
    }
}
