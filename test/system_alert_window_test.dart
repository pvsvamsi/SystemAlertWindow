import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:system_alert_window/system_alert_window.dart';

void main() {
  const MethodChannel channel = MethodChannel('system_alert_window');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await SystemAlertWindow.platformVersion, '42');
  });
}
