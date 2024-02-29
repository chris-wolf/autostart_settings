
import 'autostart_settings_platform_interface.dart';

class AutostartSettings {
  static Future<bool> canOpen({required bool autoStart, required bool batterySafer}) {
    return AutostartSettingsPlatform.instance.canOpen(autoStart: autoStart, batterySafer: batterySafer);
  }

  static Future<bool> open({required bool autoStart, required bool batterySafer}) {
    return AutostartSettingsPlatform.instance.open(autoStart: autoStart, batterySafer: batterySafer);
  }
}
