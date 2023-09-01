import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:system_alert_window/system_alert_window.dart';

class TrueCallerOverlay extends StatefulWidget {
  @override
  State<TrueCallerOverlay> createState() => _TrueCallerOverlayState();
}

class _TrueCallerOverlayState extends State<TrueCallerOverlay> {
  bool show =true;
  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    SystemAlertWindow.overlayListener.listen((event) {
      log("$event");
      if(event == "update system window"){
        setState(() {
          show = false;
        });
      }else{
        setState(() {
          show = true;
        });
      }
    });
  }
  SystemWindowPrefMode prefMode = SystemWindowPrefMode.OVERLAY;
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
                  child: Text("Update")),
              show?ElevatedButton(
                  onPressed: () async {
                    await SystemAlertWindow.closeSystemWindow(prefMode: prefMode);
                  },
                  child: Text("close")):Text("cant")
            ],
          )),
    );
  }
}
