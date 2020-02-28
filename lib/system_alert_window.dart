import 'dart:async';
import 'dart:ui';

export 'models/system_window_body.dart';
export 'models/system_window_button.dart';
export 'models/system_window_decoration.dart';
export 'models/system_window_footer.dart';
export 'models/system_window_header.dart';
export 'models/system_window_margin.dart';
export 'models/system_window_padding.dart';
export 'models/system_window_text.dart';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:system_alert_window/models/system_window_body.dart';
import 'package:system_alert_window/models/system_window_footer.dart';
import 'package:system_alert_window/models/system_window_header.dart';
import 'package:system_alert_window/models/system_window_margin.dart';
import 'package:system_alert_window/utils/commons.dart';
import 'package:system_alert_window/utils/constants.dart';

enum SystemWindowGravity { TOP, BOTTOM, CENTER }

enum ContentGravity { LEFT, RIGHT, CENTER }

enum ButtonPosition { TRAILING, LEADING, CENTER }

enum FontWeight { NORMAL, BOLD, ITALIC, BOLD_ITALIC }

typedef void OnClickListener(String tag);

class SystemAlertWindow {
  static const MethodChannel _channel = const MethodChannel(Constants.CHANNEL);

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<void> get checkPermissions async {
    await _channel.invokeMethod('checkPermissions');
  }

  static Future<bool> registerOnClickListener(
      OnClickListener callBackFunction) async {
    final callBackDispatcher =
        PluginUtilities.getCallbackHandle(callbackDispatcher);
    final callBack = PluginUtilities.getCallbackHandle(callBackFunction);
    _channel.setMethodCallHandler((MethodCall call) {
      print("Got callback");
      switch (call.method) {
        case "callBack":
          dynamic arguments = call.arguments;
          if (arguments is List) {
            final type = arguments[0];
            if (type == "onClick") {
              final tag = arguments[1];
              callBackFunction(tag);
            }
          }
      }
      return null;
    });
    await _channel.invokeMethod("registerCallBackHandler",
        <dynamic>[callBackDispatcher.toRawHandle(), callBack.toRawHandle()]);
    return true;
  }

  static Future<bool> showSystemWindow({
    @required SystemWindowHeader header,
    SystemWindowBody body,
    SystemWindowFooter footer,
    SystemWindowMargin margin,
    SystemWindowGravity gravity = SystemWindowGravity.CENTER,
    int width,
    int height,
  }) async {
    assert(header != null);
    final Map<String, dynamic> params = <String, dynamic>{
      'header': header.getMap(),
      'body': body?.getMap(),
      'footer': footer?.getMap(),
      'margin': margin?.getMap(),
      'gravity': Commons.getWindowGravity(gravity),
      'width': width ?? Constants.MATCH_PARENT,
      'height': height ?? Constants.WRAP_CONTENT
    };
    return await _channel.invokeMethod('showSystemWindow', params);
  }

  static Future<bool> updateSystemWindow({
    @required SystemWindowHeader header,
    SystemWindowBody body,
    SystemWindowFooter footer,
    SystemWindowMargin margin,
    SystemWindowGravity gravity = SystemWindowGravity.CENTER,
    int width,
    int height,
  }) async {
    assert(header != null);
    final Map<String, dynamic> params = <String, dynamic>{
      'header': header.getMap(),
      'body': body?.getMap(),
      'footer': footer?.getMap(),
      'margin': margin?.getMap(),
      'gravity': Commons.getWindowGravity(gravity),
      'width': width ?? Constants.MATCH_PARENT,
      'height': height ?? Constants.WRAP_CONTENT
    };
    return await _channel.invokeMethod('updateSystemWindow', params);
  }

  static Future<bool> closeSystemWindow() async {
    return await _channel.invokeMethod('closeSystemWindow');
  }
}

void callbackDispatcher() {
  // 1. Initialize MethodChannel used to communicate with the platform portion of the plugin
  const MethodChannel _backgroundChannel =
      const MethodChannel(Constants.BACKGROUND_CHANNEL);
  // 2. Setup internal state needed for MethodChannels.
  WidgetsFlutterBinding.ensureInitialized();

  // 3. Listen for background events from the platform portion of the plugin.
  _backgroundChannel.setMethodCallHandler((MethodCall call) async {
    final args = call.arguments;
    // 3.1. Retrieve callback instance for handle.
    final Function callback = PluginUtilities.getCallbackFromHandle(
        CallbackHandle.fromRawHandle(args[0]));
    assert(callback != null);
    final type = args[1];
    if (type == "onClick") {
      final tag = args[2];
      // 3.2. Invoke callback.
      callback(tag);
    }
  });
}
