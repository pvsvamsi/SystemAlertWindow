package in.jvapps.system_alert_window.utils;

import static android.content.Context.ACTIVITY_SERVICE;
import static in.jvapps.system_alert_window.utils.Constants.KEY_LAYOUT_PARAMS_FLAG;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;

public class Commons {

    public static boolean isClickDisabled = false;

    public static int getLayoutParamFlags(@NonNull Map<String, Object> paramsMap) {
        Object flagsObj = paramsMap.get(KEY_LAYOUT_PARAMS_FLAG);
        if (flagsObj == null) return 0;
        List<?> flagsList = (List<?>) flagsObj;
        int flags = 0;
        if (flagsList.contains("FLAG_NOT_FOCUSABLE"))
            flags |= android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        if (flagsList.contains("FLAG_NOT_TOUCHABLE")) {
            flags |= android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            isClickDisabled = true;
        } else {
            isClickDisabled = false;
        }
        if (flagsList.contains("FLAG_NOT_TOUCH_MODAL"))
            flags |= android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        return flags;
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
                    gravity = Gravity.START;
                    break;
                case "trailing":
                    gravity = Gravity.END;
                    break;
            }
        }
        return gravity;
    }


    public static boolean isForceAndroidBubble(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            if (activityManager != null) {
                PackageManager pm = context.getPackageManager();
                return !pm.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE) || pm.hasSystemFeature(PackageManager.FEATURE_RAM_LOW) || activityManager.isLowRamDevice();
            } else {
                LogUtils.getInstance().i("SAW:Commons", "Marking force android bubble as false");
            }
        }
        return false;
    }
}
