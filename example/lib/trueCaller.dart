import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:system_alert_window/system_alert_window.dart';

class TrueCallerOverlay extends StatefulWidget {
  @override
  State<TrueCallerOverlay> createState() => _TrueCallerOverlayState();
}

class _TrueCallerOverlayState extends State<TrueCallerOverlay> {
  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    SystemAlertWindow.overlayListener.listen((event) {
      log("$event");
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
          color: Colors.lightBlue,
          child: Column(
            children: [
              Center(child: Text("aryaveer")),
              ElevatedButton(
                  onPressed: () async {
                    await SystemAlertWindow.shareData('update');
                  },
                  child: Text("Update"))
            ],
          )),
    );
  }
}
