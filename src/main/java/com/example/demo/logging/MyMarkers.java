package com.example.demo.logging;

import org.gamedo.logging.Markers;
import org.slf4j.Marker;

public class MyMarkers {
    public static final Marker Login = Markers.of("login");
    public static final Marker Entity = Markers.of("entity");
    public static final Marker Load = Markers.of("db");
}
