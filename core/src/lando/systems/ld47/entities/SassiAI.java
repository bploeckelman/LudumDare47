package lando.systems.ld47.entities;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld47.screens.GameScreen;
import lando.systems.ld47.utils.accessors.Vector2Accessor;

public class SassiAI {

    enum Actions { none, boardLeft, boardRight, shootLeft, shootRight,
        ramLeft, ramRight, shootNext, shootHold, teleportPiece, dropPiece };

    float stunTime = 0;

    float actionDuration = 5f;
    float actionTime = 1f;
    boolean animating = false;

    Actions lastAction = Actions.none;

    // if the location of the board or it gets moved, this will need to change
    private final float[] yPosCoords = { 45, 65, 90, 110, 135, 160, 185, 210, 235, 265, 295, 325, 360, 395, 430, 465, 505, 545, 590, 635, 685 };

    private final float boardXBottomLeft = 450;
    private final float boardXTopLeft = 520;
    private final float boardXTopRight = 760;
    private final float boardXBottomRight = 830;
    private final float centerX = boardXBottomLeft + (boardXBottomRight - boardXBottomLeft) / 2;

    private final float sPathXDiff = (boardXTopLeft - boardXBottomLeft) * 1.1f;
    private final float minSPathXDiff = (boardXTopLeft - boardXBottomLeft) * 0.2f;
    private final float minTurnHeight = 50f; // amount of y traveling to warrant a turn
    private final float turnWidth;

    private Opponent opponent;
    private float speed = 250f;
    private GameScreen screen;

    // known way points
    private Vector2 topLeft;
    private Vector2 topRight;
    private Vector2 hold;
    private Vector2 next;

    public SassiAI(GameScreen screen, Opponent opponent) {
        this.screen = screen;
        this.opponent = opponent;

        turnWidth = opponent.size.x * 0.6f;

        float width = this.opponent.size.x;
        float height = this.opponent.size.y;

        float top = yPosCoords[yPosCoords.length - 1];
        topLeft = new Vector2(boardXTopLeft - width, top);
        topRight = new Vector2(boardXTopRight, top);

        Rectangle bounds = screen.gameHud.getNextBox().bounds;
        next = new Vector2(bounds.x - width, bounds.y + (bounds.y - height)/2);

        bounds = screen.gameHud.getHoldBox().bounds;
        hold = new Vector2(bounds.x - width, bounds.y + (bounds.y - height)/2);

        // place opponent at the top right
        opponent.position.set(topLeft.x, topLeft.y);
        opponent.direction = Opponent.Direction.right;
    }

    public void update(float dt) {
        if (animating) { return; }

        if (stunTime > 0) {
            stunTime -= dt;
            return;
        }

        actionTime -= dt;
        if (actionTime < 0) {
//            actionDuration -= dt;
//            actionTime = MathUtils.random(1f, Math.max(actionDuration, 1f));
            actionTime = 1;
            randomAction();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            stun();
        }
    }

    private void randomAction() {
        Actions action = getNextAction(lastAction);
        if (action == Actions.none) { return; }

        switch (action) {
            case boardLeft:
                move(topLeft, action);
                break;
            case boardRight:
                move(topRight, action);
                break;
            case shootLeft:
                Vector2 pos = getSidePos(true);
                if (pos == null) {
                    // nothing to shoot
                    return;
                }
                move(pos, action);
                break;
            case shootRight:
                pos = getSidePos(false);
                if (pos == null) {
                    // nothing to shoot
                    return;
                }
                move(pos, action);
                break;
            case ramLeft:
                break;
            case ramRight:
                break;
            case shootNext:
                break;
            case shootHold:
                break;
            case teleportPiece:
                break;
            case dropPiece:
                break;
        }

        animating = true;
    }

    private Actions getNextAction(Actions lastAction) {
//        // 20% chance of doing nothing
//        if (MathUtils.random(4) < 1) { return Actions.none; }
//
//        Actions[] actions = Actions.values();
        Actions[] actions = new Actions[] { Actions.none, Actions.boardLeft, Actions.shootLeft, Actions.boardRight, Actions.shootRight };

        Actions next = lastAction;
        while (next == lastAction) {
            next = actions[MathUtils.random(1, actions.length - 1)];
        }
        return next;
    }

