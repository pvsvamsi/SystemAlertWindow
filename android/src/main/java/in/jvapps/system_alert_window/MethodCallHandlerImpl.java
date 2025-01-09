package in.jvapps.system_alert_window;

import static in.jvapps.system_alert_window.services.WindowServiceNew.INTENT_EXTRA_IS_CLOSE_WINDOW;
import static in.jvapps.system_alert_window.services.WindowServiceNew.INTENT_EXTRA_IS_UPDATE_WINDOW;
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
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import in.jvapps.system_alert_window.services.WindowServiceNew;
import in.jvapps.system_alert_window.utils.Commons;
import in.jvapps.system_alert_window.utils.Constants;
import in.jvapps.system_alert_window.utils.ContextHolder;
import in.jvapps.system_alert_window.utils.LogUtils;
import in.jvapps.system_alert_window.utils.NotificationHelper;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.JSONMethodCodec;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

public class MethodCallHandlerImpl implements MethodChannel.MethodCallHandler, PluginRegistry.ActivityResultListener {

    private static final String TAG = "SAW:MethodCallHandlerImpl";
    public int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1237;

    @Nullable
    private Activity mActivity;

    @Nullable
    private MethodChannel channel;

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        try {
            LogUtils.getInstance().d(TAG, "On method call " + call.method);
            Context mContext = ContextHolder.getApplicationContext();
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
                case "isBubbleMode":
                    arguments = (JSONArray) call.arguments;
                    prefMode = (String) arguments.get(0);
                    if (prefMode == null) {
                        prefMode = "default";
                    }
                    result.success(isBubbleMode(prefMode));
                    break;
                default:
                    result.notImplemented();
            }
        } catch (Exception ex) {
            LogUtils.getInstance().e(TAG, ex.toString());
        }
    }

    /**
     * Registers this instance as a method call handler on the given {@code messenger}.
     *
     * <p>Stops any previously started and unstopped calls.
     *
     * <p>This should be cleaned with {@link #stopListening} once the messenger is disposed of.
     */
    void startListening(BinaryMessenger messenger) {
        if (channel != null) {
            LogUtils.getInstance().w(TAG, "Setting a method call handler before the last was disposed.");
            stopListening();
        }

        channel = new MethodChannel(messenger, Constants.CHANNEL, JSONMethodCodec.INSTANCE);
        channel.setMethodCallHandler(this);
    }

    /**
     * Clears this instance from listening to method calls.
     *
     * <p>Does nothing if {@link #startListening} hasn't been called, or if we're already stopped.
     */
    void stopListening() {
        if (channel == null) {
            LogUtils.getInstance().d(TAG, "Tried to stop listening when no MethodChannel had been initialized.");
            return;
        }

        channel.setMethodCallHandler(null);
        channel = null;
    }

    void setActivity(@Nullable Activity activity) {
        this.mActivity = activity;
    }


    private boolean isBubbleMode(String prefMode) {
        boolean isPreferOverlay = "overlay".equalsIgnoreCase(prefMode);
        return Commons.isForceAndroidBubble(ContextHolder.getApplicationContext()) ||
                (!isPreferOverlay && ("bubble".equalsIgnoreCase(prefMode) || Build.VERSION.SDK_INT >= Build.VERSION_CODES.R));
    }

    public boolean askPermission(boolean isOverlay) {
        Context mContext = ContextHolder.getApplicationContext();
        if (mContext != null) {
            if (!isOverlay && (Commons.isForceAndroidBubble(mContext) || Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)) {
                return NotificationHelper.getInstance(mContext).areBubblesAllowed();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(mContext)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + mContext.getPackageName()));
                    if (mActivity == null) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        Toast.makeText(mContext, "Please grant, Can Draw Over Other Apps permission.", Toast.LENGTH_SHORT).show();
                        LogUtils.getInstance().e(TAG, "Can't detect the permission change, as the mActivity is null");

                    } else {
                        mActivity.startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
                    }
                } else {
                    return true;
                }
            }
        } else {
            LogUtils.getInstance().e(TAG, "'Can Draw Over Other Apps' permission is not requested as context is null");
        }
        return false;
    }

    public boolean checkPermission(boolean isOverlay) {
        Context mContext = ContextHolder.getApplicationContext();
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
        Context mContext = ContextHolder.getApplicationContext();
        Icon icon = Icon.createWithResource(mContext, R.drawable.ic_notification);
        NotificationHelper notificationHelper = NotificationHelper.getInstance(mContext);
        notificationHelper.showNotification(icon, title, body, params);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            Context mContext = ContextHolder.getApplicationContext();
            if (!Settings.canDrawOverlays(mContext)) {
                LogUtils.getInstance().e(TAG, "System Alert Window will not work without 'Can Draw Over Other Apps' permission");
                Toast.makeText(mContext, "System Alert Window will not work without 'Can Draw Over Other Apps' permission", Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

}
