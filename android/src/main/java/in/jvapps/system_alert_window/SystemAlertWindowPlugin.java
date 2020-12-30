package in.jvapps.system_alert_window;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import in.jvapps.system_alert_window.services.WindowServiceNew;
import in.jvapps.system_alert_window.utils.Commons;
import in.jvapps.system_alert_window.utils.Constants;
import in.jvapps.system_alert_window.utils.NotificationHelper;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.view.FlutterCallbackInformation;
import io.flutter.view.FlutterMain;
import io.flutter.view.FlutterNativeView;
import io.flutter.view.FlutterRunArguments;

import static in.jvapps.system_alert_window.services.WindowServiceNew.INTENT_EXTRA_IS_CLOSE_WINDOW;
import static in.jvapps.system_alert_window.services.WindowServiceNew.INTENT_EXTRA_IS_UPDATE_WINDOW;
import static in.jvapps.system_alert_window.utils.Constants.CHANNEL;
import static in.jvapps.system_alert_window.utils.Constants.INTENT_EXTRA_PARAMS_MAP;

public class SystemAlertWindowPlugin extends Activity implements MethodCallHandler {

    private Context mContext;
    @SuppressLint("StaticFieldLeak")
    private static Activity mActivity;
    @SuppressLint("StaticFieldLeak")
    private static FlutterNativeView sBackgroundFlutterView;
    private static PluginRegistry.PluginRegistrantCallback sPluginRegistrantCallback;
    public static AtomicBoolean sIsIsolateRunning = new AtomicBoolean(false);

    static MethodChannel methodChannel;
    static MethodChannel backgroundChannel;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1237;
    private static NotificationManager notificationManager;
    private static String TAG = "SystemAlertWindowPlugin";

