#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#
Pod::Spec.new do |s|
  s.name             = 'system_alert_window'
  s.version          = '0.0.1'
  s.summary          = 'A flutter plugin to show overlay window on top of everything in Android. This plugin uses &#x27;draw on top&#x27; permission to work on Android platform. For IOS platform, it displays as a notification in the notification center.'
  s.description      = <<-DESC
A flutter plugin to show overlay window on top of everything in Android. This plugin uses &#x27;draw on top&#x27; permission to work on Android platform. For IOS platform, it displays as a notification in the notification center.
                       DESC
  s.homepage         = 'http://example.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Your Company' => 'email@example.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'

  s.ios.deployment_target = '8.0'
end

