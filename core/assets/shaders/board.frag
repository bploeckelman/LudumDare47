
#ifdef GL_ES
precision highp float;
#endif

struct PointLight {
    vec3 position;
    float falloff;
    vec4 color;
};
#define NR_POINT_LIGHTS 5
uniform PointLight u_pointLights[NR_POINT_LIGHTS];

uniform sampler2D u_texture;
uniform vec4 u_ambient;
uniform vec4 u_direction_color;
uniform vec3 u_direction_dir;
uniform float u_time;
uniform vec3 u_viewPos;

//input from vertex shader
varying vec2 v_texCoords;
varying vec4 v_color;
varying vec3 v_normal;
varying vec3 v_pos;

void main() {
    float specularStrength = .5;


    float divisions = 10.0;
    float thickness = 0.06;
    float delta = 0.1 / 2.0;
    float x = fract(v_pos.x);
    x = min(x, 1.0 - x);
    float xdelta = delta;
    x = smoothstep(x - xdelta, x + xdelta, thickness);
    float y = fract(v_pos.y);
    y = min(y, 1.0 - y);
    float ydelta = delta;
    y = smoothstep(y - ydelta, y + ydelta, thickness);
    float grid =clamp(x + y, 0.0, 1.0);


    vec3 n = normalize (v_normal);

    float directionIntensity = max(dot(n, u_direction_dir), 0.0);

    vec4 pointLightColor = vec4(0);
    for (int i = 0; i < NR_POINT_LIGHTS; i++) {
        PointLight light = u_pointLights[i];
        vec3 lightDir = normalize(light.position - v_pos);
        float diff = max(dot(n, lightDir), 0.0);

        vec3 viewDir = normalize(u_viewPos - v_pos);
        vec3 reflectDir = normalize(reflect(-lightDir, n));
        float spec = pow(max(dot(viewDir, reflectDir), 0.0), 16.) * specularStrength;

        float dist = length(light.position - v_pos);
        float atten = 5.0 / (light.falloff * (dist));

        pointLightColor.rgb = max(pointLightColor.rgb, light.color.rgb * (diff * atten + spec));
    }

    vec4 diffuseColor = max((directionIntensity * u_direction_color) + pointLightColor, u_ambient);

    vec4 baseDif = vec4(1., 1., 1., .8);

    vec4 lineDif = vec4(1., 0., 1., 1.);
    lineDif.a *= 1. - (v_pos.y / 60.);

    vec4 combineColor = mix(baseDif, lineDif, grid);

    vec4 finalColor = combineColor * diffuseColor;
    gl_FragColor = finalColor;

}