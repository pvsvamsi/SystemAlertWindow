package in.jvapps.system_alert_window.utils;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

public class Commons {

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMapFromObject(@NonNull Map<String, Object> map, String key) {
        return (Map<String, Object>) map.get(key);
    }

    public static float getSpFromPixels(@NonNull Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }

    public static int getPixelsFromDp(@NonNull Context context, int dp) {
        if (dp == -1) return -1;
        return (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()));
    }

    public static int getGravity(@Nullable String gravityStr, int defVal) {
        int gravity = defVal;
        if (gravityStr != null) {
            switch (gravityStr) {
                case "top":
                    gravity = Gravity.TOP;
                    break;
                case "center":
                    gravity = Gravity.CENTER;
                    break;
                case "bottom":
                    gravity = Gravity.BOTTOM;
                    break;
                case "leading":
                    gravity = Gravity.LEFT;
                    break;
                case "trailing":
                    gravity = Gravity.RIGHT;
                    break;
            }
        }
        return gravity;
    }
}
