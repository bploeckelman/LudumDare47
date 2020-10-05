package lando.systems.ld47.entities;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import lando.systems.ld47.Game;

public class Tetrad implements Pool.Poolable {

    enum FACE {TOP, LEFT, RIGHT, FRONT}
    public static float POINT_WIDTH = 40;
    private static float GLOBAL_HUE = 0;
    public static float GLOBAL_ANIM = 0;

    public static float blockHeight = 1f;
    private static Color ghostColor = new Color(1,1,1,1f);

    private GameBoard gameBoard;
    private Game game;
    public Array<TetradPiece> points;
    private Vector2 center = new Vector2();

    public Vector2 position;
    public Vector2 origin;
    public Color color;
    private int type;
    private int bounds;
    public boolean flashing;
    private float accum = 0;
    public float scale = 1;

    // Mesh things
    private static final int NUM_COMPONENTS_POSITION = 3;
    private static final int NUM_COMPONENTS_NORMAL = 3;
    private static final int NUM_COMPONENTS_TEXTURE = 2;
    private static final int NUM_COMPONENTS_COLOR = 4;
    private static final int NUM_COMPONENTS_PER_VERTEX = NUM_COMPONENTS_POSITION + NUM_COMPONENTS_TEXTURE + NUM_COMPONENTS_COLOR + NUM_COMPONENTS_NORMAL;
    private static final int MAX_TRIANGLES = 1000;
    private static final int MAX_NUM_VERTICES = MAX_TRIANGLES * 3;
    private Mesh mesh;
    private float[] vertices;
    private int verticesIndex;



    public Tetrad(Game game) {
        this.game = game;
        position = new Vector2(0, 0);


        points = new Array<>();


        this.mesh = new Mesh(false, MAX_NUM_VERTICES, 0,
                new VertexAttribute(VertexAttributes.Usage.Position,           NUM_COMPONENTS_POSITION, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.Normal,        NUM_COMPONENTS_NORMAL, "a_normal"),
                new VertexAttribute(VertexAttributes.Usage.ColorUnpacked,        NUM_COMPONENTS_COLOR, "a_color"),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, NUM_COMPONENTS_TEXTURE,  "a_texCoord0")
        );

        this.vertices = new float[MAX_NUM_VERTICES * NUM_COMPONENTS_PER_VERTEX];

