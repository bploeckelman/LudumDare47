package lando.systems.ld47.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
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

        public boolean pauseButtonPressed;
        public boolean pauseButtonJustPressed;

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
            pauseButtonPressed           = false;
            pauseButtonJustPressed       = false;
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
                    // TODO: might want to not use the dpad to plunge, it can be touchy
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

            boolean rotateLeftDown = controllerState.controller.getButton(Xbox.X);
            controllerState.rotateLeftButtonJustPressed = (rotateLeftDown && !controllerState.rotateLeftButtonPressed);
            controllerState.rotateLeftButtonPressed = rotateLeftDown;

            boolean rotateRightDown = controllerState.controller.getButton(Xbox.B);
            controllerState.rotateRightButtonJustPressed = (rotateRightDown && !controllerState.rotateRightButtonPressed);
            controllerState.rotateRightButtonPressed = rotateRightDown;

            boolean rBumperDown = controllerState.controller.getButton(Xbox.R_BUMPER);
            boolean lBumperDown = controllerState.controller.getButton(Xbox.L_BUMPER);
            boolean holdButtonDown = lBumperDown || rBumperDown;
            controllerState.holdButtonJustPressed = holdButtonDown && !controllerState.holdButtonPressed;
            controllerState.holdButtonPressed = holdButtonDown;

            boolean startButtonDown = controllerState.controller.getButton(Xbox.START) || controllerState.controller.getButton(Xbox.BACK);
            controllerState.pauseButtonJustPressed = startButtonDown && !controllerState.pauseButtonPressed;
            controllerState.pauseButtonPressed = startButtonDown;
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
            || Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_LEFT)
            || Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_RIGHT)
            || controllerState.holdButtonJustPressed;
    }

    public boolean isAnyButtonPressed() {
        if (controllerState.controller == null) return false;

        // check dpad
        PovDirection povDirection = controllerState.controller.getPov(0);
        if (povDirection != PovDirection.center) return true;

        int numButtons = 20; // there's not actually 20 buttons, but this catches all of them and returns true if any are pressed
        for (int i = 0; i < numButtons; ++i) {
            if (controllerState.controller.getButton(i)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPauseButtonJustPressed() {
        return controllerState.pauseButtonJustPressed;
    }

    public boolean isGamepadConnected() {
        return (controllerState.controller != null);
    }

    // if the controller is connected when launching the game it doesn't register, call this to reset it
    public void recheckController() {
        if (controllerState.controller == null && !Controllers.getControllers().isEmpty()) {
            controllerState.controller = Controllers.getControllers().first();
        }
    }

    // ------------------------------------------------------------------------------

    @Override
    public void connected(Controller controller) {
        // Gdx.app.log("controller", "connected: " + controller.getName());
        controllerState.controller = controller;
    }

    @Override
    public void disconnected(Controller controller) {
        // Gdx.app.log("controller", "disconnected: " + controller.getName());
        controllerState.controller = null;
    }

}
