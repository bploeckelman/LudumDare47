package lando.systems.ld47.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedSet;
import lando.systems.ld47.Audio;
import lando.systems.ld47.GameState;
import lando.systems.ld47.input.PlayerInput;
import lando.systems.ld47.utils.OrbitPointLight;
import lando.systems.ld47.utils.PointLight;

public class GameBoard {
    public static int TILESWIDE = 10;
    public static int TILESHIGH = 20;
    public static int MAX_POINT_LIGHTS = 5;

    public final GameState gameState;
    private final PlayerInput playerInput;
    private final OrthographicCamera camera;

    private final Array<Tetrad> tetrads;

    FrameBuffer gameFB;
    Texture gameTexture;

    public Tetrad activeTetrad;
    public Tetrad tetradToRemove;
    public Rectangle gameBounds;
    float fallInterval;
    float timeToFall;

    int blocksToFallTilRemove;
    private boolean previousBlockCleared = false;
    PerspectiveCamera boardCam;
    private Color ambientColor = new Color(.4f, .4f, .4f, 1f);
    private Color directionColor = new Color(.6f, .6f, .6f, 1f);
    private Vector3 directionLight = new Vector3(.1f, -.5f, .8f).nor();

    public PointLight[] pointLights;
    private GameBackPlate backPlate;

    public GameBoard(GameState gameState) {
        this.gameState = gameState;
        this.playerInput = gameState.gameScreen.playerInput;
        this.camera = gameState.gameScreen.worldCamera;
        this.tetrads = new Array<>();
        float width = TILESWIDE * Tetrad.POINT_WIDTH;
        float height = TILESHIGH * Tetrad.POINT_WIDTH;
        gameBounds = new Rectangle((camera.viewportWidth - width) / 2f, (camera.viewportHeight - height) / 2f, width, height);
        blocksToFallTilRemove = 3;
        fallInterval = 1f;
        timeToFall = fallInterval;



        gameFB = new FrameBuffer(Pixmap.Format.RGBA8888, (int)gameBounds.width, (int)gameBounds.height, true);
        gameTexture = gameFB.getColorBufferTexture();

        boardCam = new PerspectiveCamera(60, 12, 20);
//        boardCam.setToOrtho(false);
        boardCam.up.set(0,0,1);
        boardCam.position.set(5,0, 17);
        boardCam.far = 400;
        boardCam.lookAt(5f, 8, 0);
        boardCam.update();

        backPlate = new GameBackPlate();

        pointLights = new PointLight[MAX_POINT_LIGHTS];
        pointLights[0] = new OrbitPointLight(0, new Vector3(0, 0, 15), new Color(.5f, 0, .5f, 1f), new Vector3(.5f, .5f, 0));
        pointLights[1] = new OrbitPointLight(1, new Vector3(0, 0, -15), new Color(0f, .5f, .5f, 1f), new Vector3(-.5f, .5f, 0f));
        pointLights[2] = new OrbitPointLight(2, new Vector3(0, -15, 0), new Color(.5f, .5f, 0f, 1f), new Vector3(1, 0, 0));
        pointLights[3] = new PointLight(3, new Vector3(-30, 10, 20), new Color(.3f, .3f, .3f, 1f));
        pointLights[4] = new PointLight(4, new Vector3(-30, 10, 20), new Color(.3f, .3f, .3f, 1f));
    }

    // this happens in an update loop, so it's cool
    public Tetrad swapActiveTetrad(Tetrad tetrad) {
        Tetrad current = activeTetrad;
        Vector2 origin = null;
        if (current != null) {
            origin = current.removeFromBoard();
        }

        if (tetrad == null) {
            origin = null;
            tetrad = gameState.popNext();
        }

        // figure out positioning
        tetrad.insertIntoBoard(this, origin);
        // Test that is actually fits in the game
        if (invalidMove(tetrad, Vector2.Zero)) {
            if (!invalidMove(tetrad, new Vector2(-1, 0))) {
                tetrad.origin.x -= 1;
            }
            else if (!invalidMove(tetrad, new Vector2(1, 0))) {
                tetrad.origin.x += 1;
            } else {
                // can't fit into the board
                // TODO: Play some sound here
                if (current != null) {
                    current.insertIntoBoard(this, tetrad.origin);
                }
                return tetrad;

            }
        }

        activeTetrad = tetrad;

        return current;
    }

