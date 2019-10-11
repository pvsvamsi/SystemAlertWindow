import 'package:flutter/material.dart';

class WindowDecoration {
  Color backgroundColor;
  int borderWidth;
  double borderRadius;
  Color borderColor;

  WindowDecoration({this.backgroundColor, this.borderWidth, this.borderRadius, this.borderColor});

  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{
      'bgColor': backgroundColor?.value ?? Colors.white.value,
      'borderWidth': borderWidth ?? 0,
      'borderRadius': borderRadius ?? 0.0,
      'borderColor': borderColor?.value ?? Colors.white.value
    };
    return map;
  }
}
