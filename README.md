# system_alert_window
[![Pub](https://img.shields.io/pub/v/system_alert_window.svg)](https://pub.dartlang.org/packages/system_alert_window)

A flutter plugin to show Truecaller like overlay window, over all other apps along with callback events. Android Go or Android 11 & above, this plugin shows notification bubble, in other android versions, it shows an overlay window.

## Android

### Demo
###### 1. Clip of example app and 2. Working of button click in the background
<img src="https://github.com/pvsvamsi/SystemAlertWindow/raw/master/assets/images/example%20demo.gif" width="300" height="570">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://github.com/pvsvamsi/SystemAlertWindow/raw/master/assets/images/background%20button%20click.gif" width="300" height="570">

### Application Class

#### JAVA (Application.java)

      import android.os.Bundle;
      import in.jvapps.system_alert_window.SystemAlertWindowPlugin;
      import io.flutter.app.FlutterApplication;
      import io.flutter.plugin.common.PluginRegistry;
      import io.flutter.plugins.GeneratedPluginRegistrant;

      public class Application extends FlutterApplication implements PluginRegistry.PluginRegistrantCallback {

          @Override
          public void onCreate() {
              super.onCreate();
              //This is required as we are using background channel for dispatching click events
              SystemAlertWindowPlugin.setPluginRegistrant(this);
          }

          @Override
          public void registerWith(PluginRegistry pluginRegistry) {
              GeneratedPluginRegistrant.registerWith(pluginRegistry);
          }

      }

#### KOTLIN (Application.kt)

      import `in`.jvapps.system_alert_window.SystemAlertWindowPlugin
      import io.flutter.app.FlutterApplication
      import io.flutter.plugin.common.PluginRegistry
      import io.flutter.plugin.common.PluginRegistry.PluginRegistrantCallback
      class Application : FlutterApplication(), PluginRegistrantCallback {
          override fun onCreate() {
              super.onCreate()
              SystemAlertWindowPlugin.setPluginRegistrant(this)
          }

         override fun registerWith(registry: PluginRegistry) {
            SystemAlertWindowPlugin.registerWith(registry.registrarFor("in.jvapps.system_alert_window"));
         }
      }


### Manifest

      //Permissions
      <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
      <uses-permission android:name="android.permission.FOREGROUND_SERVICE " />
      <uses-permission android:name="android.permission.WAKE_LOCK" />


          <application
              //Linking the previously added application class
              android:name=".Application"
              android:label="system_alert_window_example"
              android:icon="@mipmap/ic_launcher">

#### Android 10 & below, Android GO (API 27)

Uses &#x27;draw on top&#x27; permission and displays it as a overlay window

#### Android 11 & above

User has to allow 'All conversations can bubble' in the notification settings of the app. Uses Android Bubble APIs to show the overlay window inside a notification bubble.

#### Android GO (API 29)

User has to manually enable bubbles from the developer options. Uses Android Bubble APIs to show the overlay window inside a notification bubble.


## IOS

Displays as a notification in the notification center [Help Needed]


## Example

### Request overlay permission
      await SystemAlertWindow.requestPermissions;

### Show the overlay
          
      SystemWindowHeader header = SystemWindowHeader(
          title: SystemWindowText(text: "Incoming Call", fontSize: 10, textColor: Colors.black45),
          padding: SystemWindowPadding.setSymmetricPadding(12, 12),
          subTitle: SystemWindowText(text: "9898989899", fontSize: 14, fontWeight: FontWeight.BOLD, textColor: Colors.black87),
          decoration: SystemWindowDecoration(startColor: Colors.grey[100]),
          button: SystemWindowButton(text: SystemWindowText(text: "Personal", fontSize: 10, textColor: Colors.black45), tag: "personal_btn"),
          buttonPosition: ButtonPosition.TRAILING);
            );
            
      SystemWindowFooter footer = SystemWindowFooter(
          buttons: [
            SystemWindowButton(
              text: SystemWindowText(text: "Simple button", fontSize: 12, textColor: Color.fromRGBO(250, 139, 97, 1)),
              tag: "simple_button", //useful to identify button click event
              padding: SystemWindowPadding(left: 10, right: 10, bottom: 10, top: 10),
              width: 0,
              height: SystemWindowButton.WRAP_CONTENT,
              decoration: SystemWindowDecoration(
              startColor: Colors.white, endColor: Colors.white, borderWidth: 0, borderRadius: 0.0),
             ),
            SystemWindowButton(
              text: SystemWindowText(text: "Focus button", fontSize: 12, textColor: Colors.white),
              tag: "focus_button",
              width: 0,
              padding: SystemWindowPadding(left: 10, right: 10, bottom: 10, top: 10),
              height: SystemWindowButton.WRAP_CONTENT,
              decoration: SystemWindowDecoration(
              startColor: Color.fromRGBO(250, 139, 97, 1), endColor: Color.fromRGBO(247, 28, 88, 1), borderWidth: 0, borderRadius: 30.0),
             )
          ],
          padding: SystemWindowPadding(left: 16, right: 16, bottom: 12),
          decoration: SystemWindowDecoration(startColor: Colors.white),
          buttonsPosition: ButtonPosition.CENTER);
          
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
              ],
              padding: SystemWindowPadding(left: 16, right: 16, bottom: 12, top: 12),
            );

      SystemAlertWindow.showSystemWindow(
          height: 230,
          header: header,
          body: body,
          footer: footer,
          margin: SystemWindowMargin(left: 8, right: 8, top: 100, bottom: 0),
          gravity: SystemWindowGravity.TOP,
          notificationTitle: "Incoming Call",
          notificationBody: "+1 646 980 4741");
          
