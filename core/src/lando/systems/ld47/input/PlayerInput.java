package lando.systems.ld47.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.controllers.mappings.Xbox;


public class PlayerInput extends ControllerAdapter {

    public static class TriggerState {
        public boolean pressed;
        public boolean triggered;

        public void reset() {
            pressed = triggered = false;
        }
    }

    public static class ControllerState {
        public Controller controller = null;

        public boolean moveLeftPressed;
        public boolean moveRightPressed;
        public boolean moveDownPressed;

        public boolean moveUpPressed;
        public boolean moveUpJustPressed;

        public boolean plungeButtonPressed;
        public boolean plungeButtonJustPressed;

        public boolean rotateLeftButtonPressed;
        public boolean rotateLeftButtonJustPressed;

        public boolean rotateRightButtonPressed;
        public boolean rotateRightButtonJustPressed;

        public boolean holdButtonPressed;
        public boolean holdButtonJustPressed;

        public ControllerState() {
            reset();
        }

        public void reset() {
            moveLeftPressed              = false;
            moveRightPressed             = false;
            moveDownPressed              = false;
            moveUpPressed                = false;
            moveUpJustPressed            = false;
            plungeButtonPressed          = false;
            plungeButtonJustPressed      = false;
            rotateLeftButtonPressed      = false;
            rotateLeftButtonJustPressed  = false;
            rotateRightButtonPressed     = false;
            rotateRightButtonJustPressed = false;
            holdButtonPressed            = false;
            holdButtonJustPressed        = false;
        }
    }
    private ControllerState controllerState = new ControllerState();

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

        if (controllerState.controller != null) {
            // NOTE: not sure if povCode 0 is guaranteed to be what we want?
            // check dpad first so stick doesn't overwrite that state
            PovDirection dPadDir = controllerState.controller.getPov(0);
            if (dPadDir != PovDirection.center) {
                if      (dPadDir == PovDirection.west)  controllerState.moveLeftPressed  = true;
                else if (dPadDir == PovDirection.east)  controllerState.moveRightPressed = true;
                else if (dPadDir == PovDirection.south) controllerState.moveDownPressed  = true;
                else {
                    boolean dPadUpPressed = (dPadDir == PovDirection.north);
                    controllerState.moveUpJustPressed = (dPadUpPressed && !controllerState.moveUpPressed);
                    controllerState.moveUpPressed = dPadUpPressed;
                }
            } else {
                float deadZone = 0.4f;

                float horizValue = controllerState.controller.getAxis(Xbox.L_STICK_HORIZONTAL_AXIS);
                controllerState.moveLeftPressed  = (horizValue < -deadZone);
                controllerState.moveRightPressed = (horizValue >  deadZone);

                float vertValue = controllerState.controller.getAxis(Xbox.L_STICK_VERTICAL_AXIS);
                controllerState.moveDownPressed = (vertValue > deadZone);

                // NOTE: we're not doing the immediate plunge this way cause its too sensitive... button press instead
//                boolean axisUpPressed = (vertValue < -deadZone);
//                controllerState.moveUpJustPressed = (axisUpPressed && !controllerState.moveUpPressed);
//                controllerState.moveUpPressed = axisUpPressed;
                controllerState.moveUpJustPressed = false;
                controllerState.moveUpPressed = false;
            }

            boolean plungeButtonDown = controllerState.controller.getButton(Xbox.Y);
            controllerState.plungeButtonJustPressed = (plungeButtonDown && !controllerState.plungeButtonPressed);
            controllerState.plungeButtonPressed = plungeButtonDown;

            boolean lBumperDown = controllerState.controller.getButton(Xbox.L_BUMPER);
            boolean xButtonDown = controllerState.controller.getButton(Xbox.X);
            boolean rotateLeftDown = xButtonDown;
            controllerState.rotateLeftButtonJustPressed = (rotateLeftDown && !controllerState.rotateLeftButtonPressed);
            controllerState.rotateLeftButtonPressed = rotateLeftDown;

            boolean rBumperDown = controllerState.controller.getButton(Xbox.R_BUMPER);
            boolean bButtonDown = controllerState.controller.getButton(Xbox.B);
            boolean rotateRightDown = bButtonDown;

            controllerState.rotateRightButtonJustPressed = (rotateRightDown && !controllerState.rotateRightButtonPressed);
            controllerState.rotateRightButtonPressed = rotateRightDown;

            // hope this works
            boolean holdButtonDown = lBumperDown || rBumperDown;
            controllerState.holdButtonJustPressed = holdButtonDown && !controllerState.holdButtonPressed;
            controllerState.holdButtonPressed = holdButtonDown;
        } else {
            controllerState.reset();
        }
    }

    public TriggerState isRightPressed() {
        horizontalState.pressed = Gdx.input.isKeyPressed(Input.Keys.D)
                               || Gdx.input.isKeyPressed(Input.Keys.RIGHT)
                               || controllerState.moveRightPressed;
        horizontalState.triggered = (horizontalState.pressed && rightHold < 0);
        if (horizontalState.triggered) {
            rightHold = horizontalOffset;
        }
        return horizontalState;
    }

    public TriggerState isLeftPressed() {
        horizontalState.pressed = Gdx.input.isKeyPressed(Input.Keys.A)
                               || Gdx.input.isKeyPressed(Input.Keys.LEFT)
                               || controllerState.moveLeftPressed;
        horizontalState.triggered = (horizontalState.pressed  && leftHold < 0);
        if (horizontalState.triggered) {
            leftHold = horizontalOffset;
        }
        return horizontalState;
    }

    public boolean isDownPressed() {
        boolean pressed = Gdx.input.isKeyPressed(Input.Keys.S)
                       || Gdx.input.isKeyPressed(Input.Keys.DOWN)
                       || controllerState.moveDownPressed;
        boolean triggered = (pressed && downHold < 0);
        if (triggered) {
            downHold = dropOffset;
        }
        return  triggered;
    }

    public boolean isRotateRight() {
        return Gdx.input.isKeyJustPressed(Input.Keys.Q)
            || Gdx.input.isKeyJustPressed(Input.Keys.J)
            || controllerState.rotateRightButtonJustPressed;
    }

    public boolean isRotateLeft() {
        return Gdx.input.isKeyJustPressed(Input.Keys.E)
            || Gdx.input.isKeyJustPressed(Input.Keys.L)
            || controllerState.rotateLeftButtonJustPressed;
    }

    public boolean isPlungedPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.isKeyJustPressed(Input.Keys.W)
            || controllerState.plungeButtonJustPressed
            || controllerState.moveUpJustPressed;
    }

    public boolean isHoldPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.K)
                || Gdx.input.isKeyJustPressed(Input.Keys.TAB)
                || controllerState.holdButtonJustPressed;
    }

    // ------------------------------------------------------------------------------

    @Override
    public void connected(Controller controller) {
        Gdx.app.log("controller", "connected: " + controller.getName());
        controllerState.controller = controller;
    }

    @Override
    public void disconnected(Controller controller) {
        Gdx.app.log("controller", "disconnected: " + controller.getName());
        controllerState.controller = null;
    }

}