    private final Vector2 sidePos = new Vector2();
    private Vector2 getSidePos(boolean left) {
        Array<Integer> rows = screen.gameBoard.getRowEnds(left);
        if (rows.isEmpty()) { return null; }

        int yIndex = rows.random();

        float x = (left)
                ? boardXBottomLeft - opponent.size.x + (boardXTopLeft - boardXBottomLeft) / 20 * yIndex
                : boardXBottomRight - (boardXBottomRight -boardXTopRight) / 20 * yIndex;

        sidePos.set(x, yPosCoords[yIndex]);
        return sidePos;
    }

    private void move(Vector2 movePos, Actions action) {
        Vector2 pos = opponent.position;

        float dy = movePos.y - pos.y;
        float dx = movePos.x - pos.x;

        setOrientation(dy);

        Vector2[] wayPoints = getWayPoints(pos, movePos, action);
        dx += (turnWidth * wayPoints.length * 2);

        float moveTime = (float)Math.sqrt(dx*dx + dy*dy) / speed;


        Tween moveTween = Tween.to(pos, Vector2Accessor.XY, moveTime);
        for (Vector2 wp : wayPoints) {
            moveTween.waypoint(wp.x, wp.y);
        }
        moveTween.target(movePos.x, movePos.y).ease(TweenEquations.easeInOutCubic);

        opponent.setState(Opponent.State.moving);

        Timeline.createSequence()
                .push(moveTween)
                .start(screen.game.tween)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        completeMove(pos, movePos, action);
                    }
                });
    }

    private void completeMove(Vector2 from, Vector2 to, Actions action) {

        Opponent.State state = Opponent.State.idle;
        Opponent.Direction direction = Opponent.Direction.right;
        switch (action) {
            case none:
                // won't happen
                break;
            case boardLeft:
                break;
            case boardRight:
                direction = Opponent.Direction.left;
                break;
            case shootLeft:
                state = Opponent.State.punch;
                break;
            case shootRight:
                direction = Opponent.Direction.left;
                state = Opponent.State.punch;
                break;
            case ramLeft:
                break;
            case ramRight:
                direction = Opponent.Direction.left;
                break;
            case shootNext:
                state = Opponent.State.punch;
                break;
            case shootHold:
                state = Opponent.State.punch;
                break;
            case teleportPiece:
                break;
            case dropPiece:
                break;
        }

        //opponent.setState(state);
        opponent.setState(Opponent.State.idle);
        opponent.animState = Opponent.AnimDirection.level;
        opponent.direction = direction;

        lastAction = action;
        animating = false;
    }

    private void setOrientation(float dy) {
        if (dy > 0){
            opponent.animState = Opponent.AnimDirection.up;
        } else if (dy < 0) {
            opponent.animState = Opponent.AnimDirection.down;
        } else {
            opponent.animState = Opponent.AnimDirection.level;
        }
    }

    private final Vector2[] emptyWayPoints = new Vector2[] {};
    private Vector2[] getWayPoints(Vector2 pos, Vector2 movePos, Actions action) {

        float dy = Math.abs(pos.y - movePos.y);
        if (dy < minTurnHeight) { return emptyWayPoints; };

        boolean up = pos.y < movePos.y;
        boolean right = pos.x < movePos.x;

        float dx = Math.abs(pos.x - movePos.x);

        // s path
        if (dx < sPathXDiff) {
            if (dx < minSPathXDiff) { return emptyWayPoints; }

            float yOffset = (Math.abs(pos.y - movePos.y)) / 3 * ((up) ? 1 : -1);
            float xOffset = turnWidth * ((movePos.x < centerX) ? 1 : -1);

            return new Vector2[]{
                    new Vector2(pos.x + xOffset, pos.y + yOffset),
                    new Vector2(movePos.x - xOffset, pos.y + (yOffset * 2f))
            };
        } else if (right && movePos.x > centerX || !right && movePos.x < centerX) {
            float yOffset = (Math.abs(pos.y - movePos.y)) / 4 * ((up) ? -1 : 1);
            float xOffset = turnWidth * ((movePos.x < centerX) ? 1 : -1);
            return new Vector2[]{
                    new Vector2(movePos.x - xOffset, movePos.y + yOffset)
            };
        }

        return emptyWayPoints;
    }

    // for tetris
    public void stun() {
        opponent.setState(Opponent.State.stun);
        stunTime = 5;
    }
}
