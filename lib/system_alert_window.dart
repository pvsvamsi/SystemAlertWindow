import 'dart:async';
import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:system_alert_window/models/system_window_body.dart';
import 'package:system_alert_window/models/system_window_footer.dart';
import 'package:system_alert_window/models/system_window_header.dart';
import 'package:system_alert_window/models/system_window_margin.dart';
import 'package:system_alert_window/utils/commons.dart';
import 'package:system_alert_window/utils/constants.dart';

export 'models/system_window_body.dart';
export 'models/system_window_button.dart';
export 'models/system_window_decoration.dart';
export 'models/system_window_footer.dart';
export 'models/system_window_header.dart';
export 'models/system_window_margin.dart';
export 'models/system_window_padding.dart';
export 'models/system_window_text.dart';

enum SystemWindowGravity { TOP, BOTTOM, CENTER }

enum ContentGravity { LEFT, RIGHT, CENTER }

enum ButtonPosition { TRAILING, LEADING, CENTER }

enum FontWeight { NORMAL, BOLD, ITALIC, BOLD_ITALIC }

enum SystemWindowPrefMode { DEFAULT, OVERLAY, BUBBLE }

class SystemAlertWindow {
  ///Channel name to handle the communication between flutter and platform specific code
  static const MethodChannel _channel =
      const MethodChannel(Constants.CHANNEL, JSONMethodCodec());

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
  static Future<bool?> checkPermissions(
      {SystemWindowPrefMode prefMode = SystemWindowPrefMode.DEFAULT}) async {
    return await _channel.invokeMethod(
        'checkPermissions', [Commons.getSystemWindowPrefMode(prefMode)]);
  }

  /// Request the corresponding system window permission
  static Future<bool?> requestPermissions(
      {SystemWindowPrefMode prefMode = SystemWindowPrefMode.DEFAULT}) async {
    return await _channel.invokeMethod(
        'requestPermissions', [Commons.getSystemWindowPrefMode(prefMode)]);
  }

