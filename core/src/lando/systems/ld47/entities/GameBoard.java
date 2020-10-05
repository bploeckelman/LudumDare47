package lando.systems.ld47.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedSet;
import lando.systems.ld47.Audio;
import lando.systems.ld47.GameState;
import lando.systems.ld47.input.PlayerInput;
import lando.systems.ld47.particles.Particle;
import lando.systems.ld47.particles.ParticleDecal;
import lando.systems.ld47.utils.OrbitPointLight;
import lando.systems.ld47.utils.PointLight;

public class GameBoard {
    public static int TILESWIDE = 10;
    public static int TILESHIGH = 20;
    public static int MAX_POINT_LIGHTS = 5;

    // number of tetrads on the board before the guy will shoot one
    public final int minTetradsToShoot = 5;
    // number of tetrads on the board before the guy will transport one
    public final int minTetradsToTransport = 3;

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

    public boolean pulloutEnabled = false;
    private boolean previousBlockCleared = false;
    PerspectiveCamera boardCam;
    private Color ambientColor = new Color(.4f, .4f, .4f, 1f);
    private Color directionColor = new Color(.6f, .6f, .6f, 1f);
    private Vector3 directionLight = new Vector3(.1f, -.5f, .8f).nor();

    public PointLight[] pointLights;
    private GameBackPlate backPlate;

    private DecalBatch decalBatch;

    public GameBoard(GameState gameState) {
        this.gameState = gameState;
        this.playerInput = gameState.gameScreen.playerInput;
        this.camera = gameState.gameScreen.worldCamera;
        this.tetrads = new Array<>();
        float width = TILESWIDE * Tetrad.POINT_WIDTH;
        float height = TILESHIGH * Tetrad.POINT_WIDTH;
        gameBounds = new Rectangle((camera.viewportWidth - width) / 2f, (camera.viewportHeight - height) / 2f, width, height);
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

        decalBatch = new DecalBatch(new CameraGroupStrategy(boardCam));

        backPlate = new GameBackPlate();

        pointLights = new PointLight[MAX_POINT_LIGHTS];
        pointLights[0] = new OrbitPointLight(0, new Vector3(0, 0, 15), new Color(.5f, 0, .5f, 1f), new Vector3(.5f, .5f, 0));
        pointLights[1] = new OrbitPointLight(1, new Vector3(0, 0, -15), new Color(0f, .5f, .5f, 1f), new Vector3(-.5f, .5f, 0f));
        pointLights[2] = new OrbitPointLight(2, new Vector3(0, -15, 0), new Color(.5f, .5f, 0f, 1f), new Vector3(1, 0, 0));
        pointLights[3] = new PointLight(3, new Vector3(-30, 10, 20), new Color(.3f, .3f, .3f, 1f));
        pointLights[4] = new PointLight(4, new Vector3(-30, 10, 20), new Color(.3f, .3f, .3f, 1f));
    }


    Tetrad lastSwap = null;
    // this happens in an update loop, so it's cool
    public Tetrad swapActiveTetrad(Tetrad hold) {
        Tetrad current = activeTetrad;
        if (hold == null) {
            if (current == null) { return null; }

            current.removeFromBoard();
            lastSwap = gameState.popNext();
        } else if (current == lastSwap) {
            gameState.gameScreen.playSound(Audio.Sounds.tet_noswap);
            return hold;
        } else {
            // current piece not the last one swapped in
            lastSwap = hold;
        }
        lastSwap.insertIntoBoard(this);
        activeTetrad = lastSwap;

        return current;
    }

