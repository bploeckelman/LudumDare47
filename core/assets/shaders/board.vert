attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;

varying vec2 v_texCoords;
varying vec3 v_normal;
varying vec3 v_pos;

void main()
{
    v_pos = a_position.xyz;
    v_normal = a_normal;
    v_texCoords = a_texCoord0;
    gl_Position =  u_projTrans * vec4(a_position, 1.);
}