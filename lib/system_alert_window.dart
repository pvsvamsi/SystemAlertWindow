import 'dart:async';
import 'package:flutter/services.dart';
import 'package:system_alert_window/utils/commons.dart';
import 'package:system_alert_window/utils/constants.dart';

enum SystemWindowGravity { TOP, BOTTOM, CENTER, LEADING, TRAILING }

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

  /// Show System Window
  ///
  /// `gravity` Position of the window and default is [SystemWindowGravity.CENTER]
  /// `width` Width of the window and default is [Constants.MATCH_PARENT]
  /// `height` Height of the window and default is [Constants.WRAP_CONTENT]
  /// `notificationTitle` Notification title, applicable in case of bubble
  /// `notificationBody` Notification body, applicable in case of bubble
  /// `prefMode` Preference for the system window. Default is [SystemWindowPrefMode.DEFAULT]
  /// `isDisableClicks` Disables the clicks across the system window. Default is false. This is not applicable for bubbles.
  /// `isFlagFocusable` Makes the overlay window focusable. Default is false.
  static Future<bool?> showSystemWindow(
      {SystemWindowGravity gravity = SystemWindowGravity.CENTER,
      int? width,
      int? height,
      String notificationTitle = "Title",
      String notificationBody = "Body",
      SystemWindowPrefMode prefMode = SystemWindowPrefMode.DEFAULT,
      bool isDisableClicks = false,
      bool isFlagFocusable = false}) async {
    final Map<String, dynamic> params = <String, dynamic>{
      'gravity': Commons.getWindowGravity(gravity),
      'width': width ?? Constants.MATCH_PARENT,
      'height': height ?? Constants.WRAP_CONTENT,
      'isDisableClicks': isDisableClicks,
      'isFlagFocusable': isFlagFocusable
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
  /// `isFlagFocusable`Makes the overlay window focusable. Default is false.
  static Future<bool?> updateSystemWindow(
      {SystemWindowGravity gravity = SystemWindowGravity.CENTER,
      int? width,
      int? height,
      String notificationTitle = "Title",
      String notificationBody = "Body",
      SystemWindowPrefMode prefMode = SystemWindowPrefMode.DEFAULT,
      bool isDisableClicks = false,
      bool isFlagFocusable = false}) async {
    final Map<String, dynamic> params = <String, dynamic>{
      'gravity': Commons.getWindowGravity(gravity),
      'width': width ?? Constants.MATCH_PARENT,
      'height': height ?? Constants.WRAP_CONTENT,
      'isDisableClicks': isDisableClicks,
      'isFlagFocusable': isFlagFocusable
    };
    return await _channel
        .invokeMethod('updateSystemWindow', [notificationTitle, notificationBody, params, Commons.getSystemWindowPrefMode(prefMode)]);
  }

  /// Broadcast data to system window
  static Future sendMessageToOverlay(dynamic data) async {
    return await _overlayMessageChannel.send(data);
  }

  /// Read data in  system window
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

  static Future<bool?> isBubbleMode({SystemWindowPrefMode prefMode = SystemWindowPrefMode.DEFAULT}) async {
    return await _channel.invokeMethod('isBubbleMode', [Commons.getSystemWindowPrefMode(prefMode)]);
  }
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
