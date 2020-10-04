package lando.systems.ld47.entities;

import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedSet;
import lando.systems.ld47.Audio;
import lando.systems.ld47.GameState;
import lando.systems.ld47.input.PlayerInput;

public class GameBoard {
    public static int TILESWIDE = 10;
    public static int TILESHIGH = 20;

    private final GameState gameState;
    private final OrthographicCamera camera;
    private final PlayerInput playerInput = new PlayerInput();
    private final Array<Tetrad> tetrads;

    private Tetrad activeTetrad;
    private Tetrad tetradToRemove;
    public Rectangle gameBounds;
    float fallInterval;
    float timeToFall;

    int blocksToFallTilRemove;

    public GameBoard(GameState gameState) {
        this.gameState = gameState;
        this.camera = gameState.gameScreen.worldCamera;
        this.tetrads = new Array<>();
        float width = TILESWIDE * Tetrad.POINT_WIDTH;
        float height = TILESHIGH * Tetrad.POINT_WIDTH;
        gameBounds = new Rectangle((camera.viewportWidth - width) / 2f, (camera.viewportHeight - height) / 2f, width, height);
        blocksToFallTilRemove = 3;
        fallInterval = 1f;
        timeToFall = fallInterval;

        // TODO: move this stuff up to BaseScreen or Game so we can use controllers on other screens
        Controllers.clearListeners();
        Controllers.addListener(playerInput);
    }

    public void update(float dt) {
        Tetrad.GLOBAL_ANIM += dt;

        for (int i = tetrads.size -1; i >= 0; i--) {
            Tetrad tetrad = tetrads.get(i);
            tetrad.update(dt);
            if (tetrad.isEmpty()) {
                tetrads.removeIndex(i);
            }
        }

        for (int y = TILESHIGH - 1; y >= 0 ; y--){
            int cellsReady = 0;
            for (int x = 0; x < TILESWIDE; x++){
                for (Tetrad tetrad : tetrads) {
                    TetradPiece piece = tetrad.getPieceAt(x, y);
                    if ( piece != null && piece.remove){
                        cellsReady ++;
                    }
                }
            }
            if (cellsReady == TILESWIDE) {
                deleteRow(y);
            }
        }

        boolean boardResolving = false;
        for (Tetrad tetrad : tetrads) {
            if (tetrad.resolvingTetrad()) boardResolving = true;
        }

        if (!boardResolving) {

            playerInput.update(dt);
            if (activeTetrad == null) {
                activeTetrad = gameState.popNext();

                activeTetrad.insertIntoBoard(this);
                if (invalidMove(activeTetrad, Vector2.Zero)) {
                    //GAME OVER
                    //TODO something else
                    tetrads.clear();
                }
                timeToFall = fallInterval;
            }

            checkForPullOut();

            if (activeTetrad != null) {
                PlayerInput.TriggerState state = playerInput.isRightPressed();
                if (state.pressed) {
                    if (state.triggered && !invalidMove(activeTetrad, new Vector2(1, 0))) {
                        activeTetrad.origin.x += 1;
                    }
                } else if (playerInput.isLeftPressed().triggered) {
                    if (!invalidMove(activeTetrad, new Vector2(-1, 0))) {
                        activeTetrad.origin.x -= 1;
                    }
                }

                if (playerInput.isRotateRight()) {
                    activeTetrad.rotate(-1);
                    if (invalidMove(activeTetrad, Vector2.Zero)) {
                        activeTetrad.rotate(1);
                    }
                }
                if (playerInput.isRotateLeft()) {
                    activeTetrad.rotate(1);
                    if (invalidMove(activeTetrad, Vector2.Zero)) {
                        activeTetrad.rotate(-1);
                    }
                }

                if (playerInput.isDownPressed()) {
                    moveDown(activeTetrad);
                }

                if (activeTetrad != null) {
                    if (playerInput.isPlungedPressed()) {
                        while (moveDown(activeTetrad)) {
                        }
                    }
                }
            }

            timeToFall -= dt;
            if (timeToFall < 0) {
                moveDown(activeTetrad);
            }

            if (activeTetrad != null) {
                activeTetrad.update(dt);
            }
        }


    }



    public void render(SpriteBatch batch) {
        batch.setColor(.3f, .3f, .3f, .8f);
        batch.draw(gameState.assets.whitePixel, gameBounds.x, gameBounds.y, gameBounds.width, gameBounds.height);
        batch.setColor(Color.WHITE);

        for (Tetrad tetrad : tetrads) {
            tetrad.render(batch);
        }
        if (activeTetrad != null) {
            activeTetrad.render(batch);
        }
    }


    public boolean invalidMove(Tetrad tetrad, Vector2 dir) {
        return collidesWithBlocks(tetrad, dir) || collidesWithWalls(tetrad, dir);
    }

    Vector2 testOrigin = new Vector2();