    @SuppressWarnings("unused")
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), CHANNEL);
        channel.setMethodCallHandler(new SystemAlertWindowPlugin(registrar.context(), registrar.activity(), channel));
    }

    private SystemAlertWindowPlugin(Context context, Activity activity, MethodChannel newMethodChannel) {
        this.mContext = context;
        mActivity = activity;
        methodChannel = newMethodChannel;
        methodChannel.setMethodCallHandler(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onMethodCall(MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "getPlatformVersion":
                result.success("Android " + Build.VERSION.RELEASE);
                break;
            case "requestPermissions":
                if (askPermission()) {
                    result.success(true);
                } else {
                    result.success(false);
                }
                break;
            case "checkPermissions":
                if (checkPermission()) {
                    result.success(true);
                } else {
                    result.success(false);
                }
                break;
            case "showSystemWindow":
                if (checkPermission()) {
                    assert (call.arguments != null);
                    List arguments = (List) call.arguments;
                    String title = (String) arguments.get(0);
                    String body = (String) arguments.get(1);
                    HashMap<String, Object> params = (HashMap<String, Object>) arguments.get(2);
                    if (Commons.isForceAndroidBubble(mContext) || Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Log.d(TAG, "Going to show Bubble");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            showBubble(title, body, params);
                        }
                    } else {
                        Log.d(TAG, "Going to show System Alert Window");
                        final Intent i = new Intent(mContext, WindowServiceNew.class);
                        i.putExtra(INTENT_EXTRA_PARAMS_MAP, params);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        i.putExtra(INTENT_EXTRA_IS_UPDATE_WINDOW, false);
                        //WindowService.enqueueWork(mContext, i);
                        mContext.startService(i);
                    }
                    result.success(true);
                } else {
                    Toast.makeText(mContext, "Please give draw over other apps permission", Toast.LENGTH_LONG).show();
                    result.success(false);
                }
                break;
            case "updateSystemWindow":
                if (checkPermission()) {
                    assert (call.arguments != null);
                    List updateArguments = (List) call.arguments;
                    String updateTitle = (String) updateArguments.get(0);
                    String updateBody = (String) updateArguments.get(1);
                    HashMap<String, Object> updateParams = (HashMap<String, Object>) updateArguments.get(2);
                    if (Commons.isForceAndroidBubble(mContext) || Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                        Log.d(TAG, "Going to update Bubble");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            showBubble(updateTitle, updateBody, updateParams);
                        }
                    } else {
                        Log.d(TAG, "Going to update System Alert Window");
                        final Intent i = new Intent(mContext, WindowServiceNew.class);
                        i.putExtra(INTENT_EXTRA_PARAMS_MAP, updateParams);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        i.putExtra(INTENT_EXTRA_IS_UPDATE_WINDOW, true);
                        //WindowService.enqueueWork(mContext, i);
                        mContext.startService(i);
                    }
                    result.success(true);
                } else {
                    Toast.makeText(mContext, "Please give draw over other apps permission", Toast.LENGTH_LONG).show();
                    result.success(false);
                }
                break;
            case "closeSystemWindow":
                if (checkPermission()) {
                    if (Commons.isForceAndroidBubble(mContext) || Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                        NotificationHelper.getInstance(mContext).dismissNotification();
                    } else {
                        final Intent i = new Intent(mContext, WindowServiceNew.class);
                        i.putExtra(INTENT_EXTRA_IS_CLOSE_WINDOW, true);
                        //WindowService.dequeueWork(mContext, i);
                        mContext.startService(i);
                    }
                    result.success(true);
                } else {
                    Toast.makeText(mContext, "Please give draw over other apps permission", Toast.LENGTH_LONG).show();
                    result.success(false);
                }
                break;
            case "registerCallBackHandler":
                try {
                    List callBackArguments = (List) call.arguments;
                    if (callBackArguments != null) {
                        long callbackHandle = Long.parseLong(String.valueOf(callBackArguments.get(0)));
                        long onClickHandle = Long.parseLong(String.valueOf(callBackArguments.get(1)));
                        SharedPreferences preferences = mContext.getSharedPreferences(Constants.SHARED_PREF_SYSTEM_ALERT_WINDOW, 0);
                        preferences.edit().putLong(Constants.CALLBACK_HANDLE_KEY, callbackHandle)
                                .putLong(Constants.CODE_CALLBACK_HANDLE_KEY, onClickHandle).apply();
                        startCallBackHandler(mContext);
                        result.success(true);
                    } else {
                        Log.e(TAG, "Unable to register on click handler. Arguments are null");
                        result.success(false);
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "Exception in registerOnClickHandler " + ex.toString());
                    result.success(false);
                }
                break;
            default:
                result.notImplemented();
        }
    }

    public static void setPluginRegistrant(PluginRegistry.PluginRegistrantCallback callback) {
        sPluginRegistrantCallback = callback;
    }

    public static void startCallBackHandler(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREF_SYSTEM_ALERT_WINDOW, 0);
        long callBackHandle = preferences.getLong(Constants.CALLBACK_HANDLE_KEY, -1);
        Log.d(TAG, "onClickCallBackHandle " + callBackHandle);
        if (callBackHandle != -1) {
            FlutterMain.ensureInitializationComplete(context, null);
            String mAppBundlePath = FlutterMain.findAppBundlePath();
            FlutterCallbackInformation flutterCallback = FlutterCallbackInformation.lookupCallbackInformation(callBackHandle);
            if (sBackgroundFlutterView == null) {
                sBackgroundFlutterView = new FlutterNativeView(context, true);
                if (mAppBundlePath != null && !sIsIsolateRunning.get()) {
                    if (sPluginRegistrantCallback == null) {
                        Log.i(TAG, "Unable to start callBackHandle... as plugin is not registered");
                        return;
                    }
                    Log.i(TAG, "Starting callBackHandle...");
                    FlutterRunArguments args = new FlutterRunArguments();
                    args.bundlePath = mAppBundlePath;
                    args.entrypoint = flutterCallback.callbackName;
                    args.libraryPath = flutterCallback.callbackLibraryPath;
                    sBackgroundFlutterView.runFromBundle(args);
                    sPluginRegistrantCallback.registerWith(sBackgroundFlutterView.getPluginRegistry());
                    backgroundChannel = new MethodChannel(sBackgroundFlutterView, Constants.BACKGROUND_CHANNEL);
                    sIsIsolateRunning.set(true);
                }
            } else {
                if (backgroundChannel == null) {
                    backgroundChannel = new MethodChannel(sBackgroundFlutterView, Constants.BACKGROUND_CHANNEL);
                }
                sIsIsolateRunning.set(true);
            }
        }
    }

    public static void invokeCallBack(Context context, String type, Object params) {
        List<Object> argumentsList = new ArrayList<>();
        Log.v(TAG, "invoking callback for tag " + params);
        /*try {
            argumentsList.add(type);
            argumentsList.add(params);
            Log.v(TAG, "invoking callback for tag "+params);
            methodChannel.invokeMethod("callBack", argumentsList);
        } catch (Exception ex) {
            Log.e(TAG, "invokeCallBack Exception : " + ex.toString());
            SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREF_SYSTEM_ALERT_WINDOW, 0);
            long codeCallBackHandle = preferences.getLong(Constants.CODE_CALLBACK_HANDLE_KEY, -1);
            Log.i(TAG, "codeCallBackHandle " + codeCallBackHandle);
            if (codeCallBackHandle == -1) {
                Log.e(TAG, "invokeCallBack failed, as codeCallBackHandle is null");
            } else {
                argumentsList.clear();
                argumentsList.add(codeCallBackHandle);
                argumentsList.add(type);
                argumentsList.add(params);
                backgroundChannel.invokeMethod("callBack", argumentsList);
            }
        }*/
        SharedPreferences preferences = context.getSharedPreferences(Constants.SHARED_PREF_SYSTEM_ALERT_WINDOW, 0);
        long codeCallBackHandle = preferences.getLong(Constants.CODE_CALLBACK_HANDLE_KEY, -1);
        //Log.i(TAG, "codeCallBackHandle " + codeCallBackHandle);
        if (codeCallBackHandle == -1) {
            Log.e(TAG, "invokeCallBack failed, as codeCallBackHandle is null");
        } else {
            argumentsList.clear();
            argumentsList.add(codeCallBackHandle);
            argumentsList.add(type);
            argumentsList.add(params);
            if (sIsIsolateRunning.get()) {
                if (backgroundChannel == null) {
                    Log.v(TAG, "Recreating the background channel as it is null");
                    backgroundChannel = new MethodChannel(sBackgroundFlutterView, Constants.BACKGROUND_CHANNEL);
                }
                try {
                    Log.v(TAG, "Invoking on method channel");
                    int[] retries = {2};
                    invokeCallBackToFlutter(backgroundChannel, "callBack", argumentsList, retries);
                    //backgroundChannel.invokeMethod("callBack", argumentsList);
                } catch (Exception ex) {
                    Log.e(TAG, "Exception in invoking callback " + ex.toString());
                }
            } else {
                Log.e(TAG, "invokeCallBack failed, as isolate is not running");
            }
        }
    }

    private static void invokeCallBackToFlutter(final MethodChannel channel, final String method, final List<Object> arguments, final int[] retries) {
        channel.invokeMethod(method, arguments, new MethodChannel.Result() {
            @Override
            public void success(Object o) {
                Log.i(TAG, "Invoke call back success");
            }

            @Override
            public void error(String s, String s1, Object o) {
                Log.e(TAG, "Error " + s + s1);
            }

            @Override
            public void notImplemented() {
                //To fix the dart initialization delay.
                if (retries[0] > 0) {
                    Log.d(TAG, "Not Implemented method " + method + ". Trying again to check if it works");
                    invokeCallBackToFlutter(channel, method, arguments, retries);
                } else {
                    Log.e(TAG, "Not Implemented method " + method);
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
                Log.e(TAG, "System Alert Window will not work without 'Can Draw Over Other Apps' permission");
                Toast.makeText(mContext, "System Alert Window will not work without 'Can Draw Over Other Apps' permission", Toast.LENGTH_LONG).show();
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean askPermission() {
        if (Commons.isForceAndroidBubble(mContext) || Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
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
                        Log.e(TAG, "Can't detect the permission change, as the mActivity is null");
                    } else {
                        Log.e(TAG, "'Can Draw Over Other Apps' permission is not granted");
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
    public boolean checkPermission() {
        if (Commons.isForceAndroidBubble(mContext) || Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
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
