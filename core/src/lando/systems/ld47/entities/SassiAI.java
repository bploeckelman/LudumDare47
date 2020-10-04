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
import lando.systems.ld47.ui.NextUI;
import lando.systems.ld47.utils.accessors.Vector2Accessor;

public class SassiAI {

    float time = 5f;
    float stunTime = 0;

    float actionDuration = 5f;
    float actionTime = 1f;
    boolean animating = false;
    int lastAction = 3; // walk left

    float left, right, top, bottom, maxX, maxY;

    private Vector2 holdPosition;
    private Vector2 nextPosition;

    // right
    private int direction = 1;

    private Sasquatch sasquatch;
    private GameScreen screen;

    public SassiAI(GameScreen screen, Sasquatch sasquatch) {
        this.screen = screen;
        this.sasquatch = sasquatch;

        float width = this.sasquatch.size.x / 2;

        Rectangle bounds = screen.gameBoard.gameBounds;
        left = bounds.x - width;
        right = bounds.x + bounds.width - width;
        top = bounds.y + bounds.height;
        bottom = bounds.y;

        bounds = screen.gameHud.getNextBox().bounds;
        nextPosition = new Vector2(bounds.x, bounds.y);

        bounds = screen.gameHud.getHoldBox().bounds;
        holdPosition = new Vector2(bounds.x, bounds.y);

        maxX = nextPosition.x - left;
        maxY = top - bottom;
        sasquatch.position.set(left, top);
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
        lastAction = getNextAction();
        switch (lastAction) {
            case 0:
                hitNext();
                break;
            case 1:
                hitLeft();
                break;
            case 2:
                hitRight();
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
    }

    private int getNextAction() {
        int value = lastAction;
        while (value == lastAction) {
            value = MathUtils.random(4);
        }
        return value;
    }

    private void walkRight() {
        walk(right, top, getTopState());
    }

    private void walkLeft() {
        walk(left, top, getTopState());
    }

    private Sasquatch.SasquatchState getTopState() {
        return (MathUtils.random(10) < 3) ? Sasquatch.SasquatchState.throwing : Sasquatch.SasquatchState.idle;
    }

    private void hitNext() {
        walk(nextPosition.x, nextPosition.y, Sasquatch.SasquatchState.punch, screen.gameHud.getNextBox());
    }

    private void hitHold() {
        walk(holdPosition.x, holdPosition.y, Sasquatch.SasquatchState.punch, screen.gameHud.getHoldBox());
    }

    private void hitRight() {
        float pos = randomY(false);
        if (pos == -1) {
            animating = false;
            return;
        }
        walk(right, pos, Sasquatch.SasquatchState.punch);
    }

    private void hitLeft() {
        float pos = randomY(true);
        if (pos == -1) {
            animating = false;
            return;
        }
        walk(left, pos, Sasquatch.SasquatchState.punch);
    }

    private float randomY(boolean left) {
        Array<Integer> rows = screen.gameBoard.getRowEnds(left);
        if (rows.isEmpty()) { return -1; }
        return bottom + (Tetrad.POINT_WIDTH * rows.random().intValue());
    }
    private void walk(float x, float y, Sasquatch.SasquatchState state) {
        this.walk(x, y, state, null);
    }

    private void walk(float x, float y, Sasquatch.SasquatchState state, HoldUI punchBox) {

        Vector2 pos = sasquatch.position;

        float dy = Math.abs(y - pos.y) / 2;
        float dx = Math.abs(x - pos.x) / 2;

        float wy = Math.max(y, pos.y) + 25;
        float wx = Math.min(x, pos.x) + dx;

        if (pos.x == x) {
            wy = Math.min(y, pos.y) + dy;
            wx = x + ((x == left) ? -10 : 10);
        }

        float time = 5f * Math.max(dx / maxX, dy / maxY);

        sasquatch.setState(Sasquatch.SasquatchState.walk);
        Timeline.createSequence()
                .push(Tween.to(sasquatch.position, Vector2Accessor.XY, time).waypoint(wx, wy).target(x, y).ease(TweenEquations.easeInOutCubic))
                .start(screen.game.tween)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        sasquatch.setState(state);
                        if (punchBox != null) {
                            punchBox.punchBox();
                        }
                        animating = false;
                    }
                });

    }

    // for tetris
    public void stun() {
        sasquatch.setState(Sasquatch.SasquatchState.stun);
        stunTime = 5;
    }
}