    public void update(float dt) {
        if (isPaused()) return;

        Tetrad.GLOBAL_ANIM += dt;

        for (PointLight light : pointLights) {
            light.update(dt);
        }
        backPlate.update(dt);
        gameState.gameScreen.particles.updateDecals(dt, boardCam);

        for (int i = tetrads.size - 1; i >= 0; i--) {
            Tetrad tetrad = tetrads.get(i);
            tetrad.update(dt);
            if (tetrad.isEmpty()) {
                tetrads.removeIndex(i);
            }
        }

        boolean foundTiles = false;
        for (int y = TILESHIGH - 1; y >= 0; y--) {
            int cellsInRow = 0;
            for (int x = 0; x < TILESWIDE; x++) {
                for (Tetrad tetrad : tetrads) {
                    TetradPiece piece = tetrad.getPieceAt(x, y);
                    if (piece != null) {
                        cellsInRow++;
                        foundTiles = true;
                    }
                }
            }
            if (cellsInRow == 0 && foundTiles) {
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
                        Tetrad movingTetrad = activeTetrad;
                        float startY = movingTetrad.origin.y;
                        int count = 0;
                        while (moveDown(activeTetrad)) {
                            count++;
                        }
                        if (count >= 3) {
                            gameState.gameScreen.particles.addPlummetParticles(movingTetrad, startY);
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

        batch.setProjectionMatrix(gameState.gameScreen.shaker.getCombinedMatrix());

        batch.draw(gameTexture, gameBounds.x, gameBounds.y + gameBounds.height, gameBounds.width, -gameBounds.height);
        // Draw anything else in screen space here

//        if (activeTetrad != null) {
//            for (TetradPiece piece : activeTetrad.points) {
//                Vector2 p = getScreenCoordOfTetradPiece(piece);
//                batch.draw(gameState.assets.whiteCircle, p.x, p.y, 4, 4);
//            }
//        }

    }

    public void renderFrameBuffer(SpriteBatch batch) {
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
        shader.setUniformi("u_texture1", 1);
        gameState.gameScreen.assets.noiseTexture.bind(1);
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

        Array<ParticleDecal> decals = gameState.gameScreen.particles.activeDecals;
        for (ParticleDecal decal : decals) {
            if (decal.ready) {
                decalBatch.add(decal.decal);
            }
        }
        decalBatch.flush();

        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        gameFB.end();
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

    Vector2 endPos = new Vector2();
    private void checkForPullOut() {
        if (!tetrads.contains(tetradToRemove, true)){
            tetradToRemove = null;
        }
        if (pulloutEnabled) {
            if (tetradToRemove != null) {
                gameState.setNext(new Tetrad(tetradToRemove));
                for (TetradPiece point : tetradToRemove.points) {
                    point.setDestroyTimer(0, .4f, TetradPiece.RemoveReason.STOLEN);
                    Vector2 pos = getScreenCoordOfTetradPiece(point);
                    Rectangle endBounds = gameState.gameScreen.gameHud.getNextBox().bounds;
                    endPos.set(endBounds.x + endBounds.width/2f, endBounds.y + endBounds.height/2f);
                    gameState.gameScreen.particles.addTeleportParticles(pos, endPos);
                }


            } else {
                tetradToRemove = getFreeBottomPiece();
            }
            pulloutEnabled = false;
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
            gameState.gameScreen.playSound(Audio.Sounds.tet_clearLine);
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
//                    piece.setDestroyTimer(delay, (tetrad.origin.x + piece.x) * .1f, TetradPiece.RemoveReason.CLEARED);
                    piece.setDestroyTimer(MathUtils.random(.1f, .5f), MathUtils.random(.6f, 1f), TetradPiece.RemoveReason.CLEARED);
//                    gameState.gameScreen.particles.addPieceDeleteParticles(gameBounds.x + (tetrad.origin.x + piece.x + .5f) * Tetrad.POINT_WIDTH,
//                            gameBounds.y + (tetrad.origin.y + piece.y + .5f) * Tetrad.POINT_WIDTH,
//                            tetrad.color);
                }
            }
        }
    }

    Vector3 tempVec3 = new Vector3();
    public Vector2 getScreenCoordsOfTetrad(Tetrad tetrad) {
        tempVec3.set(tetrad.getCenter().x + tetrad.origin.x, tetrad.getCenter().y + tetrad.origin.y, 1);
        return getScreenCoordFromGameCoord(tempVec3);
    }

    public Vector2 getScreenCoordOfTetradPiece(TetradPiece piece) {
        tempVec3.set(piece.owner.origin.x + piece.x + .5f, piece.owner.origin.y + piece.y + .5f, .5f);
        return getScreenCoordFromGameCoord(tempVec3);
    }

    Vector2 tempVec2 = new Vector2();
    public Vector2 getScreenCoordFromGameCoord(Vector3 gameCoord) {
        boardCam.project(gameCoord, gameBounds.x, gameBounds.y, gameBounds.width, gameBounds.height);
        return tempVec2.set(gameCoord.x, gameCoord.y);
    }

    public boolean canTransportTetrad() {
        return tetrads.size >= minTetradsToTransport;
    }

    public boolean canShootBlock() {
        return tetrads.size >= minTetradsToShoot;
    }

    public Tetrad getRandomTetrad() {
        return tetrads.random();
    }

    public TetradPiece getRandomBlock() {
        Tetrad tetrad = tetrads.random();
        if (tetrad != null) {
            return tetrad.points.random();
        }
        return null;
    }

    public void crash() {
        gameState.gameScreen.shaker.addDamage(100);
        gameState.gameScreen.playSound(Audio.Sounds.crash);
        pulloutEnabled = true;

    }

    public boolean isPaused() {
        return gameState.gameScreen.gameHud.getSettingsUI().isShown();
    }

}