        reset();
    }

    @Override
    public void reset() {
        position.set(0,0);
        buildNewPiece();
        this.verticesIndex = 0;

    }

    public void update(float dt) {
        accum += dt;

        // Allow tetrads to live outside of the gameboard
        if (origin != null) {
            position.set(gameBoard.gameBounds.x + origin.x * POINT_WIDTH, gameBoard.gameBounds.y + origin.y * POINT_WIDTH);
        }

        for (TetradPiece point : points) {
            point.update(dt);

        }
        if (flashing) {
            color.a = (1f + MathUtils.sin(accum * 10f)) / 2f;
        } else {
            color.a = 1;
        }
        buildMesh();
    }

    public void render(SpriteBatch batch) {
        if (flashing) {
            batch.setColor(1, 1, 1, (1f + MathUtils.sin(accum * 10f))/2f);
        } else {
            batch.setColor(Color.WHITE);
        }
        TextureRegion blockImage = game.assets.blockFaces[type];
        for (TetradPiece point : points) {
            if (!point.remove) {
                batch.draw(blockImage, position.x + (POINT_WIDTH * scale * point.x), position.y + (POINT_WIDTH * scale * point.y), POINT_WIDTH * scale, POINT_WIDTH * scale);
            }
        }
        batch.setColor(Color.WHITE);
    }

    private void buildMesh(){
        int downDist = 0;
        float offset = 0;
        boolean isActive = gameBoard.activeTetrad == this;
        if (isActive) {
            offset = .4f;
            while(!gameBoard.invalidMove(this, new Vector2(0, downDist))) {
                downDist --;
            }
            downDist++;
        }

        verticesIndex = 0;
        for (TetradPiece point : points) {
            if (point.remove){
                continue;
            }
            computeFace(origin.x + point.x, origin.y + point.y, offset, color, FACE.TOP, type );
            computeFace(origin.x + point.x, origin.y + point.y, offset, color, FACE.LEFT, type );
            computeFace(origin.x + point.x, origin.y + point.y, offset, color, FACE.RIGHT, type );
            computeFace(origin.x + point.x, origin.y + point.y, offset, color, FACE.FRONT, type );
        }

        if (isActive && gameBoard.gameState.showGhost ) {
            for (TetradPiece point : points) {
                computeGhostFace(origin.x + point.x, downDist + origin.y + point.y, offset, ghostColor, FACE.TOP );
                computeGhostFace(origin.x + point.x, downDist + origin.y + point.y, offset, ghostColor, FACE.LEFT );
                computeGhostFace(origin.x + point.x, downDist + origin.y + point.y, offset, ghostColor, FACE.RIGHT );
                computeGhostFace(origin.x + point.x, downDist + origin.y + point.y, offset, ghostColor, FACE.FRONT );
            }
        }
    }

    Vector3 LL = new Vector3();
    Vector3 UL = new Vector3();
    Vector3 LR = new Vector3();
    Vector3 UR = new Vector3();
    Vector3 NOR = new Vector3();
    Vector2 UV1 = new Vector2();
    Vector2 UV2 = new Vector2();
    private void computeFace(float x, float y, float z, Color color, FACE face, int type) {
        switch (face) {
            case TOP:
                LL.set(x, y, z + blockHeight);
                UL.set(x, y+1, z+ blockHeight);
                LR.set(x+1, y, z+ blockHeight);
                UR.set(x+1, y+1, z+ blockHeight);
                NOR.set(0,0, 1);
                UV1.set((1 + 34f * type)/340f, 0);
                UV2.set((34f * (type+1) - 1)/340f, 33f/68f);
                break;
            case LEFT:
                LL.set(x, y+1, z);
                UL.set(x, y+1, z+blockHeight);
                LR.set(x, y, z);
                UR.set(x, y, z+blockHeight);
                NOR.set(0,-1, 0);
                UV1.set(1/340f, 33/68f);
                UV2.set(33f/340f, 67/68f);
                break;
            case RIGHT:
                LL.set(x+1, y, z);
                UL.set(x+1, y, z+blockHeight);
                LR.set(x+1, y+1, z);
                UR.set(x+1, y+1, z+blockHeight);
                NOR.set(0,1, 0);
                UV1.set(1/340f, 33/68f);
                UV2.set(33f/340f, 67/68f);
                break;

            case FRONT:
                LL.set(x, y, z);
                UL.set(x, y, z+blockHeight);
                LR.set(x +1, y, z);
                UR.set(x +1, y, z+blockHeight);
                NOR.set(1,0, 0);
                UV1.set(1/340f, 33/68f);
                UV2.set(33f/340f, 67/68f);
                break;
        }

        addFaceVerts(color);
    }

    private void computeGhostFace(float x, float y, float z, Color color, FACE face) {
        switch (face) {
            case TOP:
                LL.set(x, y, z + blockHeight);
                UL.set(x, y+1, z+ blockHeight);
                LR.set(x+1, y, z+ blockHeight);
                UR.set(x+1, y+1, z+ blockHeight);
                NOR.set(0,0, 1);

                break;
            case LEFT:
                LL.set(x, y+1, z);
                UL.set(x, y+1, z+blockHeight);
                LR.set(x, y, z);
                UR.set(x, y, z+blockHeight);
                NOR.set(0,-1, 0);
                break;
            case RIGHT:
                LL.set(x+1, y, z);
                UL.set(x+1, y, z+blockHeight);
                LR.set(x+1, y+1, z);
                UR.set(x+1, y+1, z+blockHeight);
                NOR.set(0,1, 0);
                break;

            case FRONT:
                LL.set(x, y, z);
                UL.set(x, y, z+blockHeight);
                LR.set(x + 1, y, z);
                UR.set(x + 1, y, z+blockHeight);
                NOR.set(1,0, 0);
                break;
        }
        UV1.set((1 + 34f)/340f, 35/68f);
        UV2.set((34f * (2) - 1)/340f, 67/68f);

        addFaceVerts(color);
    }

    private void addFaceVerts(Color color ) {
        vertices[verticesIndex++] = LL.x;
        vertices[verticesIndex++] = LL.y;
        vertices[verticesIndex++] = LL.z;
        vertices[verticesIndex++] = NOR.x;
        vertices[verticesIndex++] = NOR.y;
        vertices[verticesIndex++] = NOR.z;
        vertices[verticesIndex++] = color.r;
        vertices[verticesIndex++] = color.g;
        vertices[verticesIndex++] = color.b;
        vertices[verticesIndex++] = color.a;
        vertices[verticesIndex++] = UV1.x;
        vertices[verticesIndex++] = UV2.y;

        vertices[verticesIndex++] = UL.x;
        vertices[verticesIndex++] = UL.y;
        vertices[verticesIndex++] = UL.z;
        vertices[verticesIndex++] = NOR.x;
        vertices[verticesIndex++] = NOR.y;
        vertices[verticesIndex++] = NOR.z;
        vertices[verticesIndex++] = color.r;
        vertices[verticesIndex++] = color.g;
        vertices[verticesIndex++] = color.b;
        vertices[verticesIndex++] = color.a;
        vertices[verticesIndex++] = UV1.x;
        vertices[verticesIndex++] = UV1.y;

        vertices[verticesIndex++] = LR.x;
        vertices[verticesIndex++] = LR.y;
        vertices[verticesIndex++] = LR.z;
        vertices[verticesIndex++] = NOR.x;
        vertices[verticesIndex++] = NOR.y;
        vertices[verticesIndex++] = NOR.z;
        vertices[verticesIndex++] = color.r;
        vertices[verticesIndex++] = color.g;
        vertices[verticesIndex++] = color.b;
        vertices[verticesIndex++] = color.a;
        vertices[verticesIndex++] = UV2.x;
        vertices[verticesIndex++] = UV2.y;

        vertices[verticesIndex++] = LR.x;
        vertices[verticesIndex++] = LR.y;
        vertices[verticesIndex++] = LR.z;
        vertices[verticesIndex++] = NOR.x;
        vertices[verticesIndex++] = NOR.y;
        vertices[verticesIndex++] = NOR.z;
        vertices[verticesIndex++] = color.r;
        vertices[verticesIndex++] = color.g;
        vertices[verticesIndex++] = color.b;
        vertices[verticesIndex++] = color.a;
        vertices[verticesIndex++] = UV2.x;
        vertices[verticesIndex++] = UV2.y;

        vertices[verticesIndex++] = UL.x;
        vertices[verticesIndex++] = UL.y;
        vertices[verticesIndex++] = UL.z;
        vertices[verticesIndex++] = NOR.x;
        vertices[verticesIndex++] = NOR.y;
        vertices[verticesIndex++] = NOR.z;
        vertices[verticesIndex++] = color.r;
        vertices[verticesIndex++] = color.g;
        vertices[verticesIndex++] = color.b;
        vertices[verticesIndex++] = color.a;
        vertices[verticesIndex++] = UV1.x;
        vertices[verticesIndex++] = UV1.y;

        vertices[verticesIndex++] = UR.x;
        vertices[verticesIndex++] = UR.y;
        vertices[verticesIndex++] = UR.z;
        vertices[verticesIndex++] = NOR.x;
        vertices[verticesIndex++] = NOR.y;
        vertices[verticesIndex++] = NOR.z;
        vertices[verticesIndex++] = color.r;
        vertices[verticesIndex++] = color.g;
        vertices[verticesIndex++] = color.b;
        vertices[verticesIndex++] = color.a;
        vertices[verticesIndex++] = UV2.x;
        vertices[verticesIndex++] = UV1.y;
    }

    public void renderModels(Camera camera) {
        ShaderProgram shader = game.assets.blockShader;
        shader.bind();
        shader.setUniformMatrix("u_projTrans", camera.combined);
        mesh.setVertices(vertices);
        mesh.render(shader, GL20.GL_TRIANGLES, 0, verticesIndex/ NUM_COMPONENTS_PER_VERTEX);

    }


    public void rotate(int dir) {
        for (TetradPiece point : points) {
            if (dir < 0) {
                point.set(point.y, bounds - point.x);
            } else {
                point.set(bounds - point.y, point.x);
            }
        }
    }

    public boolean containsPoint(int x, int y) {
        for (TetradPiece point : points) {
            if (point.x + origin.x == x && point.y + origin.y == y) return true;
        }
        return false;
    }

    public TetradPiece getPieceAt(int x, int y) {
        for (TetradPiece point : points) {
            if (point.x + origin.x == x && point.y + origin.y == y) return point;
        }
        return null;
    }

    public boolean resolvingTetrad() {
        for (TetradPiece piece : points) {
            if (piece.destroyTimer != null && piece.destroyTimer > 0) return true;
        }
        return false;
    }

    public void deleteRow(int y) {
        if (y < origin.y) {
            origin.y--;
        } else {
            for (int i = points.size - 1; i >= 0; i--) {
                TetradPiece point = points.get(i);
                if (y == origin.y + point.y) {
                    points.removeIndex(i);
                } else if (y < origin.y + point.y) {
                    point.y -= 1;
                }
            }
        }
    }

    public boolean isEmpty() {
        return points.size == 0;
    }

    public void insertIntoBoard(GameBoard gameBoard) {
        insertIntoBoard(gameBoard, null);
    }

    public void insertIntoBoard(GameBoard gameBoard, Vector2 curOrigin) {
        this.gameBoard = gameBoard;
        if (curOrigin != null) {
            this.origin = curOrigin;
        } else {
            int height = 0;
            for (TetradPiece point : points) {
                if (point.y > height) height = point.y;
            }
            this.origin = new Vector2(4, 19 - height);
        }
    }

    public Vector2 removeFromBoard() {
        Vector2 pos = origin;
        origin = null;
        gameBoard = null;
        return pos;
    }

    public void center(Vector2 center) {
        position.set(center.x - this.center.x * POINT_WIDTH * scale, center.y - this.center.y * POINT_WIDTH * scale);
    }

    private void buildNewPiece() {
        type = MathUtils.random(6);
        switch (type) {
            case 0:
                // I
                points.add(new TetradPiece(0, 2, color));
                points.add(new TetradPiece(1, 2, color));
                points.add(new TetradPiece(2, 2, color));
                points.add(new TetradPiece(3, 2, color));
                center.set(2, 2.5f);
                bounds = 3;
                color = new Color(Color.BLUE);
                break;
            case 1:
                // L
                points.add(new TetradPiece(0, 2, color));
                points.add(new TetradPiece(0, 1, color));
                points.add(new TetradPiece(1, 1, color));
                points.add(new TetradPiece(2, 1, color));
                center.set(1.5f, 2f);
                bounds = 2;
                color = new Color(Color.GREEN);
                break;
            case 2:
                // Other L
                points.add(new TetradPiece(0, 1, color));
                points.add(new TetradPiece(1, 1, color));
                points.add(new TetradPiece(2, 1, color));
                points.add(new TetradPiece(2, 2, color));
                center.set(1.5f, 2f);
                bounds = 2;
                color = new Color(Color.ORANGE);
                break;
            case 3:
                // square
                points.add(new TetradPiece(0, 0, color));
                points.add(new TetradPiece(0, 1, color));
                points.add(new TetradPiece(1, 0, color));
                points.add(new TetradPiece(1, 1, color));
                center.set(1f, 1f);
                bounds = 1;
                color = new Color(Color.RED);
                break;
            case 4:
                // S
                points.add(new TetradPiece(0, 1, color));
                points.add(new TetradPiece(1, 1, color));
                points.add(new TetradPiece(1, 2, color));
                points.add(new TetradPiece(2, 2, color));
                center.set(1.5f, 2f);
                bounds = 2;
                color = new Color(Color.RED);
                break;
            case 5:
                // other S
                points.add(new TetradPiece(0, 2, color));
                points.add(new TetradPiece(1, 2, color));
                points.add(new TetradPiece(1, 1, color));
                points.add(new TetradPiece(2, 1, color));
                center.set(1.5f, 2f);
                bounds = 2;
                color = new Color(Color.BLUE);
                break;
            case 6:
                // T
                points.add(new TetradPiece(0, 1, color));
                points.add(new TetradPiece(1, 1, color));
                points.add(new TetradPiece(1, 2, color));
                points.add(new TetradPiece(2, 1, color));
                center.set(1.5f, 2f);
                bounds = 2;
                color = new Color(Color.RED);
                break;
        }
        // I

    }

}
