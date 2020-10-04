package lando.systems.ld47.entities;

import aurelienribon.tweenengine.*;
import aurelienribon.tweenengine.equations.Linear;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld47.screens.GameScreen;
import lando.systems.ld47.utils.accessors.Vector2Accessor;

public class SassiAI {

    float time = 5f;

    float actionDuration = 5f;
    float actionTime = 1f;
    boolean animating = false;
    int lastAction = -1;

    float left, right, top, bottom;

    private Vector2 scorePosition;
    private Vector2 nextPosition;

    // right
    private int direction = 1;

    private Sasquatch sasquatch;
    private GameScreen screen;

    public SassiAI(GameScreen screen, Sasquatch sasquatch) {
        this.screen = screen;
        this.sasquatch = sasquatch;

        float width = this.sasquatch.size.x;

        Rectangle bounds = screen.gameBoard.gameBounds;
        left = bounds.x - width;
        right = bounds.x + bounds.width;
        top = bounds.y + bounds.height;
        bottom = bounds.y;

        nextPosition = new Vector2(screen.gameHud.getNextPosition()).sub(width, 0);

        sasquatch.position.set(left, top);
    }

    public void update(float dt) {
        time -= dt;

        if (!animating) {
            actionTime -= dt;
            if (actionTime < 0) {
                actionTime -= dt;
                actionTime = MathUtils.random(1f, Math.max(actionTime, 1f));
                animating = true;
                randomAction();
            }
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
        walk(nextPosition.x, nextPosition.y, Sasquatch.SasquatchState.punch);
    }

    private void hitRight() {
        walk(right, randomY(), Sasquatch.SasquatchState.punch);
    }

    private void hitLeft() {
        walk(left, randomY(), Sasquatch.SasquatchState.punch);
    }

    private float randomY() {
        return bottom + (Tetrad.POINT_WIDTH * MathUtils.random(GameBoard.TILESHIGH));
    }

    private void walk(float x, float y, Sasquatch.SasquatchState state) {

        sasquatch.setState(Sasquatch.SasquatchState.walk);
        Timeline.createSequence()
                .push(Tween.to(sasquatch.position, Vector2Accessor.XY, MathUtils.random(2, 5)).target(x, y)
                        .ease(Linear.INOUT))
                .start(screen.game.tween)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        sasquatch.setState(state);
                        animating = false;
                    }
                });
    }
}
