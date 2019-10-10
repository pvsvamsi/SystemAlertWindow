import 'package:flutter/material.dart';
import 'package:system_alert_window/models/window_margin.dart';
import 'package:system_alert_window/models/window_padding.dart';
import 'package:system_alert_window/models/window_text.dart';

class WindowButton {
  static const int MATCH_PARENT = -1;
  static const int WRAP_CONTENT = -2;

  WindowText text;
  WindowPadding padding;
  WindowMargin margin;
  int width;
  int height;
  int borderWidth;
  double borderRadius;
  Color borderColor;
  Color fillColor;

  WindowButton({@required this.text, this.padding, this.margin, this.width, this.height, this.borderWidth, this.borderRadius, this.borderColor, this.fillColor})
      : assert(text != null);

  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{
      'text': text.getMap(),
      'padding': padding?.getMap(),
      'margin': margin?.getMap(),
      'width': width ?? WRAP_CONTENT,
      'height': height ?? WRAP_CONTENT,
      'borderWidth': borderWidth ?? 1,
      'borderRadius': borderRadius ?? 30.0,
      'borderColor': borderColor?.value ?? Colors.black.value,
      'fillColor': fillColor?.value ?? Colors.white.value,
    };
    return map;
  }
}
