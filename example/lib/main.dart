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
  var mapBox = MapboxNavigation();

  @override
  void initState() {
    super.initState();
    mapBox.init();

    mapBox.getMapBoxEventResults().onData((data) {
      print(
          "getMapBoxEventResults: Event: ${data.eventName}\n,Data: ${data.data}");
    });
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
              child: Column(
                children: <Widget>[
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: <Widget>[
                      RaisedButton(
                          child: Text("Add Marker"),
                          color: Colors.blue,
                          onPressed: () async {
                            await controller.addMarker(33.569126, 73.1231471);
                            await controller.moveCameraToPosition(
                                33.569126, 73.1231471);
                          }),
                      RaisedButton(
                          child: Text("Move Camera"),
                          color: Colors.blue,
                          onPressed: () async {
                            await controller.moveCameraToPosition(
                                33.6392443, 73.278358);
                          })
                    ],
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: <Widget>[
                      RaisedButton(
                          child: Text("Build Route"),
                          color: Colors.blue,
                          onPressed: () async {
                            await controller.buildRoute();
                          }),
                      RaisedButton(
                          child: Text("Navigate"),
                          color: Colors.blue,
                          onPressed: () async {
                            await controller.startNavigation();
                          })
                    ],
                  )
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
        enableRefresh: false,
        zoom: 17.0,
        tilt: 0.0,
        bearing: 0.0,
        clientAppName: "MapBox Demo",
        voiceInstructions: true,
        bannerInstructions: true,
        continueStraight: true,
        profile: "driving-traffic",
        language: "en",
        testRoute: "",
        debug: true));
  }
}
