import 'dart:async';

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
    _channel.setMethodCallHandler((MethodCall call) {
      switch (call.method) {
        case "callBack":
          dynamic arguments = call.arguments;
          if (arguments is List) {
            final type = arguments[0].cast<String>();
            if (type == "onClick") {
              final tag = arguments[1].cast<String>();
              callBackFunction(tag);
            }
          }
      }
      return null;
    });
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
