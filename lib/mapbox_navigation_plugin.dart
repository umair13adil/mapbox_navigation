import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:mapbox_navigation/events/mapbox_event_results.dart';

class MapboxNavigation {
  static const MethodChannel _channel =
      const MethodChannel('mapbox_navigation');
  static const _eventChannel = EventChannel('mapbox_navigation_stream');

  static Stream<MapBoxEventResults> get mapBoxEventResult =>
      _mapBoxListenerController.stream;

  static StreamController<MapBoxEventResults> _mapBoxListenerController =
      StreamController<MapBoxEventResults>();

  StreamSubscription<MapBoxEventResults> mapBoxSubscription =
      _mapBoxListenerController.stream.asBroadcastStream().listen(
          (data) {
            print("DataReceived: " + data.toString());
          },
          onDone: () {},
          onError: (error) {
            print("Some Error");
          });

  void init() {
    _eventChannel.receiveBroadcastStream().listen((dynamic event) {
      Map result = jsonDecode(event);
      var res = MapBoxEventResults.fromJson(result);
      if (res.eventName.isNotEmpty) {
        _mapBoxListenerController.add(res);
      }
    }, onError: (dynamic error) {});
  }

  StreamSubscription<MapBoxEventResults> getMapBoxEventResults() {
    return mapBoxSubscription;
  }

  void dispose() {
    _mapBoxListenerController.close();
    mapBoxSubscription.cancel();
  }
}
