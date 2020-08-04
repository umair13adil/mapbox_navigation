import 'dart:convert';

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
  var isLoading = false;
  var isRouteInProgress = false;

  @override
  void initState() {
    super.initState();
    mapBox.init();

    mapBox.getMapBoxEventResults().onData((data) {
      print("Event: ${data.eventName}, Data: ${data.data}");

      var event = MapBoxEventProvider.getEventType(data.eventName);

      if (event == MapBoxEvent.route_building) {

        setState(() {
          isLoading = true;
        });

        print("Building route..");
      } else if (event == MapBoxEvent.route_build_failed) {

        setState(() {
          isLoading = false;
        });

        print("Route building failed.");

      } else if (event == MapBoxEvent.route_built) {

        setState(() {
          isLoading = false;
        });

        var routeResponse = MapBoxRouteResponse.fromJson(jsonDecode(data.data));

        controller.getFormattedDistance(routeResponse.routes.first.distance)
            .then((value) => print("Route Distance: $value"));

        controller.getFormattedDuration(routeResponse.routes.first.duration)
            .then((value) => print("Route Duration: $value"));

      } else if (event == MapBoxEvent.progress_change) {

        setState(() {
          isRouteInProgress = true;
        });

        var progressEvent = MapBoxProgressEvent.fromJson(jsonDecode(data.data));

        controller.getFormattedDistance(progressEvent.legDistanceRemaining)
            .then((value) => print("Leg Distance Remaining: $value"));

        controller.getFormattedDistance(progressEvent.distanceTraveled)
            .then((value) => print("Distance Travelled: $value"));

        controller.getFormattedDuration(progressEvent.legDurationRemaining)
            .then((value) => print("Leg Duration Remaining: $value"));

        print(
            "Voice Instruction: ${progressEvent.voiceInstruction},"
            "Banner Instruction: ${progressEvent.bannerInstruction}");

      } else if (event == MapBoxEvent.milestone_event) {

        var mileStoneEvent = MapBoxMileStoneEvent.fromJson(jsonDecode(data.data));

        controller.getFormattedDistance(mileStoneEvent.distanceTraveled)
            .then((value) => print("Distance Travelled: $value"));

      } else if (event == MapBoxEvent.speech_announcement) {

        var speechEvent = MapBoxEventData.fromJson(jsonDecode(data.data));
        print("Speech Text: ${speechEvent.data}");

      } else if (event == MapBoxEvent.banner_instruction) {

        var bannerEvent = MapBoxEventData.fromJson(jsonDecode(data.data));
        print("Banner Text: ${bannerEvent.data}");

      } else if (event == MapBoxEvent.navigation_cancelled) {

        setState(() {
          isRouteInProgress = false;
        });

      } else if (event == MapBoxEvent.navigation_finished) {

        setState(() {
          isRouteInProgress = false;
        });

      } else if (event == MapBoxEvent.on_arrival) {

        setState(() {
          isRouteInProgress = false;
        });

      } else if (event == MapBoxEvent.user_off_route) {

        var locationData = MapBoxLocation.fromJson(jsonDecode(data.data));
        print("User has off-routed: Location: ${locationData.toString()}");

      } else if (event == MapBoxEvent.faster_route_found) {

        var routeResponse = MapBoxRouteResponse.fromJson(jsonDecode(data.data));

        controller.getFormattedDistance(routeResponse.routes.first.distance)
            .then((value) => print("Faster route found: Route Distance: $value"));

        controller.getFormattedDuration(routeResponse.routes.first.duration)
            .then((value) => print("Faster route found: Route Duration: $value"));
      }
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
            !isLoading
                ? Align(
              alignment: Alignment.bottomCenter,
              child: !isRouteInProgress
                  ? Column(
                children: <Widget>[
                  Row(
                    mainAxisAlignment:
                    MainAxisAlignment.spaceEvenly,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: <Widget>[
                      RaisedButton(
                          child: Text("Add Marker"),
                          color: Colors.blue,
                          textColor: Colors.white,
                          onPressed: () async {
                            await controller.addMarker(
                                latitude: 33.569126,
                                longitude: 73.1231471);
                            await controller.moveCameraToPosition(
                                latitude: 33.569126,
                                longitude: 73.1231471);
                          }),
                      RaisedButton(
                          child: Text("Move Camera"),
                          color: Colors.blue,
                          textColor: Colors.white,
                          onPressed: () async {
                            await controller.moveCameraToPosition(
                                latitude: 33.6392443,
                                longitude: 73.278358);
                          })
                    ],
                  ),
                  Row(
                    mainAxisAlignment:
                    MainAxisAlignment.spaceEvenly,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: <Widget>[
                      RaisedButton(
                          child: Text("Build Route"),
                          color: Colors.blue,
                          textColor: Colors.white,
                          onPressed: () async {
                            setState(() {
                              isLoading = true;
                            });
                            await controller.buildRoute(
                                originLat: 33.569126,
                                originLong: 73.1231471,
                                destinationLat: 33.6392443,
                                destinationLong: 73.278358,
                                zoom: 9.5);
                          }),
                      RaisedButton(
                          child: Text("Navigate"),
                          color: Colors.blue,
                          textColor: Colors.white,
                          onPressed: () async {
                            await controller.startNavigation(
                                shouldSimulateRoute: true);
                          }),
                      RaisedButton(
                          child: Text("Navigate Embedded"),
                          color: Colors.blue,
                          textColor: Colors.white,
                          onPressed: () async {
                            await controller
                                .startEmbeddedNavigation(
                                zoom: 18.0,
                                tilt: 90.0,
                                bearing: 50.0,
                                shouldSimulateRoute: true);
                          })
                    ],
                  )
                ],
              )
                  : RaisedButton(
                  child: Text("Cancel Navigation"),
                  color: Colors.blue,
                  textColor: Colors.white,
                  onPressed: () async {
                    setState(() {
                      isRouteInProgress = false;
                      isLoading = false;
                    });
                    await controller.stopNavigation();
                  }),
            )
                : Align(
                alignment: Alignment.center,
                child: CircularProgressIndicator()),
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
        enableRefresh: true,
        alternatives: true,
        zoom: 13.0,
        tilt: 0.0,
        bearing: 0.0,
        clientAppName: "MapBox Demo",
        voiceInstructions: true,
        bannerInstructions: true,
        continueStraight: false,
        profile: "driving-traffic",
        language: "en",
        testRoute: "",
        debug: true));
  }

  String _prettyPrintJson(String input) {
    JsonDecoder _decoder = JsonDecoder();
    JsonEncoder _encoder = JsonEncoder.withIndent('  ');
    var object = _decoder.convert(input);
    var prettyString = _encoder.convert(object);
    prettyString.split('\n').forEach((element) => print(element));
    return prettyString;
  }
}
