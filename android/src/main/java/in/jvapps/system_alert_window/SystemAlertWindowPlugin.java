package in.jvapps.system_alert_window;

import static in.jvapps.system_alert_window.services.WindowServiceNew.INTENT_EXTRA_IS_CLOSE_WINDOW;
import static in.jvapps.system_alert_window.services.WindowServiceNew.INTENT_EXTRA_IS_UPDATE_WINDOW;
import static in.jvapps.system_alert_window.utils.Constants.CHANNEL;
import static in.jvapps.system_alert_window.utils.Constants.INTENT_EXTRA_PARAMS_MAP;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import in.jvapps.system_alert_window.services.WindowServiceNew;
import in.jvapps.system_alert_window.utils.Commons;
import in.jvapps.system_alert_window.utils.Constants;
import in.jvapps.system_alert_window.utils.LogUtils;
import in.jvapps.system_alert_window.utils.NotificationHelper;
import io.flutter.FlutterInjector;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.JSONMethodCodec;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.view.FlutterCallbackInformation;

public class SystemAlertWindowPlugin extends Activity implements FlutterPlugin, ActivityAware, MethodCallHandler {

    private final String flutterEngineId = "system_alert_window_engine";
    private Context mContext;
    private Activity mActivity;
    public AtomicBoolean sIsIsolateRunning = new AtomicBoolean(false);

    private MethodChannel methodChannel;
    private MethodChannel backgroundChannel;
    public int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1237;
    private final String TAG = "SAW:Plugin";

    public SystemAlertWindowPlugin() {
    }

    private SystemAlertWindowPlugin(Context context, Activity activity, MethodChannel newMethodChannel) {
        this.mContext = context;
        LogUtils.getInstance().setContext(this.mContext);
        mActivity = activity;
        methodChannel = newMethodChannel;
        methodChannel.setMethodCallHandler(this);
    }

