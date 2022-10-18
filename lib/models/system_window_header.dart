import 'package:flutter/material.dart';
import 'package:system_alert_window/system_alert_window.dart';
import 'package:system_alert_window/utils/commons.dart';

class SystemWindowHeader {

  /// Title of the system window header
  @required SystemWindowText? title;
  /// Sub title of the system window header
  SystemWindowText? subTitle;
  /// Button in the system window header
  SystemWindowButton? button;
  /// Padding for the system window header
  SystemWindowPadding? padding;
  /// Button position in the system window header
  ButtonPosition? buttonPosition;
  /// Decoration of the system window header
  SystemWindowDecoration? decoration;

  SystemWindowHeader(
      {this.title,
      this.subTitle,
      this.button,
      this.padding,
      this.buttonPosition,
      this.decoration});

  /// Internal method to convert SystemWindowHeader to primitive dataTypes
  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{
      'title': title?.getMap(),
      'subTitle': subTitle?.getMap(),
      'button': button?.getMap(),
      'padding': padding?.getMap(),
      'buttonPosition': Commons.getPosition(buttonPosition),
      'decoration': decoration?.getMap()
    };
    return map;
  }
}
