import 'package:system_alert_window/system_alert_window.dart';
import 'package:system_alert_window/utils/commons.dart';

class SystemWindowFooter {
  SystemWindowText? text;
  SystemWindowPadding? padding;
  List<SystemWindowButton>? buttons;
  ButtonPosition? buttonsPosition;
  SystemWindowDecoration? decoration;

  SystemWindowFooter(
      {this.text,
      this.padding,
      this.buttons,
      this.buttonsPosition,
      this.decoration});

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