    @SuppressWarnings("unused")
    public synchronized FlutterEngine getFlutterEngine(Context context) {
        FlutterEngine flutterEngine = FlutterEngineCache.getInstance().get(flutterEngineId);
        if (flutterEngine == null) {
            // Maybe need a boolean flag to tell us we're currently loading the main flutter engine.
            flutterEngine = new FlutterEngine(context.getApplicationContext());
            flutterEngine.getDartExecutor().executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault());
            FlutterEngineCache.getInstance().put(flutterEngineId, flutterEngine);
        }
        return flutterEngine;
    }

    @SuppressWarnings("unused")
    public void disposeFlutterEngine() {
        FlutterEngine flutterEngine = FlutterEngineCache.getInstance().get(flutterEngineId);
        if (flutterEngine != null) {
            flutterEngine.destroy();
            FlutterEngineCache.getInstance().remove(flutterEngineId);
        }
    }

    @SuppressWarnings({"unused", "deprecation"})
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), CHANNEL, JSONMethodCodec.INSTANCE);
        channel.setMethodCallHandler(new SystemAlertWindowPlugin(registrar.context(), registrar.activity(), channel));
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        this.mContext = flutterPluginBinding.getApplicationContext();
        LogUtils.getInstance().setContext(this.mContext);
        this.methodChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), CHANNEL, JSONMethodCodec.INSTANCE);
        this.methodChannel.setMethodCallHandler(this);
        LogUtils.getInstance().d(TAG, "onAttachedToEngine");
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        this.mContext = null;
        this.methodChannel.setMethodCallHandler(null);
        LogUtils.getInstance().d(TAG, "onAttachedToEngine");
        LogUtils.getInstance().setContext(null);
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding activityPluginBinding) {
        this.mActivity = activityPluginBinding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        this.mActivity = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding activityPluginBinding) {
        this.mActivity = activityPluginBinding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
        this.mActivity = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        try {
            String prefMode;
            JSONArray arguments;
            switch (call.method) {
                case "getPlatformVersion":
                    result.success("Android " + Build.VERSION.RELEASE);
                    break;
                case "enableLogs":
                    assert (call.arguments != null);
                    arguments = (JSONArray) call.arguments;
                    LogUtils.getInstance().setLogFileEnabled((boolean) arguments.get(0));
                    result.success(true);
                    break;
                case "getLogFile":
                    result.success(LogUtils.getInstance().getLogFilePath());
                    break;
                case "requestPermissions":
                    assert (call.arguments != null);
                    arguments = (JSONArray) call.arguments;
                    prefMode = (String) arguments.get(0);
                    if (prefMode == null) {
                        prefMode = "default";
                    }
                    if (askPermission(!isBubbleMode(prefMode))) {
                        result.success(true);
                    } else {
                        result.success(false);
                    }
                    break;
                case "checkPermissions":
                    arguments = (JSONArray) call.arguments;
                    prefMode = (String) arguments.get(0);
                    if (prefMode == null) {
                        prefMode = "default";
                    }
                    if (checkPermission(!isBubbleMode(prefMode))) {
                        result.success(true);
                    } else {
                        result.success(false);
                    }
                    break;
                case "showSystemWindow":
                    assert (call.arguments != null);
                    arguments = (JSONArray) call.arguments;
                    String title = (String) arguments.get(0);
                    String body = (String) arguments.get(1);
                    JSONObject paramObj = (JSONObject) arguments.get(2);
                    @SuppressWarnings("unchecked")
                    HashMap<String, Object> params = new Gson().fromJson(paramObj.toString(), HashMap.class);
                    prefMode = (String) arguments.get(3);
                    if (prefMode == null) {
                        prefMode = "default";
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && isBubbleMode(prefMode)) {
                        if (checkPermission(false)) {
                            LogUtils.getInstance().d(TAG, "Going to show Bubble");
                            showBubble(title, body, params);
                        } else {
                            Toast.makeText(mContext, "Please enable bubbles", Toast.LENGTH_LONG).show();
                            result.success(false);
                        }
                    } else {
                        if (checkPermission(true)) {
                            LogUtils.getInstance().d(TAG, "Going to show System Alert Window");
                            final Intent i = new Intent(mContext, WindowServiceNew.class);
                            i.putExtra(INTENT_EXTRA_PARAMS_MAP, params);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            i.putExtra(INTENT_EXTRA_IS_UPDATE_WINDOW, false);
                            //WindowService.enqueueWork(mContext, i);
                            mContext.startService(i);
                        } else {
                            Toast.makeText(mContext, "Please give draw over other apps permission", Toast.LENGTH_LONG).show();
                            result.success(false);
                        }
                    }
                    result.success(true);
                    break;
                case "updateSystemWindow":
                    assert (call.arguments != null);
                    JSONArray updateArguments = (JSONArray) call.arguments;
                    String updateTitle = (String) updateArguments.get(0);
                    String updateBody = (String) updateArguments.get(1);
                    @SuppressWarnings("unchecked")
                    HashMap<String, Object> updateParams = new Gson().fromJson(updateArguments.get(2).toString(), HashMap.class);
                    prefMode = (String) updateArguments.get(3);
                    if (prefMode == null) {
                        prefMode = "default";
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && isBubbleMode(prefMode)) {
                        if (checkPermission(false)) {
                            LogUtils.getInstance().d(TAG, "Going to update Bubble");
                            NotificationHelper.getInstance(mContext).dismissNotification();
                            showBubble(updateTitle, updateBody, updateParams);
                        } else {
                            Toast.makeText(mContext, "Please enable bubbles", Toast.LENGTH_LONG).show();
                            result.success(false);
                        }
                    } else {
                        if (checkPermission(true)) {
                            LogUtils.getInstance().d(TAG, "Going to update System Alert Window");
                            final Intent i = new Intent(mContext, WindowServiceNew.class);
                            i.putExtra(INTENT_EXTRA_PARAMS_MAP, updateParams);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            i.putExtra(INTENT_EXTRA_IS_UPDATE_WINDOW, true);
                            //WindowService.enqueueWork(mContext, i);
                            mContext.startService(i);
                        } else {
                            Toast.makeText(mContext, "Please give draw over other apps permission", Toast.LENGTH_LONG).show();
                            result.success(false);
                        }
                    }
                    result.success(true);
                    break;
                case "closeSystemWindow":
                    arguments = (JSONArray) call.arguments;
                    prefMode = (String) arguments.get(0);
                    if (prefMode == null) {
                        prefMode = "default";
                    }
                    if (checkPermission(!isBubbleMode(prefMode))) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && isBubbleMode(prefMode)) {
                            NotificationHelper.getInstance(mContext).dismissNotification();
                        } else {
                            final Intent i = new Intent(mContext, WindowServiceNew.class);
                            i.putExtra(INTENT_EXTRA_IS_CLOSE_WINDOW, true);
                            //WindowService.dequeueWork(mContext, i);
                            mContext.startService(i);
                        }
                        result.success(true);
                    }
                    break;
                case "registerCallBackHandler":
                    try {
                        JSONArray callBackArguments = (JSONArray) call.arguments;
                        if (callBackArguments != null) {
                            long callbackHandle = Long.parseLong(String.valueOf(callBackArguments.get(0)));
                            long onClickHandle = Long.parseLong(String.valueOf(callBackArguments.get(1)));
                            SharedPreferences preferences = mContext.getSharedPreferences(Constants.SHARED_PREF_SYSTEM_ALERT_WINDOW, 0);
                            preferences.edit().putLong(Constants.CALLBACK_HANDLE_KEY, callbackHandle)
                                    .putLong(Constants.CODE_CALLBACK_HANDLE_KEY, onClickHandle).apply();
                            startCallBackHandler(mContext);
                            result.success(true);
                        } else {
                            LogUtils.getInstance().e(TAG, "Unable to register on click handler. Arguments are null");
                            result.success(false);
                        }
                    } catch (Exception ex) {
                        LogUtils.getInstance().e(TAG, "Exception in registerOnClickHandler " + ex);
                        result.success(false);
                    }
                    break;
                default:
                    result.notImplemented();
            }
        } catch (Exception ex) {
            LogUtils.getInstance().e(TAG, ex.toString());
        }

    }

    private boolean isBubbleMode(String prefMode) {
        boolean isPreferOverlay = "overlay".equalsIgnoreCase(prefMode);
        return Commons.isForceAndroidBubble(mContext) ||
                (!isPreferOverlay && ("bubble".equalsIgnoreCase(prefMode) || Build.VERSION.SDK_INT >= Build.VERSION_CODES.R));
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
            //backgroundEngine.getServiceControlSurface().attachToService(new WindowServiceNew(), null, false);
            backgroundChannel = new MethodChannel(backgroundEngine.getDartExecutor().getBinaryMessenger(), Constants.BACKGROUND_CHANNEL, JSONMethodCodec.INSTANCE);
            sIsIsolateRunning.set(true);
            DartExecutor.DartCallback dartCallback = new DartExecutor.DartCallback(context.getAssets(), FlutterInjector.instance().flutterLoader().findAppBundlePath(), callback);
            backgroundEngine.getDartExecutor().executeDartCallback(dartCallback);
        }
    }

    public void invokeCallBack(Context context, String type, Object params) {
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
            if (sIsIsolateRunning.get()) {
                try {
                    LogUtils.getInstance().d(TAG, "Invoking on method channel");
                    int[] retries = {2};
                    invokeCallBackToFlutter(backgroundChannel, "callBack", argumentsList, retries);
                    //backgroundChannel.invokeMethod("callBack", argumentsList);
                } catch (Exception ex) {
                    LogUtils.getInstance().e(TAG, "Exception in invoking callback " + ex);
                }
            } else {
                LogUtils.getInstance().e(TAG, "invokeCallBack failed, as isolate is not running");
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

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(mContext)) {
                LogUtils.getInstance().e(TAG, "System Alert Window will not work without 'Can Draw Over Other Apps' permission");
                Toast.makeText(mContext, "System Alert Window will not work without 'Can Draw Over Other Apps' permission", Toast.LENGTH_LONG).show();
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean askPermission(boolean isOverlay) {
        if (!isOverlay && (Commons.isForceAndroidBubble(mContext) || Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)) {
            return NotificationHelper.getInstance(mContext).areBubblesAllowed();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(mContext)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + mContext.getPackageName()));
                if (mActivity == null) {
                    if (mContext != null) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        Toast.makeText(mContext, "Please grant, Can Draw Over Other Apps permission.", Toast.LENGTH_SHORT).show();
                        LogUtils.getInstance().e(TAG, "Can't detect the permission change, as the mActivity is null");
                    } else {
                        LogUtils.getInstance().e(TAG, "'Can Draw Over Other Apps' permission is not granted");
                        Toast.makeText(mContext, "Can Draw Over Other Apps permission is required. Please grant it from the app settings", Toast.LENGTH_LONG).show();
                    }
                } else {
                    mActivity.startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
                }
            } else {
                return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean checkPermission(boolean isOverlay) {
        if (!isOverlay && (Commons.isForceAndroidBubble(mContext) || Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)) {
            //return NotificationHelper.getInstance(mContext).areBubblesAllowed();
            return true;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(mContext);
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void showBubble(String title, String body, HashMap<String, Object> params) {
        Icon icon = Icon.createWithResource(mContext, R.drawable.ic_notification);
        NotificationHelper notificationHelper = NotificationHelper.getInstance(mContext);
        notificationHelper.showNotification(icon, title, body, params);
    }
}
