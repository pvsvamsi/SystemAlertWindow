import 'package:system_alert_window/system_alert_window.dart';

class Commons {
  static String getWindowGravity(SystemWindowGravity gravity) {
    if (gravity == null) gravity = SystemWindowGravity.TOP;
    switch (gravity) {
      case SystemWindowGravity.CENTER:
        return "center";
      case SystemWindowGravity.BOTTOM:
        return "bottom";
      case SystemWindowGravity.TOP:
      default:
        return "top";
    }
  }

  static String getContentGravity(ContentGravity gravity) {
    if (gravity == null) gravity = ContentGravity.LEFT;
    switch (gravity) {
      case ContentGravity.CENTER:
        return "center";
      case ContentGravity.RIGHT:
        return "right";
      case ContentGravity.LEFT:
      default:
        return "left";
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
