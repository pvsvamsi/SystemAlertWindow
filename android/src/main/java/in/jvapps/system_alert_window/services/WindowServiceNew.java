package in.jvapps.system_alert_window.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.HashMap;
import java.util.Map;

import in.jvapps.system_alert_window.R;
import in.jvapps.system_alert_window.SystemAlertWindowPlugin;
import in.jvapps.system_alert_window.models.Margin;
import in.jvapps.system_alert_window.utils.Commons;
import in.jvapps.system_alert_window.utils.NumberUtils;
import in.jvapps.system_alert_window.utils.UiBuilder;
import in.jvapps.system_alert_window.views.BodyView;
import in.jvapps.system_alert_window.views.FooterView;
import in.jvapps.system_alert_window.views.HeaderView;

import static in.jvapps.system_alert_window.utils.Constants.INTENT_EXTRA_PARAMS_MAP;
import static in.jvapps.system_alert_window.utils.Constants.KEY_BODY;
import static in.jvapps.system_alert_window.utils.Constants.KEY_FOOTER;
import static in.jvapps.system_alert_window.utils.Constants.KEY_GRAVITY;
import static in.jvapps.system_alert_window.utils.Constants.KEY_HEADER;
import static in.jvapps.system_alert_window.utils.Constants.KEY_HEIGHT;
import static in.jvapps.system_alert_window.utils.Constants.KEY_MARGIN;
import static in.jvapps.system_alert_window.utils.Constants.KEY_WIDTH;

public class WindowServiceNew extends Service implements View.OnTouchListener {

    private static final String TAG = WindowServiceNew.class.getSimpleName();
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static int NOTIFICATION_ID = 1;
    public static final String INTENT_EXTRA_IS_UPDATE_WINDOW = "IsUpdateWindow";
    public static final String INTENT_EXTRA_IS_CLOSE_WINDOW = "IsCloseWindow";

    private WindowManager wm;

    private String windowGravity;
    private int windowWidth;
    private int windowHeight;
    private Margin windowMargin;

    private LinearLayout windowView;
    private LinearLayout headerView;
    private LinearLayout bodyView;
    private LinearLayout footerView;

    private float offsetX;
    private float offsetY;
    private int originalXPos;
    private int originalYPos;
    private boolean moving;

    private Context mContext;

