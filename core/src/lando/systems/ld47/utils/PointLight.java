package lando.systems.ld47.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

public class PointLight {

    private int index;
    public Vector3 pos;
    public Color color;
    public float falloff;
    String uniformPos;
    String uniformFalloff;
    String uniformColor;

    public PointLight (int index, Vector3 pos, Color color) {
        this.pos = new Vector3(pos);
        this.color = new Color(color);
        falloff = .5f;
        this.index = index;
        uniformPos = "u_pointLights["+index+"].position";
        uniformFalloff = "u_pointLights["+index+"].falloff";
        uniformColor = "u_pointLights["+index+"].color";
    }

    public void update(float dt) {

    }

    public void addToShader(ShaderProgram shader) {
        shader.setUniformf(uniformPos, pos);
        shader.setUniformf(uniformFalloff, falloff);
        shader.setUniformf(uniformColor, color);
    }



}
