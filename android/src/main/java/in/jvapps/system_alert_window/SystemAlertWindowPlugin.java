package in.jvapps.system_alert_window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import in.jvapps.system_alert_window.utils.ContextHolder;
import in.jvapps.system_alert_window.utils.LogUtils;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;

public class SystemAlertWindowPlugin implements FlutterPlugin, ActivityAware {

    private boolean isInitialized;

    @Nullable
    private ActivityPluginBinding pluginBinding;
    MethodCallHandlerImpl methodCallHandler;
    private final String TAG = "SAW:Plugin";

    public SystemAlertWindowPlugin() {
        LogUtils.getInstance().d(TAG, "Initializing the constructor");
        isInitialized = false;
    }

    private void initialize(FlutterPluginBinding binding) {
        ContextHolder.setApplicationContext(binding.getApplicationContext());
        if (!isInitialized) {
            isInitialized = true;
            LogUtils.getInstance().d(TAG, "Initializing on attached to engine");
            if (methodCallHandler == null) {
                methodCallHandler = new MethodCallHandlerImpl();
                methodCallHandler.startListening(binding.getBinaryMessenger());
            }
            LogUtils.getInstance().d(TAG, "onAttachedToEngine");
        }
    }

    private void registerListeners() {
        if (pluginBinding != null) {
            pluginBinding.addActivityResultListener(methodCallHandler);
        }
    }

    private void deregisterListeners() {
        if (pluginBinding != null) {
            pluginBinding.removeActivityResultListener(methodCallHandler);
        }
    }

    private void dispose() {
        LogUtils.getInstance().d(TAG, "Disposing call track plugin class");
        if (methodCallHandler != null) {
            methodCallHandler.stopListening();
            methodCallHandler.setActivity(null);
            methodCallHandler = null;
        }
        isInitialized = false;
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        initialize(flutterPluginBinding);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        if (!isInitialized) {
            LogUtils.getInstance().d(TAG, "Already detached from the engine.");
            return;
        }
        LogUtils.getInstance().d(TAG, "On detached from engine");
        dispose();
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding activityPluginBinding) {
        LogUtils.getInstance().d(TAG, "Initializing on attached to activity");
        if (methodCallHandler != null) {
            methodCallHandler.setActivity(activityPluginBinding.getActivity());
        }
        this.pluginBinding = activityPluginBinding;
        registerListeners();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity();
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding activityPluginBinding) {
        onAttachedToActivity(activityPluginBinding);
    }

    @Override
    public void onDetachedFromActivity() {
        LogUtils.getInstance().d(TAG, "On detached from activity");
        if (methodCallHandler != null) {
            methodCallHandler.setActivity(null);
        }
        deregisterListeners();
    }
}
