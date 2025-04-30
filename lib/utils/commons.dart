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

  static String flagToJson(SystemWindowFlags flag) {
    switch (flag) {
      case SystemWindowFlags.FLAG_NOT_FOCUSABLE:
        return "FLAG_NOT_FOCUSABLE";
      case SystemWindowFlags.FLAG_NOT_TOUCH_MODAL:
        return "FLAG_NOT_TOUCH_MODAL";
      case SystemWindowFlags.FLAG_NOT_TOUCHABLE:
        return "FLAG_NOT_TOUCHABLE";
    }
  }

  static List<String> flagsToJson(List<SystemWindowFlags> flags) {
    return flags.map((flag) => flagToJson(flag)).toList();
  }
}
