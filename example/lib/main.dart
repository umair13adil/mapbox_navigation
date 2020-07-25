import 'package:flutter/material.dart';
import 'package:mapbox_navigation/mapbox_navigation.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  MapViewController controller;

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('MapBox Demo'),
        ),
        body: Stack(
          children: <Widget>[
            MapBoxMapView(onMapViewCreated: _onMapViewCreated),
            Align(
              alignment: Alignment.bottomCenter,
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: <Widget>[
                  RaisedButton(
                      child: Text("Build Route"),
                      onPressed: () async {
                        await controller.buildRoute();
                      }),
                  RaisedButton(
                      child: Text("Navigate"),
                      onPressed: () async {
                        await controller.startNavigation();
                      })
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _onMapViewCreated(MapViewController controller) async {
    this.controller = controller;
    await controller.showMap(MapBoxOptions(
        initialLat: 33.569126,
        initialLong: 73.1231471,
        originLat: 33.569126,
        originLong: 73.1231471,
        destinationLat: 33.6392443,
        destinationLong: 73.278358,
        shouldSimulateRoute: true,
        profile: "driving-traffic",
        language: "en",
        debug: true));
  }
}
