package in.jvapps.system_alert_window.models;

import android.content.Context;

import in.jvapps.system_alert_window.utils.Commons;
import in.jvapps.system_alert_window.utils.NumberUtils;

public class Decoration {
    private int backgroundColor;
    private int borderWidth;
    private float borderRadius;
    private int borderColor;

    public int getBackgroundColor() {
        return backgroundColor;
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

    public Decoration(Object bgColor, Object borderWidth, Object borderRadius, Object borderColor, Context context) {
        this.backgroundColor = NumberUtils.getInt(bgColor);
        this.borderWidth = Commons.getPixelsFromDp(context, NumberUtils.getInt(borderWidth));
        this.borderRadius = Commons.getPixelsFromDp(context, NumberUtils.getFloat(borderRadius));
        this.borderColor = NumberUtils.getInt(borderColor);
    }
}
