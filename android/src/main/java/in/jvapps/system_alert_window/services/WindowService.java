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

    private static WindowManager wm;
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
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        oServiceHandler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //startTheServiceProcess(intent);
        oServiceHandler = new Handler();
        return super.onStartCommand(intent, flags, startId);
    }

    public static void enqueueWork(Context context, Intent intent) {
        Log.d(TAG, "Received a work");
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
            Log.d(TAG, "Intent extras are not null");
            @SuppressWarnings("unchecked")
            HashMap<String, Object> paramsMap = (HashMap<String, Object>) intent.getSerializableExtra(INTENT_EXTRA_PARAMS_MAP);
            Map<String, Object> headersMap = Commons.getMapFromObject(paramsMap, KEY_HEADER);
            Map<String, Object> bodyMap = Commons.getMapFromObject(paramsMap, KEY_BODY);
            Map<String, Object> footerMap = Commons.getMapFromObject(paramsMap, KEY_FOOTER);
            windowMargin = UiBuilder.getMargin(mContext, paramsMap.get(KEY_MARGIN));
            windowGravity = (String) paramsMap.get(KEY_GRAVITY);
            windowWidth = NumberUtils.getInt(paramsMap.get(KEY_WIDTH));
            windowHeight = NumberUtils.getInt(paramsMap.get(KEY_HEIGHT));
            headerView = new HeaderView(this, headersMap).getView();
            bodyView = new BodyView(this, bodyMap).getView();
            footerView = new FooterView(this, footerMap).getView();
            showWindow();
        } else {
            Log.w(TAG, "Intent extras are null!");
        }
    }

    private void showWindow() {
        final WindowManager.LayoutParams params;
        params = new LayoutParams();
        params.width = (windowWidth == 0) ? LayoutParams.MATCH_PARENT : windowWidth;
        params.height = (windowHeight == 0) ? LayoutParams.WRAP_CONTENT : windowHeight;
        params.format = PixelFormat.TRANSLUCENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "Device greater than or equals to oreo");
            params.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
            params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        } else {
            Log.d(TAG, "Device is less than oreo");
            params.type = LayoutParams.TYPE_SYSTEM_ALERT | LayoutParams.TYPE_SYSTEM_OVERLAY;
            params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL;
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
        oServiceHandler.post(new Runnable() {
            @Override
            public void run() {
                WindowService.this.buildWindowView();
                //wm.addView(windowView, params);
            }
        });
        oServiceHandler.post(new Runnable() {
            @Override
            public void run() {
                //WindowService.this.buildWindowView();
                wm.addView(windowView, params);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void buildWindowView() {
        windowView = new LinearLayout(mContext);
        windowView.setOrientation(LinearLayout.VERTICAL);
        windowView.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        windowView.setLayoutParams(params);
        windowView.addView(headerView);
        windowView.addView(bodyView);
        windowView.addView(footerView);
        windowView.setOnTouchListener(this);
    }

    public static void closeOverlayService(){
        try{
            if (wm != null)
                wm.removeView(windowView);
            wm = null;
        }catch(IllegalArgumentException e){
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