    public void update(float dt) {
        Tetrad.GLOBAL_ANIM += dt;

        for (PointLight light : pointLights) {
            light.update(dt);
        }
        backPlate.update(dt);

        for (int i = tetrads.size - 1; i >= 0; i--) {
            Tetrad tetrad = tetrads.get(i);
            tetrad.update(dt);
            if (tetrad.isEmpty()) {
                tetrads.removeIndex(i);
            }
        }

        for (int y = TILESHIGH - 1; y >= 0; y--) {
            int cellsReady = 0;
            for (int x = 0; x < TILESWIDE; x++) {
                for (Tetrad tetrad : tetrads) {
                    TetradPiece piece = tetrad.getPieceAt(x, y);
                    if (piece != null && piece.remove) {
                        cellsReady++;
                    }
                }
            }
            if (cellsReady == TILESWIDE) {
                deleteRow(y);
            }
        }

        for (int i = tetrads.size -1; i >= 0; i--) {
            Tetrad tetrad = tetrads.get(i);
            if (tetrad.isEmpty()) {
                tetrads.removeIndex(i);
                gameState.freeTetrad(tetrad);
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
                    gameState.gameScreen.gameOver();
                }
                timeToFall = fallInterval;
            }

            if (!tetrads.contains(tetradToRemove, true)) {
                tetradToRemove = null;
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
                    handleRotate(-1);
                }
                if (playerInput.isRotateLeft()) {
                    handleRotate(1);
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


//        for (Tetrad tetrad : tetrads) {
//            tetrad.render(batch);
//        }
        batch.end();

        gameFB.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        ShaderProgram shader = gameState.gameScreen.assets.boardShader;
        shader.bind();
        shader.setUniformf("u_viewPos", boardCam.position);
        shader.setUniformMatrix("u_projTrans", boardCam.combined);
        shader.setUniformf("u_ambient", ambientColor);
        shader.setUniformf("u_direction_dir", directionLight);
        shader.setUniformf("u_direction_color", directionColor);
        for(PointLight light : pointLights){
            light.addToShader(shader);
        }
        backPlate.renderMesh(shader);

        shader = gameState.gameScreen.assets.blockShader;

        shader.bind();
        shader.setUniformMatrix("u_projTrans", boardCam.combined);
        shader.setUniformf("u_viewPos", boardCam.position);
        shader.setUniformf("u_ambient", ambientColor);
        shader.setUniformf("u_direction_dir", directionLight);
        shader.setUniformf("u_direction_color", directionColor);
        shader.setUniformi("u_texture", 0);
        gameState.gameScreen.assets.blockTextures.bind(0);
        for(PointLight light : pointLights){
            light.addToShader(shader);
        }

        for (Tetrad tetrad : tetrads) {
            tetrad.renderModels(shader);
        }
        if (activeTetrad != null) {
            activeTetrad.renderModels(shader);
        }
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        gameFB.end();


        batch.begin();
        batch.setProjectionMatrix(gameState.gameScreen.shaker.getCombinedMatrix());
//        if (activeTetrad != null) {
//            activeTetrad.render(batch);
//        }

        batch.draw(gameTexture, gameBounds.x, gameBounds.y + gameBounds.height, gameBounds.width, -gameBounds.height);
    }


    private void handleRotate(int dir) {
        activeTetrad.rotate(dir);
        if (invalidMove(activeTetrad, Vector2.Zero)) {
            if (collidesWithWalls(activeTetrad, Vector2.Zero)) {
                //Test one away first
                if (!invalidMove(activeTetrad, new Vector2(-1, 0))) {
                    activeTetrad.origin.x -= 1;
                } else if (!invalidMove(activeTetrad, new Vector2(1, 0))) {
                    activeTetrad.origin.x += 1;
                } else if (!invalidMove(activeTetrad, new Vector2(-2, 0))) {
                    activeTetrad.origin.x -= 2;
                } else if (!invalidMove(activeTetrad, new Vector2(2, 0))) {
                    activeTetrad.origin.x += 2;
                } else {
                    activeTetrad.rotate(-dir);
                }
            } else {
                activeTetrad.rotate(-dir);
            }
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
        for (TetradPiece point : tetrad.points) {
            if (point.x + testOrigin.x < 0 || point.x + testOrigin.x >= TILESWIDE) return true;
            if (point.y + testOrigin.y < 0 || point.y + testOrigin.y > TILESHIGH) return true;
        }
        return false;
    }

    public Tetrad getFreeBottomPiece() {
        OrderedSet<Tetrad> bottomPieces = new OrderedSet<>();
        for (int x = 0; x < TILESWIDE; x++) {
            for (Tetrad tetrad : tetrads) {
                if (tetrad.containsPoint(x, 0)) {
                    bottomPieces.add(tetrad);
                }
            }
        }
        Array<Tetrad> tetradArray = bottomPieces.orderedItems();
        tetradArray.shuffle();

        if (tetradArray.size > 0) {
            return tetradArray.first();

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

            blocksToFallTilRemove--;


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
        if (blocksToFallTilRemove <= 0) {
            if (tetradToRemove != null) {
                tetradToRemove.flashing = false;
                gameState.setNext(tetradToRemove);
                tetrads.removeValue(tetradToRemove, true);
                if (tetrads.size > 0) {
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
                            playSound(Audio.Sounds.tet_clearLine, volume += 0.1f);
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

        gameState.gameScreen.shaker.addDamage(.2f * rowsCleared);

        float comboMultiplier = 1 + Math.max(gameState.getCombo(), 10) / 10;
        switch (rowsCleared) {
            case 1:
                gameState.addScore(Math.round(100 * comboMultiplier), 1);
                break;
            case 2:
                gameState.addScore(Math.round(300 * comboMultiplier), 2);
                break;
            case 3:
                gameState.addScore(Math.round(500 * comboMultiplier), 3);
                break;
            case 4:
                gameState.addScore(Math.round(800 * comboMultiplier), 4);
                break;
        }
        if (rowsCleared > 0) {
            gameState.addCombo();
        }
        else {
            gameState.breakCombo();
        }

        previousBlockCleared = rowsCleared > 0 ? true : false;

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

    private final Array<Integer> endblocks = new Array<Integer>(TILESHIGH);
    // gets the number of blocks on the ends for punching
    public Array<Integer> getRowEnds(boolean left) {
        int column = left ? 0 : TILESWIDE - 1;

        endblocks.clear();

        // if this is already checked on each update, include this there
        for (Tetrad tetrad : tetrads) {
            for (TetradPiece piece : tetrad.points) {
                if (piece.x + tetrad.origin.x == column) {
                    endblocks.add(new Integer(piece.y + (int)tetrad.origin.y));
                }
            }
        }

        return endblocks;
    }
}
