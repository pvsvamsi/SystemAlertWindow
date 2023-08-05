package in.jvapps.system_alert_window;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import in.jvapps.system_alert_window.utils.Constants;
import in.jvapps.system_alert_window.utils.LogUtils;
import io.flutter.FlutterInjector;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.common.JSONMethodCodec;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.view.FlutterCallbackInformation;

public class BackgroundMethodCallHandlerImpl {

    private static BackgroundMethodCallHandlerImpl _instance;

    private MethodChannel backgroundChannel;
    public AtomicBoolean sIsIsolateRunning = new AtomicBoolean(false);

    private final String TAG = "SAW:BackgroundMethodCallHandlerImpl";
    private final String FLUTTER_BG_ENGINE = "in.jvapps.flutter_bg_engine";

    private BackgroundMethodCallHandlerImpl() {
    }

    public static BackgroundMethodCallHandlerImpl getInstance() {
        if (_instance == null) {
            _instance = new BackgroundMethodCallHandlerImpl();
        }
        return _instance;
    }

    public void startCallBackHandler(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREF_SYSTEM_ALERT_WINDOW, 0);
        long callBackHandle = preferences.getLong(Constants.CALLBACK_HANDLE_KEY, -1);
        LogUtils.getInstance().d(TAG, "onClickCallBackHandle " + callBackHandle);
        if (callBackHandle != -1) {
            FlutterCallbackInformation callback = FlutterCallbackInformation.lookupCallbackInformation(callBackHandle);
            if (callback == null) {
                LogUtils.getInstance().e(TAG, "callback handle not found");
                return;
            }
            FlutterEngine backgroundEngine = new FlutterEngine(context);
            DartExecutor.DartCallback dartCallback = new DartExecutor.DartCallback(context.getAssets(), FlutterInjector.instance().flutterLoader().findAppBundlePath(), callback);
            backgroundEngine.getDartExecutor().executeDartCallback(dartCallback);
            FlutterEngineCache.getInstance().put(FLUTTER_BG_ENGINE, backgroundEngine);
            sIsIsolateRunning.set(true);
        }
    }

    public void stopCallBackHandler() {
        if(backgroundChannel != null) {
            backgroundChannel.setMethodCallHandler(null);
            backgroundChannel = null;
        }
        sIsIsolateRunning.set(false);
        FlutterEngineCache.getInstance().remove(FLUTTER_BG_ENGINE);
    }

    public void invokeCallBack(Context context, String type, Object params) {
        if(!sIsIsolateRunning.get()){
            startCallBackHandler(context);
        }
        if(!sIsIsolateRunning.get()){
            LogUtils.getInstance().e(TAG, "invokeCallBack failed, as call back handler is not registered");
        }

        if (backgroundChannel == null) {
            FlutterEngine engine = FlutterEngineCache.getInstance().get(FLUTTER_BG_ENGINE);
            if (engine == null) {
                LogUtils.getInstance().e(TAG, "invokeCallBack failed, as flutter engine is null");
                return;
            }
            backgroundChannel = new MethodChannel(engine.getDartExecutor(), Constants.BACKGROUND_CHANNEL, JSONMethodCodec.INSTANCE);
        }

        List<Object> argumentsList = new ArrayList<>();
        LogUtils.getInstance().d(TAG, "invoking callback for tag " + params);
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREF_SYSTEM_ALERT_WINDOW, 0);
        long codeCallBackHandle = preferences.getLong(Constants.CODE_CALLBACK_HANDLE_KEY, -1);
        //LogUtils.getInstance().i(TAG, "codeCallBackHandle " + codeCallBackHandle);
        if (codeCallBackHandle == -1) {
            LogUtils.getInstance().e(TAG, "invokeCallBack failed, as codeCallBackHandle is null");
        } else {
            argumentsList.add(codeCallBackHandle);
            argumentsList.add(type);
            argumentsList.add(params);
            try {
                LogUtils.getInstance().d(TAG, "Invoking on method channel");
                int[] retries = {2};
                invokeCallBackToFlutter(backgroundChannel, "callBack", argumentsList, retries);
                //backgroundChannel.invokeMethod("callBack", argumentsList);
            } catch (Exception ex) {
                LogUtils.getInstance().e(TAG, "Exception in invoking callback " + ex);
            }
        }
    }

    private void invokeCallBackToFlutter(final MethodChannel channel, final String method, final List<Object> arguments, final int[] retries) {
        channel.invokeMethod(method, arguments, new MethodChannel.Result() {
            @Override
            public void success(Object o) {
                LogUtils.getInstance().i(TAG, "Invoke call back success");
            }

            @Override
            public void error(String s, String s1, Object o) {
                LogUtils.getInstance().e(TAG, "Error " + s + s1);
            }

            @Override
            public void notImplemented() {
                //To fix the dart initialization delay.
                if (retries[0] > 0) {
                    LogUtils.getInstance().d(TAG, "Not Implemented method " + method + ". Trying again to check if it works");
                    invokeCallBackToFlutter(channel, method, arguments, retries);
                } else {
                    LogUtils.getInstance().e(TAG, "Not Implemented method " + method);
                }
                retries[0]--;
            }
        });
    }
}