  /// Register your callbackFunction to receive click events
  /// Your callback function should be declared as a global function (Outside the scope of the class)
  /// Don't forget to add @pragma('vm:entry-point') above your global function
  static Future<bool> registerOnClickListener(Function callBackFunction) async {
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
      return Future.value(null);
    });
    await _channel.invokeMethod("registerCallBackHandler",
        <dynamic>[callBackDispatcher!.toRawHandle(), callBack!.toRawHandle()]);
    return true;
  }

  /// Show System Window
  ///
  /// `header` Header content of the window
  /// `body` Body content of the window
  /// `footer` Footer content of the window
  /// `margin` Margin for the window
  /// `gravity` Position of the window and default is [SystemWindowGravity.CENTER]
  /// `width` Width of the window and default is [Constants.MATCH_PARENT]
  /// `height` Height of the window and default is [Constants.WRAP_CONTENT]
  /// `notificationTitle` Notification title, applicable in case of bubble
  /// `notificationBody` Notification body, applicable in case of bubble
  /// `prefMode` Preference for the system window. Default is [SystemWindowPrefMode.DEFAULT]
  /// `backgroundColor` Background color for the system window. Default is [Colors.white]. This will be the default background color for header, body, footer
  /// `isDisableClicks` Disables the clicks across the system window. Default is false. This is not applicable for bubbles.
  static Future<bool?> showSystemWindow(
      {required SystemWindowHeader header,
      SystemWindowBody? body,
      SystemWindowFooter? footer,
      SystemWindowMargin? margin,
      SystemWindowGravity gravity = SystemWindowGravity.CENTER,
      int? width,
      int? height,
      String notificationTitle = "Title",
      String notificationBody = "Body",
      SystemWindowPrefMode prefMode = SystemWindowPrefMode.DEFAULT,
      Color backgroundColor = Colors.white,
      bool isDisableClicks = false}) async {
    final Map<String, dynamic> params = <String, dynamic>{
      'header': header.getMap(),
      'body': body?.getMap(),
      'footer': footer?.getMap(),
      'margin': margin?.getMap(),
      'gravity': Commons.getWindowGravity(gravity),
      'width': width ?? Constants.MATCH_PARENT,
      'height': height ?? Constants.WRAP_CONTENT,
      'bgColor': backgroundColor.toHex(leadingHashSign: true, withAlpha: true),
      'isDisableClicks': isDisableClicks
    };
    return await _channel.invokeMethod('showSystemWindow', [
      notificationTitle,
      notificationBody,
      params,
      Commons.getSystemWindowPrefMode(prefMode)
    ]);
  }

  /// Update System Window
  ///
  /// `header` Header content of the window
  /// `body` Body content of the window
  /// `footer` Footer content of the window
  /// `margin` Margin for the window
  /// `gravity` Position of the window and default is [SystemWindowGravity.CENTER]
  /// `width` Width of the window and default is [Constants.MATCH_PARENT]
  /// `height` Height of the window and default is [Constants.WRAP_CONTENT]
  /// `notificationTitle` Notification title, applicable in case of bubble
  /// `notificationBody` Notification body, applicable in case of bubble
  /// `prefMode` Preference for the system window. Default is [SystemWindowPrefMode.DEFAULT]
  /// `backgroundColor` Background color for the system window. Default is [Colors.white]. This will be the default background color for header, body, footer
  /// `isDisableClicks` Disables the clicks across the system window. Default is false. This is not applicable for bubbles.
  static Future<bool?> updateSystemWindow(
      {required SystemWindowHeader header,
      SystemWindowBody? body,
      SystemWindowFooter? footer,
      SystemWindowMargin? margin,
      SystemWindowGravity gravity = SystemWindowGravity.CENTER,
      int? width,
      int? height,
      String notificationTitle = "Title",
      String notificationBody = "Body",
      SystemWindowPrefMode prefMode = SystemWindowPrefMode.DEFAULT,
      Color backgroundColor = Colors.white,
      bool isDisableClicks = false}) async {
    final Map<String, dynamic> params = <String, dynamic>{
      'header': header.getMap(),
      'body': body?.getMap(),
      'footer': footer?.getMap(),
      'margin': margin?.getMap(),
      'gravity': Commons.getWindowGravity(gravity),
      'width': width ?? Constants.MATCH_PARENT,
      'height': height ?? Constants.WRAP_CONTENT,
      'bgColor': backgroundColor.toHex(leadingHashSign: true, withAlpha: true),
      'isDisableClicks': isDisableClicks
    };
    return await _channel.invokeMethod('updateSystemWindow', [
      notificationTitle,
      notificationBody,
      params,
      Commons.getSystemWindowPrefMode(prefMode)
    ]);
  }

  /// Closes the system window
  static Future<bool?> closeSystemWindow(
      {SystemWindowPrefMode prefMode = SystemWindowPrefMode.DEFAULT}) async {
    return await _channel.invokeMethod(
        'closeSystemWindow', [Commons.getSystemWindowPrefMode(prefMode)]);
  }
}

/// Global function to handle the callbacks in background isolate
@pragma('vm:entry-point')
void callbackDispatcher() {
  // 1. Initialize MethodChannel used to communicate with the platform portion of the plugin
  const MethodChannel _backgroundChannel =
      const MethodChannel(Constants.BACKGROUND_CHANNEL, JSONMethodCodec());
  // 2. Setup internal state needed for MethodChannels.
  WidgetsFlutterBinding.ensureInitialized();

  // 3. Listen for background events from the platform portion of the plugin.
  _backgroundChannel.setMethodCallHandler((MethodCall call) async {
    final args = call.arguments;
    // 3.1. Retrieve callback instance for handle.
    final Function callback = PluginUtilities.getCallbackFromHandle(
        CallbackHandle.fromRawHandle(args[0]))!;
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
  String toHex({bool leadingHashSign = false, bool withAlpha = false}) =>
      '${leadingHashSign ? '#' : ''}'
              '${_generateAlpha(alpha: alpha, withAlpha: withAlpha)}'
              '${red.toRadixString(16).padLeft(2, '0')}'
              '${green.toRadixString(16).padLeft(2, '0')}'
              '${blue.toRadixString(16).padLeft(2, '0')}'
          .toUpperCase();
}