    @Override
    public void onCreate() {
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, SystemAlertWindowPlugin.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Overlay window service is running")
                .setSmallIcon(R.drawable.ic_desktop_windows_black_24dp)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        if (null != intent && intent.getExtras() != null) {
            @SuppressWarnings("unchecked")
            HashMap<String, Object> paramsMap = (HashMap<String, Object>) intent.getSerializableExtra(INTENT_EXTRA_PARAMS_MAP);
            mContext = this;
            boolean isCloseWindow = intent.getBooleanExtra(INTENT_EXTRA_IS_CLOSE_WINDOW, false);
            if (!isCloseWindow) {
                assert paramsMap != null;
                boolean isUpdateWindow = intent.getBooleanExtra(INTENT_EXTRA_IS_UPDATE_WINDOW, false);
                if (isUpdateWindow && windowView != null) {
                    updateWindow(paramsMap);
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
        if (wm == null) {
            wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        }
    }

    private void setWindowLayoutFromMap(HashMap<String, Object> paramsMap) {
        Map<String, Object> headersMap = Commons.getMapFromObject(paramsMap, KEY_HEADER);
        Map<String, Object> bodyMap = Commons.getMapFromObject(paramsMap, KEY_BODY);
        Map<String, Object> footerMap = Commons.getMapFromObject(paramsMap, KEY_FOOTER);
        windowMargin = UiBuilder.getMargin(mContext, paramsMap.get(KEY_MARGIN));
        windowGravity = (String) paramsMap.get(KEY_GRAVITY);
        windowWidth = NumberUtils.getInt(paramsMap.get(KEY_WIDTH));
        windowHeight = NumberUtils.getInt(paramsMap.get(KEY_HEIGHT));
        headerView = new HeaderView(mContext, headersMap).getView();
        if (bodyMap != null)
            bodyView = new BodyView(mContext, bodyMap).getView();
        if (footerMap != null)
            footerView = new FooterView(mContext, footerMap).getView();
    }

    private WindowManager.LayoutParams getLayoutParams() {
        final WindowManager.LayoutParams params;
        params = new WindowManager.LayoutParams();
        params.width = (windowWidth == 0) ? android.view.WindowManager.LayoutParams.MATCH_PARENT : Commons.getPixelsFromDp(mContext, windowWidth);
        params.height = (windowHeight == 0) ? android.view.WindowManager.LayoutParams.WRAP_CONTENT : Commons.getPixelsFromDp(mContext, windowHeight);
        params.format = PixelFormat.TRANSLUCENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            params.flags = android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        } else {
            params.type = android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT | android.view.WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
            params.flags = android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }
        params.gravity = Commons.getGravity(windowGravity, Gravity.TOP);
        int marginTop = windowMargin.getTop();
        int marginBottom = windowMargin.getBottom();
        int marginLeft = windowMargin.getLeft();
        int marginRight = windowMargin.getRight();
        params.x = Math.max(marginLeft, marginRight);
        params.y = (params.gravity == Gravity.TOP) ? marginTop :
                (params.gravity == Gravity.BOTTOM) ? marginBottom : Math.max(marginTop, marginBottom);
        return params;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setWindowView(WindowManager.LayoutParams params, boolean isCreate) {
        boolean isEnableDraggable = true;//params.width == WindowManager.LayoutParams.MATCH_PARENT;
        if (isCreate) {
            windowView = new LinearLayout(mContext);
        }
        windowView.setOrientation(LinearLayout.VERTICAL);
        windowView.setBackgroundColor(Color.WHITE);
        windowView.setLayoutParams(params);
        windowView.removeAllViews();
        windowView.addView(headerView);
        if (bodyView != null)
            windowView.addView(bodyView);
        if (footerView != null)
            windowView.addView(footerView);
        if (isEnableDraggable)
            windowView.setOnTouchListener(this);
    }

    private void createWindow(HashMap<String, Object> paramsMap) {
        closeWindow(false);
        setWindowManager();
        setWindowLayoutFromMap(paramsMap);
        WindowManager.LayoutParams params = getLayoutParams();
        setWindowView(params, true);
        wm.addView(windowView, params);
    }

    private void updateWindow(HashMap<String, Object> paramsMap) {
        setWindowLayoutFromMap(paramsMap);
        WindowManager.LayoutParams params = (WindowManager.LayoutParams) windowView.getLayoutParams();
        params.width = (windowWidth == 0) ? android.view.WindowManager.LayoutParams.MATCH_PARENT : Commons.getPixelsFromDp(mContext, windowWidth);
        params.height = (windowHeight == 0) ? android.view.WindowManager.LayoutParams.WRAP_CONTENT : Commons.getPixelsFromDp(mContext, windowHeight);
        setWindowView(params, false);
        wm.updateViewLayout(windowView, params);
    }

    private void closeWindow(boolean isEverythingDone) {
        Log.i(TAG, "Closing the overlay window");
        try {
            if (wm != null) {
                if (windowView != null) {
                    wm.removeView(windowView);
                    windowView = null;
                }
            }
            wm = null;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "view not found");
        }
        if(isEverythingDone){
            stopSelf();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (null != wm) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float x = event.getRawX();
                float y = event.getRawY();
                moving = false;
                int[] location = new int[2];
                windowView.getLocationOnScreen(location);
                originalXPos = location[0];
                originalYPos = location[1];
                offsetX = originalXPos - x;
                offsetY = originalYPos - y;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                float x = event.getRawX();
                float y = event.getRawY();
                WindowManager.LayoutParams params = (WindowManager.LayoutParams) windowView.getLayoutParams();
                int newX = (int) (offsetX + x);
                int newY = (int) (offsetY + y);
                if (Math.abs(newX - originalXPos) < 1 && Math.abs(newY - originalYPos) < 1 && !moving) {
                    return false;
                }
                params.x = newX;
                params.y = newY;
                wm.updateViewLayout(windowView, params);
                moving = true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                return moving;
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Destroying the overlay window service");
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
