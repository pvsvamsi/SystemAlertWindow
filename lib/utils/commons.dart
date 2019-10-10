import 'package:system_alert_window/system_alert_window.dart';

class Commons {
  static String getGravity(WindowGravity gravity) {
    switch (gravity) {
      case WindowGravity.CENTER:
        return "center";
      case WindowGravity.BOTTOM:
        return "bottom";
      case WindowGravity.TOP:
      default:
        return "top";
    }
  }

  static String getPosition(ButtonPosition buttonPosition) {
    switch (buttonPosition) {
      case ButtonPosition.LEADING:
        return "leading";
      case ButtonPosition.TRAILING:
        return "trailing";
      case ButtonPosition.CENTER:
      default:
        return "center";
    }
  }
}
