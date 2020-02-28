package in.jvapps.system_alert_window_example;

import android.os.Bundle;

import in.jvapps.system_alert_window.SystemAlertWindowPlugin;
import io.flutter.app.FlutterApplication;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class Application extends FlutterApplication implements PluginRegistry.PluginRegistrantCallback {

    @Override
    public void onCreate() {
        super.onCreate();
        SystemAlertWindowPlugin.setPluginRegistrant(this);
    }

    @Override
    public void registerWith(PluginRegistry pluginRegistry) {
        GeneratedPluginRegistrant.registerWith(pluginRegistry);
    }

}