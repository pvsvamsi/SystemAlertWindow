package in.jvapps.system_alert_window;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
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

import in.jvapps.system_alert_window.services.BubbleService;
import in.jvapps.system_alert_window.services.WindowService;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import static in.jvapps.system_alert_window.utils.Constants.INTENT_EXTRA_PARAMS_MAP;

public class SystemAlertWindowPlugin extends Activity implements MethodCallHandler {

    private Context mContext;
    @SuppressLint("StaticFieldLeak")
    private static Activity mActivity;
    static MethodChannel methodChannel;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1237;
    private static NotificationManager notificationManager;
    private static String TAG = "SystemAlertWindowPlugin";

    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "system_alert_window");
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
                result.success("Android " + android.os.Build.VERSION.RELEASE);
                break;
            case "checkPermissions":
                if (checkPermission()) {
                    result.success("Permissions are granted");
                } else {
                    result.error("Permissions are not granted", null, null);
                }
                break;
            case "showSystemWindow":
                assert (call.arguments != null);
                @SuppressWarnings("unchecked")
                HashMap<String, Object> params = (HashMap<String, Object>) call.arguments;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    Log.d(TAG, "Going to show System Alert Window");
                    WindowService.closeOverlayService();
                    final Intent i = new Intent(mContext, WindowService.class);
                    mContext.stopService(i);
                    i.putExtra(INTENT_EXTRA_PARAMS_MAP, params);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    WindowService.enqueueWork(mContext, i);
                } else {
                    Log.d(TAG, "Going to show Bubble");
                    final Intent i = new Intent(mContext, BubbleService.class);
                    i.putExtra(INTENT_EXTRA_PARAMS_MAP, params);
                    //mContext.stopService(i);
                    mContext.startForegroundService(i);
                }
                result.success(true);
                break;
            case "closeSystemWindow":
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    WindowService.closeOverlayService();
                } else {
                    final Intent i = new Intent(mContext, BubbleService.class);
                    mContext.stopService(i);
                }
                result.success(true);
                break;
            default:
                result.notImplemented();
        }
    }

    public static void invokeCallBack(String type, Object params) {
        List<Object> argumentsList = new ArrayList<>();
        argumentsList.add(type);
        argumentsList.add(params);
        methodChannel.invokeMethod("callBack", argumentsList);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            initNotificationManager();
            if (!notificationManager.areBubblesAllowed()) {
                Log.e(TAG, "System Alert Window will not work without enabling the android bubbles");
                Toast.makeText(mContext, "System Alert Window will not work without enabling the android bubbles", Toast.LENGTH_LONG).show();
            } else {
                int devOptions = Settings.Secure.getInt(mContext.getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);
                if (devOptions == 1) {
                    Log.d(TAG, "Android bubbles are enabled");
                    return true;
                } else {
                    Log.e(TAG, "System Alert Window will not work without enabling the android bubbles");
                    Toast.makeText(mContext, "System Alert Window will not work without enabling the android bubbles in the developer options", Toast.LENGTH_LONG).show();
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(mContext)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + mContext.getPackageName()));
                if (mActivity == null) {
                    if (mContext != null) {
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
