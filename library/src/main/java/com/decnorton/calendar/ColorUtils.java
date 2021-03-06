package com.decnorton.calendar;

import android.content.res.Resources;
import android.graphics.Color;

/**
 * Created by decnorton on 15/04/2014.
 */
public class ColorUtils {

    public static int getColor(Resources res, int resId) {
        return res.getColor(resId);
    }

    public static int darken(int color, float scale) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= scale; // value component
        return Color.HSVToColor(hsv);
    }
}
