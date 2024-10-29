import 'dart:developer';
import 'dart:isolate';
import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:system_alert_window/system_alert_window.dart';

class CustomOverlay extends StatefulWidget {
  @override
  State<CustomOverlay> createState() => _CustomOverlayState();
}

class _CustomOverlayState extends State<CustomOverlay> {
  static const String _mainAppPort = 'MainApp';
  SendPort? mainAppPort;
  bool update = false;
  @override
  void initState() {
    // TODO: implement initState
    super.initState();

    SystemAlertWindow.overlayListener.listen((event) {
      log("$event in overlay");
      if (event is bool) {
        setState(() {
          update = event;
        });
      }
    });
  }

  void callBackFunction(String tag) {
    print("Got tag " + tag);
    mainAppPort ??= IsolateNameServer.lookupPortByName(
      _mainAppPort,
    );
    mainAppPort?.send('Date: ${DateTime.now()}');
    mainAppPort?.send(tag);
  }

  Widget overlay() {
    return Container(
      height: MediaQuery.of(context).size.height,
      width: MediaQuery.of(context).size.width,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Container(
            height: 60,
            color: Colors.grey[100],
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Padding(
                  padding: const EdgeInsets.all(12.0),
                  child: Column(
                    children: [
                      Text(update ? "outgoing" : "Incoming", style: TextStyle(fontSize: 10, color: Colors.black45)),
                      Text(
                        "123456",
                        style: TextStyle(fontSize: 14, color: Colors.black87, fontWeight: FontWeight.bold),
                      ),
                    ],
                  ),
                ),
                TextButton(
                  style: ButtonStyle(
                    overlayColor: MaterialStateProperty.all(Colors.transparent),
                  ),
                  onPressed: () {
                    callBackFunction("Close");
                    SystemAlertWindow.closeSystemWindow(prefMode: prefMode);
                  },
                  child: Container(
                    width: MediaQuery.of(context).size.width / 2.3,
                    margin: EdgeInsets.only(left: 30),
                    padding: EdgeInsets.all(10),
                    decoration: BoxDecoration(borderRadius: BorderRadius.all(Radius.circular(30)), color: update ? Colors.grey : Colors.deepOrange),
                    child: Center(
                      child: Text(
                        "Close",
                        style: TextStyle(color: Colors.white, fontSize: 12),
                      ),
                    ),
                  ),
                )
              ],
            ),
          ),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 20),
            child: Text(
              update ? "clicks Disabled" : "Body",
              style: TextStyle(fontSize: 16, color: Colors.black54),
            ),
          ),
          TextButton(
            style: ButtonStyle(
              overlayColor: MaterialStateProperty.all(Colors.transparent),
            ),
            onPressed: () {
              callBackFunction("Action");
            },
            child: Container(
              padding: EdgeInsets.all(12),
              height: (MediaQuery.of(context).size.height) / 3.5,
              width: MediaQuery.of(context).size.width / 1.05,
              decoration: BoxDecoration(borderRadius: BorderRadius.all(Radius.circular(5)), color: update ? Colors.grey : Colors.deepOrange),
              child: Center(
                child: Text(
                  "Action",
                  style: TextStyle(fontSize: 14, color: Colors.white),
                ),
              ),
            ),
          )
        ],
      ),
    );
  }

  SystemWindowPrefMode prefMode = SystemWindowPrefMode.OVERLAY;
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: overlay(),
    );
  }
}
