import 'package:system_alert_window/system_alert_window.dart';
import 'package:system_alert_window/utils/commons.dart';

class SystemWindowFooter {
  /// Text of the system window footer
  SystemWindowText? text;

  /// Padding for the system window footer
  SystemWindowPadding? padding;

  /// List of buttons to be shown in the system window footer
  List<SystemWindowButton>? buttons;

  /// Position of the button in system window footer
  ButtonPosition? buttonsPosition;

  /// Decoration of the system window footer
  SystemWindowDecoration? decoration;

  SystemWindowFooter(
      {this.text,
      this.padding,
      this.buttons,
      this.buttonsPosition,
      this.decoration});

  /// Internal method to convert SystemWindowFooter to primitive dataTypes
  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{
      'isShowFooter':
          (text != null || (buttons != null && buttons!.length > 0)),
      'text': text?.getMap(),
      'buttons': (buttons == null)
          ? null
          : List<Map<String, dynamic>>.from(
              buttons!.map((button) => button.getMap())),
      'buttonsPosition': Commons.getPosition(buttonsPosition),
      'padding': padding?.getMap(),
      'decoration': decoration?.getMap()
    };
    return map;
  }
}
