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
