# mapbox_navigation
#### A Flutter plugin for MapBox's Navigation SDK, NavigationView & Navigation services. (Android)
##### iOS Support comming soon.

[![pub package](https://img.shields.io/pub/v/mapbox_navigation)](https://pub.dev/packages/mapbox_navigation)

Overview
--------

This plugin is developed to used advance features of MapBox on Flutter apps.

Features
--------

- MapBox map view
- MapBox's Navigation


## Install
In your pubspec.yaml

```yaml
dependencies:
    mapbox_navigation: [LATEST_VERSION]
```

```dart
    import 'package:mapbox_navigation/mapbox_navigation.dart';
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

        override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
            GeneratedPluginRegistrant.registerWith(flutterEngine)
            MapboxNavigationPlugin.registerWith(flutterEngine, this, getString(R.string.access_token))
        }
    }
```

##### Step 2:
Add your MapBox's token on *strings.xml* file:

```xml
    <string name="access_token" translatable="false">YOUR_ACCESS_TOKEN_HERE</string>
```

_______________________________________________

# Author

MapBox Navigation plugin is developed by Umair Adil. You can email me at <m.umair.adil@gmail.com> for any queries.

