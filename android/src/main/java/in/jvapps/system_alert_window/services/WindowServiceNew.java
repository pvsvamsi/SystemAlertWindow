package in.jvapps.system_alert_window.services;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.content.pm.ServiceInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.view.ViewCompat;

import java.util.HashMap;
import java.util.Objects;

import in.jvapps.system_alert_window.R;
import in.jvapps.system_alert_window.SystemAlertWindowPlugin;
import in.jvapps.system_alert_window.utils.Commons;
import in.jvapps.system_alert_window.utils.Constants;
import in.jvapps.system_alert_window.utils.ContextHolder;
import in.jvapps.system_alert_window.utils.LogUtils;
import in.jvapps.system_alert_window.utils.NumberUtils;
import io.flutter.embedding.android.FlutterTextureView;
import io.flutter.embedding.android.FlutterView;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.JSONMessageCodec;
import io.flutter.plugin.common.MethodChannel;

public class WindowServiceNew extends Service implements View.OnTouchListener {

    private static final String TAG = WindowServiceNew.class.getSimpleName();
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    public static final String INTENT_EXTRA_IS_UPDATE_WINDOW = "IsUpdateWindow";
    public static final String INTENT_EXTRA_IS_CLOSE_WINDOW = "IsCloseWindow";


    private WindowManager windowManager;

    private String windowGravity;
    private int windowWidth;
    private int windowHeight;

    private boolean isDisableClicks = false;
    private boolean isFlagFocusable = false;

    private FlutterView flutterView;

