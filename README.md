# mapbox_navigation
#### A Flutter plugin for MapBox's Navigation SDK, NavigationView & Navigation services. (Android)
##### iOS Support comming soon.

[![pub package](https://img.shields.io/pub/v/mapbox_navigation)](https://pub.dev/packages/mapbox_navigation)

Overview
--------

This plugin is developed to use advance features of MapBox for Flutter apps. This plugin provides all the basic features of MapBox along with some useful callbacks.

Features
--------

- MapBox Map View UI
- MapBox's Navigation UI
- Add markers on Map
- Move camera to location on Map
- Draw route on Map
- Start Navigation
- Simulate Route Option
- Listen to Navigation Events
- Listen to User Off Route Event
- Listen to Fast Route Available
- Listen to Voice Events
- Listen to Banner Messages
- Listen to Route Progress Events (Distance, Duration, Travelled & more)
- Listen to Mile stone Events
- Control Camera zoom, tile, bearing


## Install
In your pubspec.yaml

```yaml
dependencies:
    mapbox_navigation: [LATEST_VERSION]
```

```dart
    import 'package:mapbox_navigation/mapbox_navigation.dart';
```

```dart
    MapViewController controller;
    var mapBox = MapboxNavigation();

    @override
    void initState() {
        super.initState();
        mapBox.init();

        mapBox.getMapBoxEventResults().onData((data) {
            print("Event: ${data.eventName}, Data: ${data.data}");

            //Get MapBox Event Type
            var event = MapBoxEventProvider.getEventType(data.eventName);

            if (event == MapBoxEvent.route_built) {

                var routeResponse = MapBoxRouteResponse.fromJson(jsonDecode(data.data));

                print("Route Distance: ${routeResponse.routes.first.distance},"
                      "Route Duration: ${routeResponse.routes.first.duration}");

            } else if (event == MapBoxEvent.progress_change) {
                
                var progressEvent = MapBoxProgressEvent.fromJson(jsonDecode(data.data));

                print("Leg Distance Remaining: ${progressEvent.legDistanceRemaining},"
                      "Leg Duration Remaining: ${progressEvent.legDurationRemaining},"
                      "Distance Travelled: ${progressEvent.distanceTraveled}");

            } else if (event == MapBoxEvent.milestone_event) {

                var mileStoneEvent = MapBoxMileStoneEvent.fromJson(jsonDecode(data.data));

                print("Distance Travelled: ${mileStoneEvent.distanceTraveled}");

            } else if (event == MapBoxEvent.speech_announcement) {

                var speechEvent = MapBoxEventData.fromJson(jsonDecode(data.data));

                print("Speech Text: ${speechEvent.data}");

            } else if (event == MapBoxEvent.banner_instruction) {

                var bannerEvent = MapBoxEventData.fromJson(jsonDecode(data.data));

                print("Banner Text: ${bannerEvent.data}");
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
            body: MapBoxMapView(onMapViewCreated: _onMapViewCreated)
        ),
        );
    }

    void _onMapViewCreated(MapViewController controller) async {
        this.controller = controller;
        await controller.showMap(MapBoxOptions(
            initialLat: 33.569126,
            initialLong: 73.1231471,
            shouldSimulateRoute: true,
            enableRefresh: false,
            alternatives: true,
            zoom: 13.0,
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
```

## Android

##### Step 1:
Change your Android Project's *MainActivity* class to following:

```kotlin
    import androidx.annotation.NonNull
    import com.umair.mapbox_navigation.MapboxNavigationPlugin
    import io.flutter.embedding.android.FlutterActivity
    import io.flutter.embedding.engine.FlutterEngine
    import io.flutter.plugins.GeneratedPluginRegistrant


    class MainActivity : FlutterActivity() {

        companion object {

            @JvmStatic
            var flutterEngineInstance: FlutterEngine? = null
        }

        override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
            GeneratedPluginRegistrant.registerWith(flutterEngine)
            flutterEngineInstance = flutterEngine
        }

        override fun onResume() {
            super.onResume()

            flutterEngineInstance?.let {
                MapboxNavigationPlugin.registerWith(it, getString(R.string.access_token))
            }
        }

        override fun onDestroy() {
            flutterEngine?.platformViewsController?.onFlutterViewDestroyed()
            super.onDestroy()
        }
    }
```

##### Step 2:
Add your MapBox's token on *strings.xml* file:

```xml
    <string name="access_token" translatable="false">YOUR_ACCESS_TOKEN_HERE</string>
```

## Flutter

### Build Route:

This will build route based on origin & destination values provided.

```dart
    await controller.buildRoute(
        originLat: 33.569126,
        originLong: 73.1231471,
        destinationLat: 33.6392443,
        destinationLong: 73.278358,
    );
```

### Start Navigation:

This will start navigation based on route fetched using 'buildRoute' method.

```dart
    await controller.startNavigation();
```

### Add Marker:

This will add marker on provided location.

```dart
    await controller.addMarker(
        latitude: 33.569126, 
        longitude: 73.1231471);
```

### Move Camera:

This will move camera to provided location.

```dart
    await controller.moveCameraToPosition(
            latitude: 33.569126, 
            longitude: 73.1231471);
```

### MapBox Events:

MapBox events from 'MapBoxEvent' will be returned in the following stream along with data. Data will be a JSON String.

```dart
    enum MapBoxEvent {
        map_ready,
        route_built,
        progress_change,
        user_off_route,
        milestone_event,
        navigation_running,
        navigation_cancelled,
        navigation_finished,
        faster_route_found,
        speech_announcement,
        banner_instruction,
        on_arrival,
        failed_to_reroute,
        reroute_along
    }

    mapBox.getMapBoxEventResults().onData((data) {
      print(
          "Event: ${data.eventName} Data: ${data.data}");
    });
```

_______________________________________________

# Author

MapBox Navigation plugin is developed by Umair Adil. You can email me at <m.umair.adil@gmail.com> for any queries.

