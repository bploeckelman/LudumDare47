
#ifdef GL_ES
precision highp float;
#endif

uniform sampler2D u_texture;

//input from vertex shader
varying vec2 v_texCoords;
varying vec4 v_color;

void main() {
    gl_FragColor = v_color;
}