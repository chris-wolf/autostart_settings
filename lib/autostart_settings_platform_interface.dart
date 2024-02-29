import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'autostart_settings_method_channel.dart' if (dart.library.html) 'web_autostart_settings_method_channel.dart';


abstract class AutostartSettingsPlatform extends PlatformInterface {
  /// Constructs a AutostartSettingsPlatform.
  AutostartSettingsPlatform() : super(token: _token);

  static final Object _token = Object();

  static AutostartSettingsPlatform _instance = MethodChannelAutostartSettings();

  /// The default instance of [AutostartSettingsPlatform] to use.
  ///
  /// Defaults to [MethodChannelAutostartSettings].
  static AutostartSettingsPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [AutostartSettingsPlatform] when
  /// they register themselves.
  static set instance(AutostartSettingsPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<bool> canOpen(
      {required bool autoStart, required bool batterySafer}) async {
    return false;
  }

  Future<bool> open(
      {required bool autoStart, required bool batterySafer}) async {
    return false;
  }
}
