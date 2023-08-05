package in.jvapps.system_alert_window.utils;

import android.content.Context;

public class ContextHolder {
    private static Context applicationContext;

    public static Context getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(Context context) {
        if (applicationContext == null) {
            applicationContext = context;
            LogUtils.getInstance().d("SAW:ContextHolder", "received application context");
        }
    }
}