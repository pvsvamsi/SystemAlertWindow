import 'package:system_alert_window/models/window_decoration.dart';
import 'package:system_alert_window/models/window_margin.dart';
import 'package:system_alert_window/models/window_padding.dart';
import 'package:system_alert_window/models/window_text.dart';
import 'package:system_alert_window/system_alert_window.dart';
import 'package:system_alert_window/utils/commons.dart';

class WindowBody {
  List<EachRow> rows;
  WindowPadding padding;
  WindowDecoration decoration;

  WindowBody({this.rows, this.padding, this.decoration});

  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{
      'rows': (rows == null) ? null : List<dynamic>.from(rows.map((x) => x?.getMap())),
      'padding': padding?.getMap(),
      'decoration': decoration?.getMap()
    };
    return map;
  }
}

class EachRow {
  List<EachColumn> columns;
  WindowPadding padding;
  WindowMargin margin;
  ContentGravity gravity;
  WindowDecoration decoration;

  EachRow({this.columns, this.padding, this.margin, this.gravity, this.decoration});

  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{
      'columns': (columns == null) ? null : List<dynamic>.from(columns.map((x) => x?.getMap())),
      'padding': padding?.getMap(),
      'margin': margin?.getMap(),
      'gravity': Commons.getContentGravity(gravity),
      'decoration': decoration?.getMap()
    };
    return map;
  }
}

class EachColumn {
  WindowText text;
  WindowPadding padding;
  WindowMargin margin;
  WindowDecoration decoration;

  EachColumn({this.text, this.padding, this.margin, this.decoration});

  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{
      'text': text?.getMap(),
      'padding': padding?.getMap(),
      'margin': margin?.getMap(),
      'decoration': decoration?.getMap()
    };
    return map;
  }
}
