import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:system_alert_window/system_alert_window.dart';
import 'package:system_alert_window/models/system_window_body.dart';
import 'package:system_alert_window/models/system_window_text.dart';
import 'package:system_alert_window/models/system_window_footer.dart';
import 'package:system_alert_window/models/system_window_button.dart';
import 'package:system_alert_window/models/system_window_decoration.dart';
import 'package:system_alert_window/models/system_window_padding.dart';
import 'package:system_alert_window/models/system_window_margin.dart';
import 'package:system_alert_window/models/system_window_header.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  bool _isShowingWindow = false;

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
    if (!_isShowingWindow) {
      SystemWindowHeader header = SystemWindowHeader(
        title: SystemWindowText(text: "Incoming Call", fontSize: 10, textColor: Colors.black45),
        padding: SystemWindowPadding.setSymmetricPadding(12, 12),
        subTitle: SystemWindowText(text: "9898989899", fontSize: 14, fontWeight: FontWeight.BOLD, textColor: Colors.black87),
        decoration: SystemWindowDecoration(startColor: Colors.grey[100]),
      );
      SystemWindowBody body = SystemWindowBody(
        rows: [
          EachRow(
            columns: [
              EachColumn(
                text: SystemWindowText(text: "Some body", fontSize: 12, textColor: Colors.black45),
              ),
            ],
            gravity: ContentGravity.CENTER,
          ),
          EachRow(columns: [
            EachColumn(
                text: SystemWindowText(text: "Long data of the body", fontSize: 12, textColor: Colors.black87, fontWeight: FontWeight.BOLD),
                padding: SystemWindowPadding.setSymmetricPadding(6, 8),
                decoration: SystemWindowDecoration(startColor: Colors.black12, borderRadius: 25.0),
                margin: SystemWindowMargin(top: 4)),
          ], gravity: ContentGravity.CENTER),
          EachRow(
            columns: [
              EachColumn(
                text: SystemWindowText(text: "Notes", fontSize: 10, textColor: Colors.black45),
              ),
            ],
            gravity: ContentGravity.LEFT,
            margin: SystemWindowMargin(top: 8),
          ),
          EachRow(
            columns: [
              EachColumn(
                text: SystemWindowText(text: "Some random notes.", fontSize: 13, textColor: Colors.black54, fontWeight: FontWeight.BOLD),
              ),
            ],
            gravity: ContentGravity.LEFT,
          ),
        ],
        padding: SystemWindowPadding(left: 16, right: 16, bottom: 12, top: 12),
      );
      SystemWindowFooter footer = SystemWindowFooter(
          button: SystemWindowButton(
            text: SystemWindowText(text: "I'm a button", fontSize: 12, textColor: Colors.white),
            tag: "footer_button",
            width: SystemWindowButton.MATCH_PARENT,
            height: SystemWindowButton.WRAP_CONTENT,
            decoration: SystemWindowDecoration(
                startColor: Color.fromRGBO(250, 139, 97, 1), endColor: Color.fromRGBO(247, 28, 88, 1), borderWidth: 0, borderRadius: 30.0),
          ),
          padding: SystemWindowPadding(left: 16, right: 16, bottom: 12),
          decoration: SystemWindowDecoration(startColor: Colors.white),
          buttonPosition: ButtonPosition.CENTER);
      SystemAlertWindow.showSystemWindow(
          height: 230,
          header: header,
          body: body,
          footer: footer,
          margin: SystemWindowMargin(left: 8, right: 8, top: 200, bottom: 0),
          gravity: SystemWindowGravity.TOP);
      _isShowingWindow = true;
    }else{
      /*SystemWindowHeader header = SystemWindowHeader(
        title: SystemWindowText(text: "Incoming Call", fontSize: 10, textColor: Colors.black45),
        padding: SystemWindowPadding.setSymmetricPadding(12, 12),
        subTitle: SystemWindowText(text: "9898989899", fontSize: 14, fontWeight: FontWeight.BOLD, textColor: Colors.black87),
        decoration: SystemWindowDecoration(startColor: Colors.grey[100]),
      );
      SystemWindowBody body = SystemWindowBody(
        rows: [
          EachRow(
            columns: [
              EachColumn(
                text: SystemWindowText(text: "Some body", fontSize: 12, textColor: Colors.black45),
              ),
            ],
            gravity: ContentGravity.CENTER,
          ),
          EachRow(columns: [
            EachColumn(
                text: SystemWindowText(text: "Long data of the body", fontSize: 12, textColor: Colors.black87, fontWeight: FontWeight.BOLD),
                padding: SystemWindowPadding.setSymmetricPadding(6, 8),
                decoration: SystemWindowDecoration(startColor: Colors.black12, borderRadius: 25.0),
                margin: SystemWindowMargin(top: 4)),
          ], gravity: ContentGravity.CENTER),
          EachRow(
            columns: [
              EachColumn(
                text: SystemWindowText(text: "Notes", fontSize: 10, textColor: Colors.black45),
              ),
            ],
            gravity: ContentGravity.LEFT,
            margin: SystemWindowMargin(top: 8),
          ),
          EachRow(
            columns: [
              EachColumn(
                text: SystemWindowText(text: "Some random notes.", fontSize: 13, textColor: Colors.black54, fontWeight: FontWeight.BOLD),
              ),
            ],
            gravity: ContentGravity.LEFT,
          ),
        ],
        padding: SystemWindowPadding(left: 16, right: 16, bottom: 12, top: 12),
      );
      SystemWindowFooter footer = SystemWindowFooter(
          button: SystemWindowButton(
            text: SystemWindowText(text: "I'm a button", fontSize: 12, textColor: Colors.white),
            tag: "footer_button",
            width: SystemWindowButton.MATCH_PARENT,
            height: SystemWindowButton.WRAP_CONTENT,
            decoration: SystemWindowDecoration(
                startColor: Color.fromRGBO(250, 139, 97, 1), endColor: Color.fromRGBO(247, 28, 88, 1), borderWidth: 0, borderRadius: 30.0),
          ),
          padding: SystemWindowPadding(left: 16, right: 16, bottom: 12),
          decoration: SystemWindowDecoration(startColor: Colors.white),
          buttonPosition: ButtonPosition.CENTER);
      SystemAlertWindow.showSystemWindow(
          height: 230,
          header: header,
          body: body,
          footer: footer,
          margin: SystemWindowMargin(left: 8, right: 8, top: 200, bottom: 0),
          gravity: SystemWindowGravity.TOP);*/
      SystemAlertWindow.closeSystemWindow();
      _isShowingWindow = false;
    }
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
