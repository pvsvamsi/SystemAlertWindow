package in.jvapps.system_alert_window.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.view.ViewCompat;

import java.util.HashMap;
import java.util.Map;

import in.jvapps.system_alert_window.models.Margin;
import in.jvapps.system_alert_window.utils.Commons;
import in.jvapps.system_alert_window.utils.NumberUtils;
import in.jvapps.system_alert_window.utils.UiBuilder;
import in.jvapps.system_alert_window.views.BodyView;
import in.jvapps.system_alert_window.views.FooterView;
import in.jvapps.system_alert_window.views.HeaderView;

import static in.jvapps.system_alert_window.utils.Constants.*;


public class WindowService extends JobIntentService implements View.OnTouchListener {

    private static final String TAG = "WindowService";
    public static final int JOB_ID = 1;
    private static final String INTENT_EXTRA_IS_UPDATE_WINDOW = "IsUpdateWindow";
    private static final String INTENT_EXTRA_IS_CLOSE_WINDOW = "IsCloseWindow";

    private static WindowManager wm;
    @SuppressLint("StaticFieldLeak")
    private static LinearLayout windowView;
    private Handler oServiceHandler;

    private String windowGravity;
    private int windowWidth;
    private int windowHeight;
    private Margin windowMargin;

    private LinearLayout headerView;
    private LinearLayout bodyView;
    private LinearLayout footerView;

    private Context mContext;

    private float offsetX;
    private float offsetY;
    private int originalXPos;
    private int originalYPos;
    private boolean moving;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Creating Window Service");
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        oServiceHandler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //startTheServiceProcess(intent);
        Log.d(TAG, "onStartCommand");
        oServiceHandler = new Handler();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onStopCurrentWork() {
        closeOverlayService();
        return super.onStopCurrentWork();
    }

    public static void enqueueWork(Context context, Intent intent) {
        Log.d(TAG, "Received - Start work");
        intent.putExtra(INTENT_EXTRA_IS_UPDATE_WINDOW, false);
        enqueueWork(context, WindowService.class, JOB_ID, intent);
    }

    public static void updateWindow(Context context, Intent intent) {
        Log.d(TAG, "Received - Update window");
        intent.putExtra(INTENT_EXTRA_IS_UPDATE_WINDOW, true);
        enqueueWork(context, WindowService.class, JOB_ID, intent);

    }

    public static void dequeueWork(Context context, Intent intent) {
        Log.d(TAG, "Received - Stop work");
        intent.putExtra(INTENT_EXTRA_IS_CLOSE_WINDOW, true);
        enqueueWork(context, WindowService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d(TAG, "Starting the service process");
        startTheServiceProcess(intent);
    }

    private void startTheServiceProcess(Intent intent) {
        mContext = this;
        if (null != intent && intent.getExtras() != null) {
            Log.i(TAG, "Intent extras are not null");
            boolean isCloseWindow = intent.getBooleanExtra(INTENT_EXTRA_IS_CLOSE_WINDOW, false);
            if (!isCloseWindow) {
                boolean isUpdateWindow = intent.getBooleanExtra(INTENT_EXTRA_IS_UPDATE_WINDOW, false);
                if (windowView == null || !ViewCompat.isAttachedToWindow(windowView)) {
                    isUpdateWindow = false;
                }
                if (!isUpdateWindow) {
                    closeOverlayService();
                    wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                } else {
                    try {
                        wm.removeView(windowView);
                    } catch (Exception ex) {
                        Log.e(TAG, "Caught exception " + ex.toString());
                    }
                }
                @SuppressWarnings("unchecked")
                HashMap<String, Object> paramsMap = (HashMap<String, Object>) intent.getSerializableExtra(INTENT_EXTRA_PARAMS_MAP);
                assert paramsMap != null;
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
                if (wm != null) {
                    showWindow(isUpdateWindow);
                } else {
                    Log.e(TAG, "Unable to show the overlay window as the window manager is null");
                }
            } else {
                closeOverlayService();
                try {
                    Log.d(TAG, "Calling stopSelf");
                    stopSelf();
                    Log.d(TAG, "Stopped self");
                } catch (Exception ex) {
                    Log.d(TAG, "Exception in stopping self");
                }
            }
        } else {
            Log.e(TAG, "Intent extras are null!");
        }
    }

    private void showWindow(final boolean isUpdateWindow) {
        if (isUpdateWindow) {
            Log.d(TAG, "Updating the window");
        } else {
            Log.d(TAG, "Creating the window");
        }
        final WindowManager.LayoutParams params;
        params = new LayoutParams();
        params.width = (windowWidth == 0) ? LayoutParams.MATCH_PARENT : Commons.getPixelsFromDp(mContext, windowWidth);
        params.height = (windowHeight == 0) ? LayoutParams.WRAP_CONTENT : Commons.getPixelsFromDp(mContext, windowHeight);
        params.format = PixelFormat.TRANSLUCENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
            params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_SHOW_WHEN_LOCKED | LayoutParams.FLAG_NOT_FOCUSABLE;
        } else {
            params.type = LayoutParams.TYPE_SYSTEM_ALERT | LayoutParams.TYPE_SYSTEM_OVERLAY;
            params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;
        }
        params.gravity = Commons.getGravity(windowGravity, Gravity.TOP);
        int marginTop = windowMargin.getTop();
        int marginBottom = windowMargin.getBottom();
        int marginLeft = windowMargin.getLeft();
        int marginRight = windowMargin.getRight();
        params.x = Math.max(marginLeft, marginRight);
        params.y = (params.gravity == Gravity.TOP) ? marginTop :
                (params.gravity == Gravity.BOTTOM) ? marginBottom : Math.max(marginTop, marginBottom);
       /* params.horizontalMargin = Math.max(marginLeft, marginRight);
        params.verticalMargin = (params.gravity == Gravity.TOP) ? marginTop :
                (params.gravity == Gravity.BOTTOM) ? marginBottom : Math.max(marginTop, marginBottom);*/
        //windowView.setOnTouchListener(this);
        oServiceHandler.post(() -> {
            LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(params.width, params.height);
            WindowService.this.buildWindowView(contentParams, params.width == LayoutParams.MATCH_PARENT);
            //wm.addView(windowView, params);
        });
        oServiceHandler.post(() -> {
            //WindowService.this.buildWindowView();
            wm.addView(windowView, params);
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void buildWindowView(LinearLayout.LayoutParams params, boolean enableDraggable) {
        windowView = new LinearLayout(mContext);
        windowView.setOrientation(LinearLayout.VERTICAL);
        windowView.setBackgroundColor(Color.WHITE);
        windowView.setLayoutParams(params);
        windowView.removeAllViews();
        windowView.addView(headerView);
        if (bodyView != null)
            windowView.addView(bodyView);
        if (footerView != null)
            windowView.addView(footerView);
        if (enableDraggable)
            windowView.setOnTouchListener(this);
    }

    private void closeOverlayService() {
        Log.d(TAG, "Ending the service process");
        try {
            if (wm != null) {
                if(windowView != null) {
                    wm.removeView(windowView);
                }
            }
            wm = null;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "view not found");
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent event) {
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
                WindowManager.LayoutParams params = (LayoutParams) windowView.getLayoutParams();
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
}
