package in.jvapps.system_alert_window;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import in.jvapps.system_alert_window.services.BubbleService;
import in.jvapps.system_alert_window.services.WindowServiceNew;
import in.jvapps.system_alert_window.utils.Commons;
import in.jvapps.system_alert_window.utils.Constants;
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

    @Override
    public void onMethodCall(MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "getPlatformVersion":
                result.success("Android " + Build.VERSION.RELEASE);
                break;
            case "checkPermissions":
                if (checkPermission()) {
                    result.success("Permissions are granted");
                } else {
                    result.success("Permissions are not granted");
                }
                break;
            case "showSystemWindow":
                assert (call.arguments != null);
                HashMap<String, Object> params = (HashMap<String, Object>) call.arguments;
                if(Commons.isForceAndroidBubble(mContext) || Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){
                    Log.d(TAG, "Going to show Bubble");
                    final Intent i = new Intent(mContext, BubbleService.class);
                    i.putExtra(INTENT_EXTRA_PARAMS_MAP, params);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mContext.startForegroundService(i);
                    }
                }else{
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
                break;
            case "updateSystemWindow":
                assert (call.arguments != null);
                HashMap<String, Object> updateParams = (HashMap<String, Object>) call.arguments;
                if(Commons.isForceAndroidBubble(mContext) || Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){
                    Log.d(TAG, "Going to update Bubble");
                    final Intent i = new Intent(mContext, BubbleService.class);
                    mContext.stopService(i);
                    i.putExtra(INTENT_EXTRA_PARAMS_MAP, updateParams);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mContext.startForegroundService(i);
                    }
                }else{
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
                break;
            case "closeSystemWindow":
                if(Commons.isForceAndroidBubble(mContext) || Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){
                    final Intent i = new Intent(mContext, BubbleService.class);
                    mContext.stopService(i);
                }else{
                    final Intent i = new Intent(mContext, WindowServiceNew.class);
                    i.putExtra(INTENT_EXTRA_IS_CLOSE_WINDOW, true);
                    //WindowService.dequeueWork(mContext, i);
                    mContext.startService(i);
                }
                result.success(true);
                break;
            case "registerCallBackHandler":
                try {
                    List arguments = (List) call.arguments;
                    if (arguments != null) {
                        long callbackHandle = Long.parseLong(String.valueOf(arguments.get(0)));
                        long onClickHandle = Long.parseLong(String.valueOf(arguments.get(1)));
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
                if(mAppBundlePath != null && !sIsIsolateRunning.get()){
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
            }else {
                if(backgroundChannel == null){
                    backgroundChannel = new MethodChannel(sBackgroundFlutterView, Constants.BACKGROUND_CHANNEL);
                }
                sIsIsolateRunning.set(true);
            }
        }
    }

    public static void invokeCallBack(Context context, String type, Object params) {
        List<Object> argumentsList = new ArrayList<>();
        Log.v(TAG, "invoking callback for tag "+params);
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
            if(sIsIsolateRunning.get()) {
                if(backgroundChannel == null){
                    Log.v(TAG, "Recreating the background channel as it is null");
                    backgroundChannel = new MethodChannel(sBackgroundFlutterView, Constants.BACKGROUND_CHANNEL);
                }
                try {
                    Log.v(TAG, "Invoking on method channel");
                    int[] retries = {2};
                    invokeCallBackToFlutter(backgroundChannel, "callBack", argumentsList, retries);
                    //backgroundChannel.invokeMethod("callBack", argumentsList);
                }catch(Exception ex){
                    Log.e(TAG, "Exception in invoking callback "+ex.toString());
                }
            }else{
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
                Log.e(TAG, "Error " + s+s1);
            }

            @Override
            public void notImplemented() {
                //To fix the dart initialization delay.
                if (retries[0] > 0) {
                    Log.d(TAG, "Not Implemented method "+ method+". Trying again to check if it works");
                    invokeCallBackToFlutter(channel, method, arguments, retries);
                } else {
                    Log.e(TAG, "Not Implemented method "+ method);
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

    public boolean checkPermission() {
        if (Commons.isForceAndroidBubble(mContext) ||  Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            if(Build.VERSION.SDK_INT == Build.VERSION_CODES.Q){
                Log.i(TAG, "Forcing using Android bubble");
                return handleBubblesPermissionForAndroidQ();
            }
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                initNotificationManager();
                if (!notificationManager.areBubblesAllowed()) {
                    Log.e(TAG, "System Alert Window will not work without enabling the android bubbles");
                    Toast.makeText(mContext, "System Alert Window will not work without enabling the android bubbles", Toast.LENGTH_LONG).show();
                } else {
                    //TODO to check for higher android versions, post their release
                    Log.d(TAG, "Android bubbles are enabled");
                    return true;
                }
            }
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean handleBubblesPermissionForAndroidQ() {
        int devOptions = Settings.Secure.getInt(mContext.getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);
        if (devOptions == 1) {
            Log.d(TAG, "Android bubbles are enabled");
            return true;
        } else {
            Log.e(TAG, "System Alert Window will not work without enabling the android bubbles");
            Toast.makeText(mContext, "Enable android bubbles in the developer options, for System Alert Window to work", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initNotificationManager() {
        if (notificationManager == null) {
            if (mContext == null) {
                if (mActivity != null) {
                    mContext = mActivity.getApplicationContext();
                }
            }
            if (mContext == null) {
                Log.e(TAG, "Context is null. Can't show the System Alert Window");
                return;
            }
            notificationManager = mContext.getSystemService(NotificationManager.class);
        }
    }
}
