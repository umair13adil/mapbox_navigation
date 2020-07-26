import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:mapbox_navigation/models/mapbox_options.dart';

typedef void MapViewCreatedCallback(MapViewController controller);

class MapBoxMapView extends StatefulWidget {
  const MapBoxMapView({
    Key key,
    this.onMapViewCreated,
  }) : super(key: key);

  final MapViewCreatedCallback onMapViewCreated;

  @override
  State<StatefulWidget> createState() => _MapBoxMapViewState();
}

class _MapBoxMapViewState extends State<MapBoxMapView> {
  @override
  Widget build(BuildContext context) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        viewType: "umair.mapbox_navigation/mapboxMapView",
        onPlatformViewCreated: _onPlatformViewCreated,
      );
    }
    return Text(
        '$defaultTargetPlatform is not yet supported by the text_view plugin');
  }

  void _onPlatformViewCreated(int id) {
    if (widget.onMapViewCreated == null) {
      return;
    }
    widget.onMapViewCreated(new MapViewController._(id));
  }
}

class MapViewController {
  MapViewController._(int id)
      : _channel =
            new MethodChannel('umair.mapbox_navigation/mapboxMapView$id');

  final MethodChannel _channel;

  Future<String> showMap(MapBoxOptions options) async {
    assert(options != null);
    return _channel.invokeMethod('showMapView', <String, dynamic>{
      'initialLat': options.initialLat,
      'initialLong': options.initialLong,
      'shouldSimulateRoute': options.shouldSimulateRoute,
      'language': options.language,
      'zoom': options.zoom,
      'bearing': options.bearing,
      'tilt': options.tilt,
      'alternatives': options.alternatives,
      'clientAppName': options.clientAppName,
      'profile': options.profile,
      'continueStraight': options.continueStraight,
      'enableRefresh': options.enableRefresh,
      'steps': options.steps,
      'voiceInstructions': options.voiceInstructions,
      'bannerInstructions': options.bannerInstructions,
      'testRoute': options.testRoute,
      'debug': options.debug,
    });
  }

  Future<String> buildRoute(
      {double originLat,
      double originLong,
      double destinationLat,
      double destinationLong}) async {
    return _channel.invokeMethod('buildRoute', <String, dynamic>{
      'originLat': originLat,
      'originLong': originLong,
      'destinationLat': destinationLat,
      'destinationLong': destinationLong
    });
  }

  Future<String> startNavigation() async {
    return _channel.invokeMethod('startNavigation');
  }

  Future<String> addMarker({double latitude, double longitude}) async {
    return _channel.invokeMethod('addMarker', <String, dynamic>{
      'latitude': latitude,
      'longitude': longitude,
    });
  }

  Future<String> moveCameraToPosition({double latitude, double longitude}) async {
    return _channel.invokeMethod('moveCameraToPosition', <String, dynamic>{
      'latitude': latitude,
      'longitude': longitude,
    });
  }
}
