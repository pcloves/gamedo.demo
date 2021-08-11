package org.gamedo.demo.logging;

import org.apache.logging.log4j.Marker;
import org.gamedo.logging.Markers;

public class MyMarkers {
    public static final Marker Login = Markers.of("login");
    public static final Marker Entity = Markers.of("entity");
    public static final Marker DB = Markers.of("db");
    public static final Marker Profiling = Markers.of("profiling");
}
