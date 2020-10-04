package lando.systems.ld47.entities;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld47.screens.GameScreen;
import lando.systems.ld47.ui.HoldUI;
import lando.systems.ld47.utils.accessors.Vector2Accessor;

public class SassiAI {

    float time = 5f;
    float stunTime = 0;

    float actionDuration = 5f;
    float actionTime = 1f;
    boolean animating = false;
    int lastAction = 3; // walk left

    float tleft, tright, bleft, bright, top, bottom, maxX, maxY;

    private final Vector2 holdPosition;
    private final Vector2 nextPosition;

    // if the location of the board or it gets moved, this will need to change
    private float[] yPosCoords = { 685, 635, 590, 545, 505, 465, 430, 395, 360, 325, 295, 265, 235, 210, 185, 160, 135, 110, 90, 65, 45 };
    // bottom x, top x left side
    private Vector2 leftDiff = new Vector2(450, 520);
    // top x, bottom y right side
    private Vector2 rightDiff = new Vector2(760,830);

    // right
    private int direction = 1;

    private Opponent opponent;
    private GameScreen screen;

    public SassiAI(GameScreen screen, Opponent opponent) {
        this.screen = screen;
        this.opponent = opponent;

        float width = this.opponent.size.x;

        tleft = leftDiff.y - width;
        tright = rightDiff.x;
        bleft = leftDiff.x - width;
        bright = rightDiff.y;

        top = yPosCoords[0];
        bottom = yPosCoords[19];

        Rectangle bounds = screen.gameHud.getNextBox().bounds;
        nextPosition = new Vector2(bounds.x, bounds.y);

        bounds = screen.gameHud.getHoldBox().bounds;
        holdPosition = new Vector2(bounds.x, bounds.y);

        maxX = nextPosition.x - bleft;
        maxY = top - bottom;
        opponent.position.set(tleft, top);
    }

    public void update(float dt) {
        time -= dt;

        if (animating) { return; }

        if (stunTime > 0) {
            stunTime -= dt;
            return;
        }

        actionTime -= dt;
        if (actionTime < 0) {
            actionDuration -= dt;
            actionTime = MathUtils.random(1f, Math.max(actionDuration, 1f));
            animating = true;
            randomAction();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            stun();
        }
    }

    private void randomAction() {
        int action = getNextAction();
        switch (lastAction) {
            case 0:
                hitNext();
                break;
            case 1:
                hitLeft(lastAction != 2);
                 break;
            case 2:
                hitRight(lastAction != 1);
                break;
            case 3:
                walkLeft();
                break;
            case 4:
                walkRight();
                break;
            case 5:
                hitHold();
                break;
        }
        lastAction = action;
    }

    private int getNextAction() {
        int value = lastAction;
        while (value == lastAction) {
            value = MathUtils.random(4);
        }
        return value;
    }

    private void walkRight() {
        walk(tright, top, getTopState());
    }

    private void walkLeft() {
        walk(tleft, top, getTopState());
    }

    private Opponent.State getTopState() {
        return (MathUtils.random(10) < 3) ? Opponent.State.throwing : Opponent.State.idle;
    }

    private void hitNext() {
        walk(nextPosition.x, nextPosition.y, Opponent.State.punch, screen.gameHud.getNextBox(), false);
    }

    private void hitHold() {
        walk(holdPosition.x, holdPosition.y, Opponent.State.punch, screen.gameHud.getHoldBox(), false);
    }

    private void hitRight(boolean drop) {
        Vector2 pos = getSidePos(false);
        if (pos == null) {
            animating = false;
            return;
        }
        walk(pos.x, pos.y, Opponent.State.punch, null, drop);
    }

    private void hitLeft(boolean drop) {
        Vector2 pos = getSidePos(true);
        if (pos == null) {
            animating = false;
            return;
        }
        walk(pos.x, pos.y, Opponent.State.punch, null, drop);
    }

    Vector2 sidePos = new Vector2();
    private Vector2 getSidePos(boolean left) {
        Array<Integer> rows = screen.gameBoard.getRowEnds(left);
        if (rows.isEmpty()) { return null; }

        // array is reverse and I'm too lazy to switch it
        int yPos = (yPosCoords.length - 1) - rows.random();

        float x = getXPos(left, yPos);
        sidePos.set(x, yPosCoords[yPos]);
        return sidePos;
    }

    private float getXPos(boolean left, int index) {
        if (left) {
            // x is bottom left, y is top left
            return leftDiff.x + (leftDiff.y - leftDiff.x) / 20 * index;
        }
        // x is top right, y is bottom right
        return rightDiff.y - (leftDiff.y - leftDiff.x) / 20 * index;
    }

    private void walk(float x, float y, Opponent.State state) {
        this.walk(x, y, state, null, false);
    }

    private void walk(float x, float y, Opponent.State state, HoldUI punchBox, boolean drop) {

        Vector2 pos = opponent.position;

        float dy = Math.abs(y - pos.y) / 2;
        float dx = Math.abs(x - pos.x) / 2;

        float wy = Math.max(y, pos.y) + 25;
        float wx = Math.min(x, pos.x) + dx;

        if (drop) {
            wy = Math.min(y, pos.y) + dy;
        }

        float time = 5f * Math.max(dx / maxX, dy / maxY);

        opponent.setState(Opponent.State.idle);
        Timeline.createSequence()
                .push(Tween.to(opponent.position, Vector2Accessor.XY, time).waypoint(wx, wy).target(x, y).ease(TweenEquations.easeInOutCubic))
                .start(screen.game.tween)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        opponent.setState(state);
                        if (punchBox != null) {
                            punchBox.punchBox();
                        }
                        animating = false;
                    }
                });

    }

    // for tetris
    public void stun() {
        opponent.setState(Opponent.State.stun);
        stunTime = 5;
    }
}
