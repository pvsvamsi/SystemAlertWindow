import 'package:system_alert_window/system_alert_window.dart';

class Commons {
  ///Converts SystemWindowGravity to string format
  static String getWindowGravity(SystemWindowGravity gravity) {
    //if (gravity == null) gravity = SystemWindowGravity.TOP;
    switch (gravity) {
      case SystemWindowGravity.CENTER:
        return "center";
      case SystemWindowGravity.BOTTOM:
        return "bottom";
      case SystemWindowGravity.TRAILING:
        return "trailing";
      case SystemWindowGravity.LEADING:
        return "leading";
      case SystemWindowGravity.TOP:
      default:
        return "top";
    }
  }

  ///Converts SystemWindowPrefMode to string format
  static String getSystemWindowPrefMode(SystemWindowPrefMode prefMode) {
    //if (prefMode == null) prefMode = SystemWindowPrefMode.DEFAULT;
    switch (prefMode) {
      case SystemWindowPrefMode.OVERLAY:
        return "overlay";
      case SystemWindowPrefMode.BUBBLE:
        return "bubble";
      case SystemWindowPrefMode.DEFAULT:
      default:
        return "default";
    }
  }
}
