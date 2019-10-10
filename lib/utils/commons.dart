import 'package:system_alert_window/system_alert_window.dart';

class Commons {
  static String getGravity(WindowGravity gravity) {
    if (gravity == null) gravity = WindowGravity.TOP;
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
    if (buttonPosition == null) buttonPosition = ButtonPosition.CENTER;
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

  static String getFontWeight(FontWeight fontWeight) {
    if (fontWeight == null) fontWeight = FontWeight.NORMAL;
    switch (fontWeight) {
      case FontWeight.BOLD:
        return "bold";
      case FontWeight.ITALIC:
        return "italic";
      case FontWeight.BOLD_ITALIC:
        return "bold_italic";
      case FontWeight.NORMAL:
      default:
        return "normal";
    }
  }
}
