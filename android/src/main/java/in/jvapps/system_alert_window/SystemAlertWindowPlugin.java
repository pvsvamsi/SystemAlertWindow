package in.jvapps.system_alert_window;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import in.jvapps.system_alert_window.utils.Commons;
import in.jvapps.system_alert_window.utils.Constants;
import in.jvapps.system_alert_window.utils.ContextHolder;
import in.jvapps.system_alert_window.utils.LogUtils;
import io.flutter.FlutterInjector;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.FlutterEngineGroup;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.JSONMessageCodec;

public class SystemAlertWindowPlugin implements FlutterPlugin, ActivityAware, BasicMessageChannel.MessageHandler {

    private boolean isInitialized;

    @Nullable
    private ActivityPluginBinding pluginBinding;
    MethodCallHandlerImpl methodCallHandler;
    private final String TAG = "SAW:Plugin";
    private Context context;
    private BasicMessageChannel<Object> messenger;


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
        this.context = flutterPluginBinding.getApplicationContext();
        initialize(flutterPluginBinding);
        messenger = new BasicMessageChannel<>(flutterPluginBinding.getBinaryMessenger(), Constants.MESSAGE_CHANNEL,
                JSONMessageCodec.INSTANCE);
        messenger.setMessageHandler(this);
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
                try {
                    FlutterEngine existingEngine = FlutterEngineCache.getInstance().get(Constants.FLUTTER_CACHE_ENGINE);
                    if(existingEngine==null){
                    FlutterEngineGroup enn = new FlutterEngineGroup(context);
                    DartExecutor.DartEntrypoint dEntry = new DartExecutor.DartEntrypoint(
                            FlutterInjector.instance().flutterLoader().findAppBundlePath(),
                            "overlayMain");
                    FlutterEngine engine = enn.createAndRunEngine(context, dEntry);
                    FlutterEngineCache.getInstance().put(Constants.FLUTTER_CACHE_ENGINE, engine);
                    }
                } catch (Exception e) {
                    LogUtils.getInstance().e(TAG, "Error initializing FlutterEngine: " + e.getMessage());
                }

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

    @Override
    public void onMessage(@Nullable Object message, @NonNull BasicMessageChannel.Reply reply) {
        BasicMessageChannel overlayMessageChannel = new BasicMessageChannel(
                FlutterEngineCache.getInstance().get(Constants.FLUTTER_CACHE_ENGINE)
                        .getDartExecutor(),
                Constants.MESSAGE_CHANNEL, JSONMessageCodec.INSTANCE);
        overlayMessageChannel.send(message, reply);
    }
}
