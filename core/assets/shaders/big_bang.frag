#ifdef GL_ES
precision highp float;
#endif

uniform sampler2D u_texture;
uniform float iTime;
uniform vec2 iResolution;

//input from vertex shader
varying vec2 v_texCoords;

void main() {
    vec3 c;
    float t = iTime*.1,i,d;
    vec4 o;
    for(i=0.; i<1.; i+=.06)
    {
        d = fract(i+3.*t);
        o = vec4( (v_texCoords-iResolution.xy*.5)/iResolution.y*(1.-d) ,-i,0)*28.;
        for (int i=0 ; i++ <27;) o.xzyw = abs( o/dot(o,o) - vec4( 1.-.03*sin(t) , .9 , .1 , .15 -.14*cos(t*1.3)) );
        c+= o.xyz*o.yzw*(d-d*d);
    }
    o.rgb = c*vec3(.3,.2,1) * .8;
    o.a = 1.;

    gl_FragColor = o;

}