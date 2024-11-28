import 'dart:async';
import 'dart:developer';
import 'dart:isolate';
import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:share_plus/share_plus.dart';
import 'package:system_alert_window/system_alert_window.dart';
import 'custom_overlay.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(MyApp());
}

@pragma("vm:entry-point")
void overlayMain() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(
    MaterialApp(
      debugShowCheckedModeBanner: false,
      home: CustomOverlay(),
    ),
  );
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  bool _isShowingWindow = false;
  bool _isUpdatedWindow = false;
  SystemWindowPrefMode prefMode = SystemWindowPrefMode.OVERLAY;
  static const String _mainAppPort = 'MainApp';
  final _receivePort = ReceivePort();
  SendPort? homePort;
  String? latestMessageFromOverlay;

  @override
  void initState() {
    super.initState();
    _initPlatformState();
    _requestPermissions();
    if (homePort != null) return;
    final res = IsolateNameServer.registerPortWithName(
      _receivePort.sendPort,
      _mainAppPort,
    );
    log("$res: OVERLAY");
    _receivePort.listen((message) {
      log("message from OVERLAY: $message");
    });
  }

  @override
  void dispose() {
    super.dispose();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> _initPlatformState() async {
    await SystemAlertWindow.enableLogs(true);
    String? platformVersion;
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
    if (platformVersion != null)
      setState(() {
        _platformVersion = platformVersion!;
      });
  }

  Future<void> _requestPermissions() async {
    await SystemAlertWindow.requestPermissions(prefMode: prefMode);
  }

  void _showOverlayWindow() async {
    if (!_isShowingWindow) {
      await SystemAlertWindow.sendMessageToOverlay('show system window');
      SystemAlertWindow.showSystemWindow(
          height: 200,
          width: MediaQuery.of(context).size.width.floor(),
          gravity: SystemWindowGravity.CENTER,
          prefMode: prefMode,
          isFlagFocusable: false);
      setState(() {
        _isShowingWindow = true;
      });
    } else if (!_isUpdatedWindow) {
      await SystemAlertWindow.sendMessageToOverlay('update system window');
      SystemAlertWindow.updateSystemWindow(
          height: 200,
          width: MediaQuery.of(context).size.width.floor(),
          gravity: SystemWindowGravity.CENTER,
          prefMode: prefMode,
          isFlagFocusable: true,
          isDisableClicks: true);
      setState(() {
        _isUpdatedWindow = true;
        SystemAlertWindow.sendMessageToOverlay(_isUpdatedWindow);
      });
    } else {
      setState(() {
        _isShowingWindow = false;
        _isUpdatedWindow = false;
        SystemAlertWindow.sendMessageToOverlay(_isUpdatedWindow);
      });
      SystemAlertWindow.closeSystemWindow(prefMode: prefMode);
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('System Alert Window Example App \n with flutterview'),
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
                  child: !_isShowingWindow
                      ? Text("Show system alert window")
                      : !_isUpdatedWindow
                          ? Text("Update system alert window")
                          : Text("Close system alert window"),
                  color: Colors.deepOrange,
                  padding: const EdgeInsets.symmetric(vertical: 8.0),
                ),
              ),
              Padding(
                padding: const EdgeInsets.symmetric(vertical: 8.0),
                child: MaterialButton(
                  onPressed: () => SystemAlertWindow.sendMessageToOverlay("message from main"),
                  textColor: Colors.white,
                  child: Text("send message to overlay"),
                  color: Colors.deepOrange,
                  padding: const EdgeInsets.symmetric(vertical: 8.0),
                ),
              ),
              TextButton(
                  onPressed: () async {
                    String? logFilePath = await SystemAlertWindow.getLogFile;
                    if (logFilePath != null && logFilePath.isNotEmpty) {
                      Share.shareFiles([logFilePath]);
                    } else {
                      print("Path is empty");
                    }
                  },
                  child: Text("Share Log file"))
            ],
          ),
        ),
      ),
    );
  }
}
