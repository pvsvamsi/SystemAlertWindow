import 'package:flutter/material.dart';
import 'package:system_alert_window/system_alert_window.dart';
import 'package:system_alert_window/utils/commons.dart';

class SystemWindowHeader {
  @required
  SystemWindowText? title;
  SystemWindowText? subTitle;
  SystemWindowButton? button;
  SystemWindowPadding? padding;
  ButtonPosition? buttonPosition;
  SystemWindowDecoration? decoration;

  SystemWindowHeader(
      {this.title,
      this.subTitle,
      this.button,
      this.padding,
      this.buttonPosition,
      this.decoration});

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
