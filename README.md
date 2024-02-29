<?code-excerpt path-base="excerpts/packages/autostart_settings"?>

# Open autostart settings for many manufacturers. 

[![pub package](https://img.shields.io/pub/v/autostart_settings.svg)](https://pub.dev/packages/autostart_settings)

Many manufacturers disable apps from auto starting by default. This prevents scheduled local notifications or background tasks after the device got rebooted.

This plugin links the user to the settings page where the user can enable autostart permission for selected apps. It also can navigate the user to device specific battery safer permission, where the user can select apps that shouldn't be killed in the background.

Currently this plugin support a wide variety of manufacturers gathered over the years;
* Xiaomi / Redmi
* Oppo
* Vivo
* LeEco
* Mediatek
* IQOO
* Tecno
* Huawei
* Asus
* Meizu
* OnePlus
* HTC
* Nokia


|             | Android |
|-------------|---------|
| **Support** | 14+     |

## Installation

First, add `autostart_settings` as a [dependency in your pubspec.yaml file](https://flutter.dev/using-packages/).

##  Usage

```
import 'package:autostart_settings/autostart_settings.dart';

final canOpen = await AutostartSettings.canOpen(autoStart: true, batterySafer: true);
if (canOpen) {
    final opened = await AutostartSettings.open(autoStart: true, batterySafer: true);
}

```

## Example

```
import 'package:flutter/material.dart';
import 'package:autostart_settings/autostart_settings.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _text = '';

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Autostart example app'),
        ),
        body: Center(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: ElevatedButton(
                    onPressed: () async {
                      final canOpen = await AutostartSettings.canOpen(
                          autoStart: true, batterySafer: true);
                      setState(() {
                        if (canOpen) {
                          _text = 'device can open autostart Settings';
                        } else {
                          _text = 'device doesn\'t have autostart settings activity';
                        }
                      });
                    },
                    child: const Text('openSettings')),
              ),
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: ElevatedButton(
                    onPressed: () async {
                      final opened = await AutostartSettings.open(
                          autoStart: true, batterySafer: true);
                      setState(() {
                        _text = 'Settings opened: $opened';
                      });
                    },
                    child: const Text('openSettings')),
              ),
              Padding(
                padding: const EdgeInsets.all(8.0),
                child: Text(_text),
              )
            ],
          ),
        ),
      ),
    );
  }
}
```

# Recommendation

I would recommend for better reliability to also request ignoreBatteryOptimizations permission from the permission_handler plugin:

```
import 'package:permission_handler/permission_handler.dart';


                                        await Permission.ignoreBatteryOptimizations
                                            .request();
```
# Contribute
If you own a device where the autostart settings page is not yet added, would be grateful if yo contribute, these adb-commands might help identify the correct activity:
* list all activityName:   adb shell dumpsys package | grep Activity
* finding current active/resumed activity:   adb shell dumpsys activity activities | grep mResumedActivity



