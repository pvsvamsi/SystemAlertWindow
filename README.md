# system_alert_window

A flutter plugin to show Truecaller like overlay window, over all other apps along with callback events.

## Android

### Permissions

      <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
      <uses-permission android:name="android.permission.FOREGROUND_SERVICE " />
    <uses-permission android:name="android.permission.WAKE_LOCK" />

#### Android 9 and below

Uses &#x27;draw on top&#x27; permission and displays it as a overlay window

#### Android 10 and above

Uses Android Bubble APIs to show the overlay window.


## IOS

Displays as a notification in the notification center [Help Needed]


## Example

### Show the overlay
          
      SystemWindowHeader header = SystemWindowHeader(
              title: SystemWindowText(text: "Incoming Call", fontSize: 10, textColor: Colors.black45),
              padding: SystemWindowPadding.setSymmetricPadding(12, 12),
              subTitle: SystemWindowText(text: "9898989899", fontSize: 14, fontWeight: FontWeight.BOLD, textColor: Colors.black87),
              decoration: SystemWindowDecoration(startColor: Colors.grey[100]),
            );
            
      SystemWindowFooter footer = SystemWindowFooter(
          button: SystemWindowButton(
            text: SystemWindowText(text: "I'm a button", fontSize: 12, textColor: Colors.white),
            tag: "footer_button", //Usefull to identify the callback events
            width: SystemWindowButton.MATCH_PARENT,
            height: SystemWindowButton.WRAP_CONTENT,
            decoration: SystemWindowDecoration(
                startColor: Color.fromRGBO(250, 139, 97, 1), endColor: Color.fromRGBO(247, 28, 88, 1), borderWidth: 0, borderRadius: 30.0),
          ),
          padding: SystemWindowPadding(left: 16, right: 16, bottom: 12),
          decoration: SystemWindowDecoration(startColor: Colors.white),
          buttonPosition: ButtonPosition.CENTER);
          
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
          gravity: SystemWindowGravity.TOP);
          
### Register callback events (like button click)

      SystemAlertWindow.registerCallBack((dynamic arguments) {
            String type = arguments[0];
            if (type == "onClick") {
              String tag = arguments[1];
              print("OnClick event of $tag");
            }
          });
          
### Close the overlay

      SystemAlertWindow.closeSystemWindow();