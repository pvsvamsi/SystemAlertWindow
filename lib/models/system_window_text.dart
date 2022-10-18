import 'package:flutter/material.dart';
import 'package:system_alert_window/system_alert_window.dart';
import 'package:system_alert_window/utils/commons.dart';

class SystemWindowText {
  /// Value of the text
  String text;
  /// Value of the font size of the text
  double? fontSize;
  /// Color of the text
  Color? textColor;
  /// Font weight of the text
  FontWeight? fontWeight;
  /// Padding for the text
  SystemWindowPadding? padding;

  SystemWindowText(
      {required this.text,
      this.fontSize,
      this.fontWeight,
      this.textColor,
      this.padding});

  /// Internal method to convert SystemWindowText to primitive dataTypes
  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{
      'text': text,
      'fontSize': fontSize ?? 14.0,
      'fontWeight': Commons.getFontWeight(fontWeight),
      'textColor':
          textColor?.value.toSigned(32) ?? Colors.black.value.toSigned(32),
      'padding': padding?.getMap(),
    };
    return map;
  }
}