    private float offsetY;
    private boolean moving;
    @SuppressLint("UnspecifiedImmutableFlag")
    @Override
    public void onCreate() {
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, SystemAlertWindowPlugin.class);
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Overlay window service is running")
                .setSmallIcon(R.drawable.ic_desktop_windows_black_24dp)
                .setContentIntent(pendingIntent)
                .build();
        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(NOTIFICATION_ID, notification, FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        if (null != intent && intent.getExtras() != null) {
            ContextHolder.setApplicationContext(this);
            @SuppressWarnings("unchecked")
            HashMap<String, Object> paramsMap = (HashMap<String, Object>) intent.getSerializableExtra(Constants.INTENT_EXTRA_PARAMS_MAP);
            boolean isCloseWindow = intent.getBooleanExtra(INTENT_EXTRA_IS_CLOSE_WINDOW, false);
            if (!isCloseWindow) {
                assert paramsMap != null;
                boolean isUpdateWindow = intent.getBooleanExtra(INTENT_EXTRA_IS_UPDATE_WINDOW, false);
                if (isUpdateWindow && windowManager != null && flutterView != null) {
                    if (ViewCompat.isAttachedToWindow(flutterView)) {
                         updateWindow(paramsMap);
                    } else {
                        createWindow(paramsMap);
                    }
                } else {
                    createWindow(paramsMap);
                }
            } else {
                closeWindow(true);
            }
        }
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void setWindowManager() {
        if (windowManager == null) {
            windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        }
    }

    private void setWindowLayoutFromMap(HashMap<String, Object> paramsMap) {
        isDisableClicks = Commons.getIsClicksDisabled(paramsMap);
        isFlagFocusable = Commons.getIsFlagFocusable(paramsMap);
        LogUtils.getInstance().i(TAG, String.valueOf(isDisableClicks));
        windowGravity = (String) paramsMap.get(Constants.KEY_GRAVITY);
        windowWidth = NumberUtils.getInt(paramsMap.get(Constants.KEY_WIDTH));
        windowHeight = NumberUtils.getInt(paramsMap.get(Constants.KEY_HEIGHT));
    }

    private WindowManager.LayoutParams getLayoutParams() {
        final WindowManager.LayoutParams params;
        params = new WindowManager.LayoutParams();
        params.width = (windowWidth == 0) ? android.view.WindowManager.LayoutParams.MATCH_PARENT : Commons.getPixelsFromDp(this, windowWidth);
        params.height = (windowHeight == 0) ? android.view.WindowManager.LayoutParams.WRAP_CONTENT : Commons.getPixelsFromDp(this, windowHeight);
        params.format = PixelFormat.TRANSLUCENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            if (isDisableClicks) {
                params.flags = WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED | android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
            } else {
                params.flags =  WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED | android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED ;
            }
        } else {
            params.type = android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            if (isDisableClicks) {
                params.flags =  WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED | android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            } else {
                params.flags = WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED | android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            }
        }
        if(!isFlagFocusable)  params.flags|= android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && isDisableClicks) {
            params.alpha = 0.8f;
        }
        params.gravity = Commons.getGravity(windowGravity, Gravity.TOP);
        return params;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createWindow(HashMap<String, Object> paramsMap) {
        closeWindow(false);
        setWindowManager();
        setWindowLayoutFromMap(paramsMap);
        WindowManager.LayoutParams params = getLayoutParams();
        FlutterEngine engine = FlutterEngineCache.getInstance().get(Constants.FLUTTER_CACHE_ENGINE);
        assert engine != null;
        engine.getLifecycleChannel().appIsResumed();
        flutterView = new FlutterView(getApplicationContext(), new FlutterTextureView(getApplicationContext()));
        flutterView.attachToFlutterEngine(Objects.requireNonNull(FlutterEngineCache.getInstance().get(Constants.FLUTTER_CACHE_ENGINE)));
        flutterView.setFitsSystemWindows(true);
        flutterView.setFocusable(true);
        flutterView.setFocusableInTouchMode(true);
        flutterView.setBackgroundColor(Color.TRANSPARENT);
        flutterView.setOnTouchListener(this);
        try {
            windowManager.addView(flutterView, params);
        } catch (Exception ex) {
            LogUtils.getInstance().e(TAG, ex.toString());
            retryCreateWindow(paramsMap);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void retryCreateWindow(HashMap<String, Object> paramsMap) {
        try {
            LogUtils.getInstance().d(TAG, "Retrying create window");
            closeWindow(false);
            setWindowManager();
            setWindowLayoutFromMap(paramsMap);
            WindowManager.LayoutParams params = getLayoutParams();
            FlutterEngine engine = FlutterEngineCache.getInstance().get(Constants.FLUTTER_CACHE_ENGINE);
            assert engine != null;
            engine.getLifecycleChannel().appIsResumed();
            flutterView = new FlutterView(getApplicationContext(), new FlutterTextureView(getApplicationContext()));
            flutterView.attachToFlutterEngine(Objects.requireNonNull(FlutterEngineCache.getInstance().get(Constants.FLUTTER_CACHE_ENGINE)));
            flutterView.setFitsSystemWindows(true);
            flutterView.setFocusable(true);
            flutterView.setFocusableInTouchMode(true);
            flutterView.setBackgroundColor(Color.TRANSPARENT);
            flutterView.setOnTouchListener(this);
            windowManager.addView(flutterView, params);
        } catch (Exception ex) {
            LogUtils.getInstance().e(TAG, ex.toString());
        }
    }

    private void updateWindow(HashMap<String, Object> paramsMap) {
        setWindowLayoutFromMap(paramsMap);
        WindowManager.LayoutParams newParams = getLayoutParams();
        WindowManager.LayoutParams previousParams = (WindowManager.LayoutParams) flutterView.getLayoutParams();
        previousParams.width = (windowWidth == 0) ? android.view.WindowManager.LayoutParams.MATCH_PARENT : Commons.getPixelsFromDp(this, windowWidth);
        previousParams.height = (windowHeight == 0) ? android.view.WindowManager.LayoutParams.WRAP_CONTENT : Commons.getPixelsFromDp(this, windowHeight);
        previousParams.flags = newParams.flags;
        previousParams.alpha = newParams.alpha;
        windowManager.updateViewLayout(flutterView, previousParams);
    }

    private void closeWindow(boolean isStopService) {
        LogUtils.getInstance().i(TAG, "Closing the overlay window");
        try {
            if (windowManager != null) {
               if(flutterView!=null){
                   windowManager.removeView(flutterView);
                   windowManager = null;
                   flutterView.detachFromFlutterEngine();
               }
            }
        } catch (IllegalArgumentException e) {
            LogUtils.getInstance().e(TAG, "view not found");
        }
        if (isStopService) {
            stopSelf();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View v, MotionEvent event) {
        if (windowManager != null) {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) flutterView.getLayoutParams();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    moving = false;
                    offsetY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dy = event.getRawY() - offsetY;
                    if (!moving && Math.abs(dy) > 25) {
                        moving = true;
                    }
                    if (moving) {
                        offsetY = event.getRawY();
                        int yy = params.y + (int) dy;
                        params.y = yy;
                        windowManager.updateViewLayout(flutterView, params);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                default:
                    return false;
            }
            return false;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        closeWindow(false);
        LogUtils.getInstance().d(TAG, "Destroying the overlay window service");
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
