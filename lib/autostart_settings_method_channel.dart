import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'autostart_settings_platform_interface.dart';

/// An implementation of [AutostartSettingsPlatform] that uses method channels.
class MethodChannelAutostartSettings extends AutostartSettingsPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('autostart_settings');

  @override
  Future<bool> canOpen({required bool autoStart, required bool batterySafer}) async {
    return await methodChannel.invokeMethod<bool>('canOpen', {'autoStart': autoStart, 'batterySafer': batterySafer}) ?? false;
  }
  @override
  Future<bool> open({required bool autoStart, required bool batterySafer}) async {
    return await methodChannel.invokeMethod<bool>('open', {'autoStart': autoStart, 'batterySafer': batterySafer}) ?? false;
  }
}
