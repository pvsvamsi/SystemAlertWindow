package in.jvapps.system_alert_window.models;

import android.content.Context;

import in.jvapps.system_alert_window.utils.Commons;
import in.jvapps.system_alert_window.utils.NumberUtils;

public class Decoration {
    private int startColor;
    private int endColor;
    private int borderWidth;
    private float borderRadius;
    private int borderColor;

    private boolean isGradient;

    public int getStartColor() {
        return startColor;
    }

    public int getEndColor() {
        return endColor;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public float getBorderRadius() {
        return borderRadius;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public Decoration(Object startColor, Object endColor, Object borderWidth, Object borderRadius, Object borderColor, Context context) {
        this.startColor = NumberUtils.getInt(startColor);
        if (endColor != null) {
            this.endColor = NumberUtils.getInt(endColor);
            isGradient = true;
        } else {
            isGradient = false;
        }
        this.borderWidth = Commons.getPixelsFromDp(context, NumberUtils.getInt(borderWidth));
        this.borderRadius = Commons.getPixelsFromDp(context, NumberUtils.getFloat(borderRadius));
        this.borderColor = NumberUtils.getInt(borderColor);
    }

    public boolean isGradient() {
        return isGradient;
    }
}
