package com.umair.mapbox_navigation.plugin;

import android.content.Context;

import com.umair.mapbox_navigation.plugin.FlutterMapViewFactory;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class MapViewFactory extends PlatformViewFactory {
    private final BinaryMessenger messenger;

    public MapViewFactory(BinaryMessenger messenger) {
        super(StandardMessageCodec.INSTANCE);
        this.messenger = messenger;
    }

    @Override
    public PlatformView create(Context context, int id, Object o) {
        return new FlutterMapViewFactory(context, messenger, id);
    }
}
