package in.jvapps.system_alert_window.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityManagerCompat;

import java.util.List;
import java.util.Map;

import in.jvapps.system_alert_window.models.Margin;

import static android.content.Context.ACTIVITY_SERVICE;
import static in.jvapps.system_alert_window.utils.Constants.KEY_MARGIN;

public class Commons {

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMapFromObject(@NonNull Map<String, Object> map, String key) {
        return (Map<String, Object>) map.get(key);
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> getMapListFromObject(@NonNull Map<String, Object> map, String key) {
        return (List<Map<String, Object>>) map.get(key);
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

    public static float getPixelsFromDp(@NonNull Context context, float dp) {
        if (dp == -1) return -1;
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
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
                    gravity = Gravity.START;
                    break;
                case "trailing":
                    gravity = Gravity.END;
                    break;
            }
        }
        return gravity;
    }

    public static int getFontWeight(@Nullable String fontWeightStr, int defVal) {
        int fontWeight = defVal;
        if (fontWeightStr != null) {
            switch (fontWeightStr) {
                case "normal":
                default:
                    fontWeight = Typeface.NORMAL;
                    break;
                case "bold":
                    fontWeight = Typeface.BOLD;
                    break;
                case "italic":
                    fontWeight = Typeface.ITALIC;
                    break;
                case "bold_italic":
                    fontWeight = Typeface.BOLD_ITALIC;
                    break;
            }
        }
        return fontWeight;
    }

    public static void setMargin(Context context, LinearLayout.LayoutParams params, Map<String, Object> map) {
        Margin margin = UiBuilder.getMargin(context, map.get(KEY_MARGIN));
        params.setMargins(margin.getLeft(), margin.getTop(), margin.getRight(), margin.getBottom());
    }

    public static boolean isForceAndroidBubble(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            if (activityManager != null) {
                PackageManager pm = context.getPackageManager();
                return !pm.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE) || pm.hasSystemFeature(PackageManager.FEATURE_RAM_LOW) || activityManager.isLowRamDevice();
            } else {
                Log.i("Commons", "Marking force android bubble as false");
            }
        }
        return false;
    }
}
