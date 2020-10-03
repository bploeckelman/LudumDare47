package lando.systems.ld47.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;



public class PlayerInput {

    public class TriggerState {
        public boolean pressed;
        public boolean triggered;

        public void reset() {
            pressed = triggered = false;
        }
    }

    private float horizontalOffset = 0.09f;
    private float dropOffset = 0.025f;

    private float rightHold = horizontalOffset;
    private float leftHold = horizontalOffset;
    private float downHold = dropOffset;

    private TriggerState horizontalState = new TriggerState();

    public void update(float dt) {
        rightHold -= dt;
        leftHold -= dt;
        downHold -= dt;
        horizontalState.reset();
    }

    public TriggerState isRightPressed() {
        horizontalState.pressed = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        horizontalState.triggered = (horizontalState.pressed && rightHold < 0);
        if (horizontalState.triggered) {
            rightHold = horizontalOffset;
        }
        return horizontalState;
    }

    public TriggerState isLeftPressed() {
        horizontalState.pressed = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        horizontalState.triggered = (horizontalState.pressed  && leftHold < 0);
        if (horizontalState.triggered) {
            leftHold = horizontalOffset;
        }
        return horizontalState;
    }

    public boolean isDownPressed() {
        boolean pressed = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean triggered = (pressed && downHold < 0);
        if (triggered) {
            downHold = dropOffset;
        }
        return  triggered;
    }

    public boolean isRotateLeft() {
        return Gdx.input.isKeyJustPressed(Input.Keys.Q) || Gdx.input.isKeyJustPressed(Input.Keys.J);
    }

    public boolean isRotateRight() {
        return Gdx.input.isKeyJustPressed(Input.Keys.E) || Gdx.input.isKeyJustPressed(Input.Keys.L);
    }
}
