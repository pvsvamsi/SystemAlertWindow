import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:system_alert_window/system_alert_window.dart';
import 'package:system_alert_window/models/window_body.dart';
import 'package:system_alert_window/models/window_text.dart';
import 'package:system_alert_window/models/window_footer.dart';
import 'package:system_alert_window/models/window_button.dart';
import 'package:system_alert_window/models/window_decoration.dart';
import 'package:system_alert_window/models/window_padding.dart';
import 'package:system_alert_window/models/window_margin.dart';
import 'package:system_alert_window/models/window_header.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    _initPlatformState();
    _checkPermissions();
    _registerCallBack();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> _initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await SystemAlertWindow.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  Future<void> _checkPermissions() async {
    await SystemAlertWindow.checkPermissions;
  }

  void _registerCallBack() {
    SystemAlertWindow.registerCallBack((dynamic arguments) {
      String type = arguments[0];
      if (type == "onClick") {
        String tag = arguments[1];
        print("OnClick event of $tag");
      }
    });
  }

  void _showOverlayWindow() {
    WindowHeader header = WindowHeader(
      title: WindowText(text: "Incoming Call", fontSize: 10, textColor: Colors.black45),
      padding: WindowPadding.setSymmetricPadding(8, 8),
      subTitle: WindowText(text: "9898989899", fontSize: 14, fontWeight: FontWeight.BOLD, textColor: Colors.black87),
      decoration: WindowDecoration(backgroundColor: Colors.grey[100]),
    );
    WindowBody body = WindowBody(rows: [
      EachRow(columns: [EachColumn(text: WindowText(text: "Some body", fontSize: 12, textColor: Colors.black87))])
    ], padding: WindowPadding.setSymmetricPadding(8, 8));
    WindowFooter footer = WindowFooter(
        button: WindowButton(
            text: WindowText(text: "I'm a button", fontSize: 12, textColor: Colors.white),
            tag: "footer_button",
            width: WindowButton.MATCH_PARENT,
            height: WindowButton.WRAP_CONTENT,
            decoration: WindowDecoration(backgroundColor: Colors.deepOrange, borderWidth: 0, borderRadius: 30.0),),
        padding: WindowPadding(left: 8, right: 8, top: 8, bottom: 8),
        decoration: WindowDecoration(backgroundColor: Colors.white),
        buttonPosition: ButtonPosition.CENTER);
    SystemAlertWindow.showSystemWindow(
        header: header, body: body, footer: footer, margin: WindowMargin(left: 8, right: 8, top: 100, bottom: 0), gravity: WindowGravity.TOP);
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: <Widget>[
              Text('Running on: $_platformVersion\n'),
              Padding(
                padding: const EdgeInsets.symmetric(vertical: 8.0),
                child: MaterialButton(
                  onPressed: _showOverlayWindow,
                  textColor: Colors.white,
                  child: Text("Show overlay"),
                  color: Colors.deepOrange,
                  padding: const EdgeInsets.symmetric(vertical: 8.0),
                ),
              )
            ],
          ),
        ),
      ),
    );
  }
}