### Register for onClick events (button click)

      SystemAlertWindow.registerOnClickListener(callBackFunction);

      ///
      /// As this callback function is called from background, it should be declared on the parent level
      /// Whenever a button is clicked, this method will be invoked with a tag (As tag is unique for every button, it helps in identifying the button).
      /// You can check for the tag value and perform the relevant action for the button click
      ///
      void callBackFunction(String tag) {
        switch(tag){
          case "simple_button":
            print("Simple button has been clicked");
            break;
          case "focus_button":
            print("Focus button has been clicked");
            break;
          case "personal_btn":
            print("Personal button has been clicked");
            break;
          default:
            print("OnClick event of $tag");
        }
      }
          
### Close the overlay

      SystemAlertWindow.closeSystemWindow();
      
### Isolate communication
##### Use this snippet, if you want the callbacks on your main thread, instead of handling them in an isolate (like mentioned above)

###### Create an isolate_manager.dart
```
import 'dart:isolate';

import 'dart:ui';

class IsolateManager{

  static const FOREGROUND_PORT_NAME = "foreground_port";

  static SendPort lookupPortByName() {
    return IsolateNameServer.lookupPortByName(FOREGROUND_PORT_NAME);
  }

  static bool registerPortWithName(SendPort port) {
    assert(port != null, "'port' cannot be null.");
    removePortNameMapping(FOREGROUND_PORT_NAME);
    return IsolateNameServer.registerPortWithName(port, FOREGROUND_PORT_NAME);
  }

  static bool removePortNameMapping(String name) {
    assert(name != null, "'name' cannot be null.");
    return IsolateNameServer.removePortNameMapping(name);
  }

}
```

###### While initializing system alert window in your code
```
    await SystemAlertWindow.checkPermissions;
    ReceivePort _port = ReceivePort();
    IsolateManager.registerPortWithName(_port.sendPort);
    _port.listen((dynamic callBackData) {
      String tag= callBackData[0];
      switch (tag) {
        case "personal_btn":
          print("Personal button click : Do what ever you want here. This is inside your application scope");
          break;
        case "simple_button":
          print("Simple button click : Do what ever you want here. This is inside your application scope");
          break;
        case "focus_button":
          print("Focus button click : Do what ever you want here. This is inside your application scope");
          break;
      }
    });
    SystemAlertWindow.registerOnClickListener(callBackFunction);
```

###### Now the callBackFunction should looks like 
```
bool callBackFunction(String tag) {
  print("Got tag " + tag);
  SendPort port = IsolateManager.lookupPortByName();
  port.send([tag]);
  return true;
}
```
