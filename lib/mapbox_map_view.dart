import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

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

  Future<void> showMap(String text) async {
    assert(text != null);
    return _channel.invokeMethod('showMapView', text);
  }
}
