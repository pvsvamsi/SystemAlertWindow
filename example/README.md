# system_alert_window_example

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