    public boolean collidesWithBlocks(Tetrad tetrad, Vector2 dir) {
        if (tetrad.origin == null) return false;
        testOrigin.set(tetrad.origin.x + dir.x, tetrad.origin.y + dir.y);
        for (int i = 0; i < tetrad.points.size; i++) {
            TetradPiece point = tetrad.points.get(i);
            for (int j = 0; j < tetrads.size; j++) {
                Tetrad placedPiece = tetrads.get(j);
                if (placedPiece == tetrad) continue;
                for (TetradPiece placedPoint : placedPiece.points) {
                    if (placedPiece.origin == null) continue;
                    if (point.x + testOrigin.x == placedPoint.x + placedPiece.origin.x &&
                            point.y + testOrigin.y == placedPoint.y + placedPiece.origin.y) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean collidesWithWalls(Tetrad tetrad, Vector2 dir) {
        if (tetrad.origin == null) return false;
        testOrigin.set(tetrad.origin.x + dir.x, tetrad.origin.y + dir.y);
        for (TetradPiece point : tetrad.points){
            if (point.x + testOrigin.x < 0 || point.x + testOrigin.x >= TILESWIDE) return true;
            if (point.y + testOrigin.y < 0 || point.y + testOrigin.y >= TILESHIGH) return true;
        }
        return false;
    }

    public Tetrad getFreeBottomPiece(){
        OrderedSet<Tetrad> bottomPieces = new OrderedSet<>();
        for (int x = 0; x < TILESWIDE; x++) {
            for (Tetrad tetrad : tetrads) {
                if (tetrad.containsPoint(x, 0)){
                    bottomPieces.add(tetrad);
                }
            }
        }
        Array<Tetrad> tetradArray = bottomPieces.orderedItems();
        tetradArray.shuffle();
        for (Tetrad t : tetradArray){
            if (!collidesWithBlocks(t, new Vector2(0, -1))){
                return t;
            }
        }
        return null;
    }

    public boolean moveDown(Tetrad tetrad) {
        boolean valid = false;
        if (invalidMove(tetrad, new Vector2(0, -1))) {
            tetrads.add(activeTetrad);
            playSound(Audio.Sounds.tet_land);
            activeTetrad = null;
            fallInterval = Math.max(.2f, fallInterval - .005f);

            // TODO make this more async
            checkForFullRows();

            blocksToFallTilRemove --;

            if (!tetrads.contains(tetradToRemove, true)) {
                tetradToRemove = null;
            }



        } else {
            tetrad.origin.y -= 1;
            valid = true;
        }
        timeToFall = fallInterval;
        return valid;
    }

    private void checkForPullOut() {
        if (blocksToFallTilRemove <= 0){
            if (tetradToRemove != null){
                tetradToRemove.flashing = false;
                gameState.setNext(tetradToRemove);
                tetrads.removeValue(tetradToRemove, true);
                if (tetrads.size > 0){
                    boolean removedLine = true;
                    float volume = 0.5f;
                    while (removedLine) {
                        boolean clearLine = true;
                        for (int x = 0; x < TILESWIDE; x++) {
                            for (Tetrad t : tetrads) {
                                if (t.containsPoint(x, 0)) {
                                    clearLine = false;
                                }
                            }
                        }
                        if (clearLine) {
                            deleteRow(0);
                            playSound(Audio.Sounds.tet_clearLine, volume+= 0.1f);
                            removedLine = true;
                        } else {
                            removedLine = false;
                        }
                    }
                }
            }
            tetradToRemove = getFreeBottomPiece();
            if (tetradToRemove != null) {
                tetradToRemove.flashing = true;
            }
            blocksToFallTilRemove = 3;
        }
    }

    public void checkForFullRows() {
        int rowsCleared = 0;
        for (int y = TILESHIGH - 1; y >= 0; y--) {
            boolean emptySpot = false;
            for (int x = 0; x < TILESWIDE; x++) {
                boolean tileFilled = false;
                for (Tetrad tetrad : tetrads) {
                    if (tetrad.containsPoint(x, y)) {
                        tileFilled = true;
                    }
                }
                if (!tileFilled) {
                    emptySpot = true;
                }
            }
            if (!emptySpot) {
                markRowForDeletion(y, .2f);
//                y += 1;
                rowsCleared++;
            }

        }

        gameState.gameScreen.shaker.addDamage(.2f*rowsCleared);


        switch (rowsCleared){
            case 1:
                gameState.addScore(100); break;
            case 2:
                gameState.addScore(300); break;
            case 3:
                gameState.addScore(500); break;
            case 4:
                gameState.addScore(800); break;
        }
    }

    private void deleteRow(int y) {
        for (Tetrad tetrad : tetrads) {
            tetrad.deleteRow(y);
        }
    }

    private void playSound(Audio.Sounds sound) {
        this.playSound(sound, 1f);
    }

    private void playSound(Audio.Sounds sound, float volume) {
        gameState.gameScreen.game.audio.playSound(sound, volume);
    }

    private void markRowForDeletion(int y, float delay) {
        for (Tetrad tetrad : tetrads) {
            for (TetradPiece piece : tetrad.points) {
                if (tetrad.origin.y + piece.y == y) {
                    piece.destroyTimer = delay + (tetrad.origin.x + piece.x) * .05f;
                    gameState.gameScreen.particles.addPieceDeleteParticles(gameBounds.x + (tetrad.origin.x + piece.x + .5f) * Tetrad.POINT_WIDTH,
                            gameBounds.y + (tetrad.origin.y + piece.y + .5f) * Tetrad.POINT_WIDTH,
                            tetrad.color);
                }
            }
        }

    }
}
