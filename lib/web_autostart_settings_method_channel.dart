import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'autostart_settings_platform_interface.dart';

/// Not implemented for web
class MethodChannelAutostartSettings extends AutostartSettingsPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('autostart_settings');

  @override
  Future<bool> canOpen({required bool autoStart, required bool batterySafer}) async {
    return false;
  }
  @override
  Future<bool> open({required bool autoStart, required bool batterySafer}) async {
    return false;
  }
}
