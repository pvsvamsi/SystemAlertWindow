import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:system_alert_window/system_alert_window.dart';

void main() {
  const MethodChannel channel = MethodChannel('system_alert_window');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        if (methodCall.method == 'getPlatformVersion') {
          return '42';
        }
        return null;
      },
    );
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(channel, null);
  });

  test('getPlatformVersion', () async {
    expect(await SystemAlertWindow.platformVersion, '42');
  });
}
