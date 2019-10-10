import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:system_alert_window/models/window_body.dart';
import 'package:system_alert_window/models/window_footer.dart';
import 'package:system_alert_window/models/window_header.dart';
import 'package:system_alert_window/models/window_margin.dart';
import 'package:system_alert_window/utils/commons.dart';

enum WindowGravity { TOP, BOTTOM, CENTER }

enum ButtonPosition { TRAILING, LEADING, CENTER }

enum FontWeight { NORMAL, BOLD, ITALIC, BOLD_ITALIC }

class SystemAlertWindow {
  static const MethodChannel _channel = const MethodChannel('system_alert_window');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<void> get checkPermissions async {
    await _channel.invokeMethod('checkPermissions');
  }

  static registerCallBack(callBackFunction) {
    _channel.setMethodCallHandler((MethodCall call) {
      switch (call.method) {
        case "callBack":
          dynamic arguments = call.arguments;
          if (arguments is List) {
            return callBackFunction(arguments);
          } else
            return null;
      }
      return null;
    });
  }

  static Future<bool> showSystemWindow({
    @required WindowHeader header,
    @required WindowBody body,
    @required WindowFooter footer,
    WindowMargin margin,
    WindowGravity gravity = WindowGravity.CENTER,
    int width,
    int height,
  }) async {
    assert(header != null && body != null && footer != null);
    final Map<String, dynamic> params = <String, dynamic>{
      'header': header.getMap(),
      'body': body.getMap(),
      'footer': footer.getMap(),
      'margin': margin?.getMap(),
      'gravity': Commons.getGravity(gravity),
      'width': width ?? 0,
      'height': height ?? 0
    };
    return await _channel.invokeMethod('showSystemWindow', params);
  }

  static Future<bool> cancel() async {
    return await _channel.invokeMethod("cancel");
  }
}
