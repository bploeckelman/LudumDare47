
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
uniform vec3 u_viewPos;

//input from vertex shader
varying vec2 v_texCoords;
varying vec4 v_color;
varying vec3 v_normal;
varying vec3 v_pos;


void main() {
    float specularStrength = 1.;

    vec4 texSample = texture2D(u_texture, v_texCoords);

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

    vec4 finalColor = texSample * max((directionIntensity * u_direction_color) + pointLightColor, u_ambient);
//    finalColor.rgb = normalize(u_viewPos);

    finalColor.a *= v_color.a;
    gl_FragColor = finalColor;
}