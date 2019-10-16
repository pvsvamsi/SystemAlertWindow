import 'package:system_alert_window/models/window_button.dart';
import 'package:system_alert_window/models/window_decoration.dart';
import 'package:system_alert_window/models/window_padding.dart';
import 'package:system_alert_window/models/window_text.dart';
import 'package:system_alert_window/system_alert_window.dart';
import 'package:system_alert_window/utils/commons.dart';

class WindowFooter {
  WindowText text;
  WindowPadding padding;
  WindowButton button;
  ButtonPosition buttonPosition;
  WindowDecoration decoration;

  WindowFooter(
      {this.text,
      this.padding,
      this.button,
      this.buttonPosition,
      this.decoration});

  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{
      'isShowFooter': (text != null || button != null),
      'text': text?.getMap(),
      'button': button?.getMap(),
      'buttonPosition': Commons.getPosition(buttonPosition),
      'padding': padding?.getMap(),
      'decoration': decoration?.getMap()
    };
    return map;
  }
}
