package org.oscim.renderer.light;

import org.oscim.backend.canvas.Color;

public class Fog {

    //    public static final int FOG_COLOR = Color.get(0.95, 0.95, 1.0);
    public static final int FOG_COLOR = Color.get(200, 77, 125, 193);
    public static final float FOG_DENSITY = 3.5f;
    public static final float FOG_GRADIENT = 4.0f;
    public static final float FOG_SHIFT = 0.6f;

    private int mColor;
    private float mDensity;
    private float mGradient;
    private float mShift;

    public Fog() {
        mColor = FOG_COLOR;
        mDensity = FOG_DENSITY;
        mGradient = FOG_GRADIENT;
        mShift = FOG_SHIFT;
    }

    public Fog(int fogColor, float fogDensity, float fogGradient, float fogShift) {
        mColor = fogColor;
        mDensity = fogDensity;
        mGradient = fogGradient;
        mShift = fogShift;
    }

    public int getColor() {
        return mColor;
    }

    public float getDensity() {
        return mDensity;
    }

    public float getGradient() {
        return mGradient;
    }

    public float getShift() {
        return mShift;
    }

    static class Shader {
        /**
         * The sFog color as uniform.
         */
        int uColor;

        /**
         * The sFog density as uniform.
         */
        int uDensity;

        /**
         * The sFog gradient as uniform.
         */
        int uGradient;

        /**
         * The sFog shift as uniform.
         */
        int uShift;
    }
}
