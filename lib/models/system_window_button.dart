import 'package:system_alert_window/models/system_window_decoration.dart';
import 'package:system_alert_window/models/system_window_margin.dart';
import 'package:system_alert_window/models/system_window_padding.dart';
import 'package:system_alert_window/models/system_window_text.dart';

class SystemWindowButton {
  /// Use this to provide the value as match parent
  static const int MATCH_PARENT = -1;

  /// Use this to provide the value as wrap content
  static const int WRAP_CONTENT = -2;

  /// Text of the system window button
  SystemWindowText text;

  /// Padding of the system window button
  SystemWindowPadding? padding;

  /// Margin of the system window button
  SystemWindowMargin? margin;

  /// Width of the system window button
  int? width;

  /// Height of the system window button
  int? height;

  /// Tag of the system window button. Used to identify the click event
  String tag;

  /// Decoration of the system window button
  SystemWindowDecoration? decoration;

  SystemWindowButton(
      {required this.text,
      required this.tag,
      this.padding,
      this.margin,
      this.width,
      this.height,
      this.decoration});

  /// Internal method to convert SystemWindowButton to primitive dataTypes
  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{
      'text': text.getMap(),
      'tag': tag,
      'padding': padding?.getMap(),
      'margin': margin?.getMap(),
      'width': width ?? WRAP_CONTENT,
      'height': height ?? WRAP_CONTENT,
      'decoration': decoration?.getMap()
    };
    return map;
  }
}
