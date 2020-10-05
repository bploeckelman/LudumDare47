package lando.systems.ld47.entities;

import aurelienribon.tweenengine.*;
import aurelienribon.tweenengine.equations.Quad;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld47.screens.GameScreen;
import lando.systems.ld47.ui.HoldUI;
import lando.systems.ld47.utils.accessors.Vector2Accessor;

public class SassiAI {

    enum Actions { none, boardLeft, boardRight, shootLeft, shootRight,
        ramLeft, ramRight, shootNext, shootHold, teleportPiece, dropPiece };

    enum BoardSide { left, right };

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
    private final HoldUI holdBox;

    // known way points
    private Vector2 topLeft;
    private Vector2 topRight;
    private Vector2 hold;
    private Vector2 next;

    private final GameBoard gameBoard;

    public SassiAI(GameScreen screen, Opponent opponent) {
        this.screen = screen;
        this.gameBoard = screen.gameBoard;
        this.opponent = opponent;
        this.holdBox = screen.gameHud.getHoldBox();

        turnWidth = opponent.size.x * 0.6f;

        float width = this.opponent.size.x;
        float height = this.opponent.size.y;

        float top = yPosCoords[yPosCoords.length - 1];
        topLeft = new Vector2(boardXTopLeft - width, top);
        topRight = new Vector2(boardXTopRight, top);

        Rectangle bounds = screen.gameHud.getNextBox().bounds;
        next = new Vector2(bounds.x - width, bounds.y + (bounds.height - height)/2);

        bounds = holdBox.bounds;
        hold = new Vector2(bounds.x - width, bounds.y + (bounds.height - height)/2);

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

        System.out.println(lastAction + " -> " + action);
        switch (action) {
            case boardLeft:
                move(topLeft, action);
                break;
            case boardRight:
                move(topRight, action);
                break;
            case shootLeft:
                moveSide(BoardSide.left, true, action);
                break;
            case shootRight:
                moveSide(BoardSide.right, true, action);
                break;
            case ramLeft:
                moveSide(BoardSide.left, false, action);
                break;
            case ramRight:
                moveSide(BoardSide.right, false, action);
                break;
            case shootNext:
                move(next, action);
                break;
            case shootHold:
                move(hold, action);
                break;
            case teleportPiece:
            case dropPiece:
                return; // todo: implement
        }

        animating = true;
    }

    Array<Actions> actions = new Array<>(Actions.values().length);
    private Actions getNextAction(Actions lastAction) {
        // 20% chance of doing nothing
        // if (MathUtils.random(4) < 1) { return Actions.none; }

        actions.clear();
        actions.add(Actions.boardLeft, Actions.boardRight, Actions.shootNext);
        // prevent shoots and rams from same side
        if (gameBoard.canShootBlock()) {
            if (lastAction != Actions.ramRight) {
                actions.add(Actions.shootRight);
            } else if (lastAction != Actions.ramLeft) {
                actions.add(Actions.shootLeft);
            }
        }
        if (gameBoard.canTransportTetrad()) {
            if (lastAction != Actions.shootLeft) {
                actions.add(Actions.ramLeft);
            } else if (lastAction != Actions.shootRight) {
                actions.add(Actions.ramRight);
            }
        }
        if (holdBox.hold != null) {
            actions.add(Actions.shootHold);
        }
        Actions next = lastAction;
        while (next == lastAction) {
            next = actions.random();
        }
        return next;

    }

    private void moveSide(BoardSide side, boolean isShooting, Actions action) {
        Vector2 pos = getSidePos(side, isShooting);
        if (pos == null) { return; } // nothing to do
        move(pos, action);
    }

    private final Vector2 sidePos = new Vector2();
    Tetrad target;
    private Vector2 getSidePos(BoardSide side, boolean isShooting) {
        int yIndex = yPosCoords.length / 2;
        if (isShooting) {
            target = gameBoard.getRandomTetrad();
            if (target == null) { return null; }
            yIndex = target.selectRandomBlock();
        }

        float x = (side == BoardSide.left)
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

        Timeline.createSequence().push(moveTween)
                .start(screen.game.tween)
                .setCallback((i, s) -> completeMove(action));
    }

    private void completeMove(Actions action) {

        Opponent.State state = Opponent.State.idle;
        Opponent.Direction direction = Opponent.Direction.right;
        boolean endAnimation = true;

        switch (action) {
            case boardRight:
                direction = Opponent.Direction.left;
                break;
            case shootLeft:
            case shootRight:
                direction = (action == Actions.shootRight) ? Opponent.Direction.left : Opponent.Direction.right;
                opponent.shoot(target);
                break;
            case ramLeft:
            case ramRight:
                if (action == Actions.ramRight) {
                    direction = Opponent.Direction.left;
                }
                endAnimation = false;
                ramBoard(action);
                break;
            case shootNext:
                opponent.shoot(screen.gameHud.getNextBox());
                break;
            case shootHold:
                opponent.shoot(holdBox);
                break;
            case teleportPiece:
                break;
            case dropPiece:
                break;
        }

        opponent.setState(state);
        opponent.direction = direction;

        lastAction = action;
        animating = !endAnimation;
    }

    private void setOrientation(float dy) {
        if (dy > 0) {
            opponent.animState = Opponent.AnimDirection.up;
        } else if (dy < 0) {
            opponent.animState = Opponent.AnimDirection.down;
        } else {
            opponent.animState = Opponent.AnimDirection.level;
        }
    }

    private void ramBoard(Actions action) {

        Vector2 pos = opponent.position;
        int modifier = (action == Actions.ramRight) ? -1 : 1;
        float xOffset = -turnWidth * modifier;
        float xHit = 10f * modifier;

        Timeline.createSequence()
                .pushPause(1f)
                .push(Tween.to(pos, Vector2Accessor.X, 0.3f).target(pos.x + xOffset).ease(Quad.OUT))
                .push(Tween.to(pos, Vector2Accessor.X, 0.2f).target(pos.x + xHit))
                .push(Tween.to(pos, Vector2Accessor.X, 0.2f).target(pos.x - xHit))
                .start(screen.tween)
                .setCallback((s, i) -> {
                        gameBoard.crash();
                        animating = false;
                    });
    }

    private final Vector2[] emptyWayPoints = new Vector2[] {};
    private Vector2[] getWayPoints(Vector2 pos, Vector2 movePos, Actions action) {

        float dy = Math.abs(pos.y - movePos.y);
        if (dy < minTurnHeight) { return emptyWayPoints; };

        boolean up = pos.y < movePos.y;
        boolean right = pos.x < movePos.x;

        float dx = Math.abs(pos.x - movePos.x);

        if (action == Actions.shootHold || action == Actions.shootNext) {
            if (pos.x > centerX) {
                // moving to next or hold from right side
                float yOffset = (Math.abs(pos.y - movePos.y)) / 2 * ((up) ? 1 : -1);

                return new Vector2[]{
                        new Vector2(pos.x - turnWidth, pos.y + yOffset)
                };
            }
        } else if (dx < sPathXDiff) {
            // s path
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
