import 'package:flutter/material.dart';
import 'package:system_alert_window/models/window_padding.dart';
import 'package:system_alert_window/models/window_text.dart';

class WindowBody {
  List<EachRow> rows;
  WindowPadding padding;
  Color backgroundColor;

  WindowBody({this.rows, this.padding, this.backgroundColor});

  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{
      'rows': (rows == null) ? null : List<dynamic>.from(rows.map((x) => x?.getMap())),
      'padding': padding?.getMap(),
      'bgColor': backgroundColor?.value ?? Colors.white.value
    };
    return map;
  }
}

class EachRow {
  List<EachColumn> columns;
  WindowPadding padding;

  EachRow({this.columns, this.padding});

  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{
      'columns': (columns == null) ? null : List<dynamic>.from(columns.map((x) => x?.getMap())),
      'padding': padding?.getMap()
    };
    return map;
  }
}

class EachColumn {
  WindowText text;
  WindowPadding padding;

  EachColumn({this.text, this.padding});

  Map<String, dynamic> getMap() {
    final Map<String, dynamic> map = <String, dynamic>{'text': text?.getMap(), 'padding': padding?.getMap()};
    return map;
  }
}
