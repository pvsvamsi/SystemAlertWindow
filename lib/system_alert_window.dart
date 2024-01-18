import 'dart:async';
import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:system_alert_window/utils/commons.dart';
import 'package:system_alert_window/utils/constants.dart';

enum SystemWindowGravity { TOP, BOTTOM, CENTER }

enum SystemWindowPrefMode { DEFAULT, OVERLAY, BUBBLE }

class SystemAlertWindow {
  ///Channel name to handle the communication between flutter and platform specific code
  static const MethodChannel _channel = const MethodChannel(Constants.CHANNEL, JSONMethodCodec());

  static const BasicMessageChannel _overlayMessageChannel = BasicMessageChannel(Constants.MESSAGE_CHANNEL, JSONMessageCodec());

  static final StreamController _controller = StreamController();

  /// Fetches the current platform version
  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  /// Fetches the generated log file
  static Future<String?> get getLogFile async {
    return await _channel.invokeMethod('getLogFile');
  }


  /// Method to enable the logs. By default, logs are disabled
  static Future<void> enableLogs(bool flag) async {
    await _channel.invokeMethod('enableLogs', [flag]);
  }

  /// Check if system window permission is granted
  static Future<bool?> checkPermissions({SystemWindowPrefMode prefMode = SystemWindowPrefMode.DEFAULT}) async {
    return await _channel.invokeMethod('checkPermissions', [Commons.getSystemWindowPrefMode(prefMode)]);
  }

  /// Request the corresponding system window permission
  static Future<bool?> requestPermissions({SystemWindowPrefMode prefMode = SystemWindowPrefMode.DEFAULT}) async {
    return await _channel.invokeMethod('requestPermissions', [Commons.getSystemWindowPrefMode(prefMode)]);
  }

  /// Register your callbackFunction to receive click events
  /// Your callback function should be declared as a global function (Outside the scope of the class)
  /// Don't forget to add @pragma('vm:entry-point') above your global function
  static Future<bool> registerOnClickListener(Function callBackFunction) async {
    final callBackDispatcher = PluginUtilities.getCallbackHandle(callbackDispatcher);
    final callBack = PluginUtilities.getCallbackHandle(callBackFunction);
    await _channel.invokeMethod("registerCallBackHandler", <dynamic>[callBackDispatcher!.toRawHandle(), callBack!.toRawHandle()]);
    return true;
  }

  static Future<bool> removeOnClickListener() async {
    return await _channel.invokeMethod("removeCallBackHandler");
  }

  /// Show System Window
  ///
  /// `gravity` Position of the window and default is [SystemWindowGravity.CENTER]
  /// `width` Width of the window and default is [Constants.MATCH_PARENT]
  /// `height` Height of the window and default is [Constants.WRAP_CONTENT]
  /// `notificationTitle` Notification title, applicable in case of bubble
  /// `notificationBody` Notification body, applicable in case of bubble
  /// `prefMode` Preference for the system window. Default is [SystemWindowPrefMode.DEFAULT]
  /// `isDisableClicks` Disables the clicks across the system window. Default is false. This is not applicable for bubbles.
  static Future<bool?> showSystemWindow(
      {SystemWindowGravity gravity = SystemWindowGravity.CENTER,
      int? width,
      int? height,
      String notificationTitle = "Title",
      String notificationBody = "Body",
      SystemWindowPrefMode prefMode = SystemWindowPrefMode.DEFAULT,
      bool isDisableClicks = false}) async {
    final Map<String, dynamic> params = <String, dynamic>{
      'gravity': Commons.getWindowGravity(gravity),
      'width': width ?? Constants.MATCH_PARENT,
      'height': height ?? Constants.WRAP_CONTENT,
      'isDisableClicks': isDisableClicks
    };
    return await _channel.invokeMethod('showSystemWindow', [notificationTitle, notificationBody, params, Commons.getSystemWindowPrefMode(prefMode)]);
  }

  /// Update System Window
  ///
  /// `gravity` Position of the window and default is [SystemWindowGravity.CENTER]
  /// `width` Width of the window and default is [Constants.MATCH_PARENT]
  /// `height` Height of the window and default is [Constants.WRAP_CONTENT]
  /// `notificationTitle` Notification title, applicable in case of bubble
  /// `notificationBody` Notification body, applicable in case of bubble
  /// `prefMode` Preference for the system window. Default is [SystemWindowPrefMode.DEFAULT]
  /// `isDisableClicks` Disables the clicks across the system window. Default is false. This is not applicable for bubbles.
  static Future<bool?> updateSystemWindow(
      {SystemWindowGravity gravity = SystemWindowGravity.CENTER,
      int? width,
      int? height,
      String notificationTitle = "Title",
      String notificationBody = "Body",
      SystemWindowPrefMode prefMode = SystemWindowPrefMode.DEFAULT,
      bool isDisableClicks = false}) async {
    final Map<String, dynamic> params = <String, dynamic>{
      'gravity': Commons.getWindowGravity(gravity),
      'width': width ?? Constants.MATCH_PARENT,
      'height': height ?? Constants.WRAP_CONTENT,
      'isDisableClicks': isDisableClicks
    };
    return await _channel
        .invokeMethod('updateSystemWindow', [notificationTitle, notificationBody, params, Commons.getSystemWindowPrefMode(prefMode)]);
  }

  /// Broadcast data to and from overlay app
  static Future sendMessageToOverlay(dynamic data) async {
    return await _overlayMessageChannel.send(data);
  }

  static Stream<dynamic> get overlayListener {
    _overlayMessageChannel.setMessageHandler((message) async {
      _controller.add(message);
      return message;
    });
    return _controller.stream;
  }

  static void disposeOverlayListener() {
    _controller.close();
  }

  /// Closes the system window
  static Future<bool?> closeSystemWindow({SystemWindowPrefMode prefMode = SystemWindowPrefMode.DEFAULT}) async {
    return await _channel.invokeMethod('closeSystemWindow', [Commons.getSystemWindowPrefMode(prefMode)]);
  }
}

/// Global function to handle the callbacks in background isolate
@pragma('vm:entry-point')
void callbackDispatcher() {
  // 1. Initialize MethodChannel used to communicate with the platform portion of the plugin
  const MethodChannel _backgroundChannel = const MethodChannel(Constants.BACKGROUND_CHANNEL, JSONMethodCodec());
  // 2. Setup internal state needed for MethodChannels.
  WidgetsFlutterBinding.ensureInitialized();

  // 3. Listen for background events from the platform portion of the plugin.
  _backgroundChannel.setMethodCallHandler((MethodCall call) async {
    final args = call.arguments;
    // 3.1. Retrieve callback instance for handle.
    final Function callback = PluginUtilities.getCallbackFromHandle(CallbackHandle.fromRawHandle(args[0]))!;
    final type = args[1];
    if (type == "onClick") {
      final tag = args[2];
      // 3.2. Invoke callback.
      callback(tag);
    }
  });
}

extension HexColor on Color {
  String _generateAlpha({required int alpha, required bool withAlpha}) {
    if (withAlpha) {
      return alpha.toRadixString(16).padLeft(2, '0');
    } else {
      return '';
    }
  }

  /// Extension method for Color to generate Hex code
  String toHex({bool leadingHashSign = false, bool withAlpha = false}) => '${leadingHashSign ? '#' : ''}'
          '${_generateAlpha(alpha: alpha, withAlpha: withAlpha)}'
          '${red.toRadixString(16).padLeft(2, '0')}'
          '${green.toRadixString(16).padLeft(2, '0')}'
          '${blue.toRadixString(16).padLeft(2, '0')}'
      .toUpperCase();
}
