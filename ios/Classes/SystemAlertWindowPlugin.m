#import "SystemAlertWindowPlugin.h"
#import <system_alert_window/system_alert_window-Swift.h>

@implementation SystemAlertWindowPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftSystemAlertWindowPlugin registerWithRegistrar:registrar];
}
@end
