attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;

varying vec2 v_texCoords;
varying vec4 v_color;
varying vec3 v_normal;

void main()
{
    v_normal = a_normal;
    v_color = a_color;
    v_texCoords = a_texCoord0;
    gl_Position =  u_projTrans * vec4(a_position, 1.);
}