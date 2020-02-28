## 0.2.1

* Added support for background dispatch of click events. So that the click events are not missed, in case the app is destroyed while displaying the overlay window.

## 0.2.0 - Breaking Changes

* Added support for multiple buttons in the footer. Please refer to the updated example

## 0.1.5+1

* Fixed the casting issue in onClick callback.

## 0.1.5 - Breaking Changes

* Refactored the code and replaced registerCallBack with registerOnClickListener. Please refer to the updated example.

## 0.1.4+6

* Now body and footer are optional

## 0.1.4+5

* Fixed issue with ButtonPosition.TRAILING alignment

## 0.1.4+4

* Fixed issue with bubble configuration on Android Q

## 0.1.4+3

* Fixed minimum required API version for bubbles + Optimized the exports of systemAlertWindow models

## 0.1.4+2

* Fixed an issue where overlay window is crashing during update overlay window call

## 0.1.4+1

* Fixed an issue where multiple overlay windows are creating during update overlay window call

## 0.1.4

* Fixed overlay window issue with older Android versions

## 0.1.3+1

* Fixed a crash with updateSystemWindow method for android 9 and below

## 0.1.3

* Fixed a bug in closeSystemWindow method
* Improved permissions checking logic
* Added support for updating the system alert window

## 0.1.2

* Moved bubble notification to foreground service, to allow the bubble to display in all cases
* Fixed the launcher icons of the example

## 0.1.1+1

* Added logging + Made minor changes to fix crashes with bubbles

## 0.1.1 - Breaking Changes

* Renamed the models to avoid conflicts with the native libraries

## 0.1.0

* Added support to close the system alert window
* Improved the permissions API to include support for Android 10

## 0.0.1

* Initial release (Working only for Android)
