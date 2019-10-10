import 'package:flutter/material.dart';
import 'package:system_alert_window/models/window_padding.dart';

class WindowText {
  String text;
  double fontSize;
  Color textColor;
  WindowPadding padding;

  WindowText({@required this.text, this.fontSize, this.textColor, this.padding}) : assert(text != null);

  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{
      'text': text,
      'fontSize': fontSize ?? 14.0,
      'textColor': textColor?.value ?? Colors.black.value,
      'padding': padding?.getMap(),
    };
    return map;
  }
}
