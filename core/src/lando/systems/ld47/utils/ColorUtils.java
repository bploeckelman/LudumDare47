package lando.systems.ld47.utils;

import com.badlogic.gdx.graphics.Color;

public class ColorUtils {

    private static HSV tempHSV = new HSV();

    public static class HSV {
        public float h = 0f; // hue: [0,360]
        public float s = 0f; // saturation: [0,1]
        public float v = 0f; // value: [0,1]
        public HSV() {}
        public HSV(float h, float s, float v) {
            this.h = h;
            this.s = s;
            this.v = v;
        }
    }

    public static Color lerpHSV(Color outColor, HSV from, HSV to, float interp) {
        tempHSV.h = from.h + interp * (to.h - from.h);
        tempHSV.s = from.s + interp * (to.s - from.s);
        tempHSV.v = from.v + interp * (to.v - from.v);
        return outColor.fromHsv(tempHSV.h, tempHSV.s, tempHSV.v);
    }

}
