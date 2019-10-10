package in.jvapps.system_alert_window;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import in.jvapps.system_alert_window.services.WindowService;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import static in.jvapps.system_alert_window.utils.Constants.INTENT_EXTRA_PARAMS_MAP;

public class SystemAlertWindowPlugin extends Activity implements MethodCallHandler {

    Activity context;
    static MethodChannel methodChannel;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1237;

    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "system_alert_window");
        channel.setMethodCallHandler(new SystemAlertWindowPlugin(registrar.activity(), channel));
    }

    private SystemAlertWindowPlugin(Activity activity, MethodChannel newMethodChannel) {
        this.context = activity;
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
                checkPermission();
                result.success("checking permissions");
                break;
            case "showSystemWindow":
                WindowService.closeOverlayService();
                assert (call.arguments != null);
                @SuppressWarnings("unchecked")
                HashMap<String, Object> params = (HashMap<String, Object>) call.arguments;
                final Intent i = new Intent(context, WindowService.class);
                context.stopService(i);
                i.putExtra(INTENT_EXTRA_PARAMS_MAP, params);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        WindowService.enqueueWork(context, i);
                    }
                }, 500);
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
            if (!Settings.canDrawOverlays(context)) {
                // You don't have permission
                checkPermission();
            }
        }

    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + context.getPackageName()));
                context.startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }
}
