import 'package:flutter/material.dart';
import 'package:system_alert_window/models/window_padding.dart';
import 'package:system_alert_window/system_alert_window.dart';
import 'package:system_alert_window/utils/commons.dart';

class WindowText {
  String text;
  double fontSize;
  Color textColor;
  FontWeight fontWeight;
  WindowPadding padding;

  WindowText({@required this.text, this.fontSize, this.fontWeight, this.textColor, this.padding}) : assert(text != null);

  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{
      'text': text,
      'fontSize': fontSize ?? 14.0,
      'fontWeight': Commons.getFontWeight(fontWeight),
      'textColor': textColor?.value ?? Colors.black.value,
      'padding': padding?.getMap(),
    };
    return map;
  }
}
