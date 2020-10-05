package lando.systems.ld47.entities;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class GameBackPlate {

    // Mesh things
    private static final int NUM_COMPONENTS_POSITION = 3;
    private static final int NUM_COMPONENTS_NORMAL = 3;
    private static final int NUM_COMPONENTS_TEXTURE = 2;
    private static final int NUM_COMPONENTS_PER_VERTEX = NUM_COMPONENTS_POSITION + NUM_COMPONENTS_TEXTURE + NUM_COMPONENTS_NORMAL;
    private static final int MAX_TRIANGLES = 1000;
    private static final int MAX_NUM_VERTICES = MAX_TRIANGLES * 3;
    private Mesh mesh;
    private float[] vertices;
    private int verticesIndex;

    private float accum = 0;

    public GameBackPlate () {
        this.mesh = new Mesh(true, MAX_NUM_VERTICES, 0,
                new VertexAttribute(VertexAttributes.Usage.Position,           NUM_COMPONENTS_POSITION, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.Normal,        NUM_COMPONENTS_NORMAL, "a_normal"),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, NUM_COMPONENTS_TEXTURE,  "a_texCoord0")
        );

        this.vertices = new float[MAX_NUM_VERTICES * NUM_COMPONENTS_PER_VERTEX];
        verticesIndex = 0;
        buildMesh();
    }

    public void buildMesh() {
        // BL
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = 0;

        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = 1;

        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = 1;

        // UL
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = GameBoard.TILESHIGH;
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = 1;
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = 0;

        // BR
        vertices[verticesIndex++] = GameBoard.TILESWIDE;
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = 1;
        vertices[verticesIndex++] = 1;
        vertices[verticesIndex++] = 1;

        //BR
        vertices[verticesIndex++] = GameBoard.TILESWIDE;
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = 1;
        vertices[verticesIndex++] = 1;
        vertices[verticesIndex++] = 1;

        // UL
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = GameBoard.TILESHIGH;
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = 1;
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = 0;

        // UR
        vertices[verticesIndex++] = GameBoard.TILESWIDE;
        vertices[verticesIndex++] = GameBoard.TILESHIGH;
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = 0;
        vertices[verticesIndex++] = 1;
        vertices[verticesIndex++] = 1;
        vertices[verticesIndex++] = 0;
    }

    public void update(float dt) {
        accum += dt;
    }

    public void renderMesh(ShaderProgram shader){
        shader.setUniformf("u_time", accum);
        mesh.setVertices(vertices);
        mesh.render(shader, GL20.GL_TRIANGLES, 0, verticesIndex/ NUM_COMPONENTS_PER_VERTEX);
    }

}
