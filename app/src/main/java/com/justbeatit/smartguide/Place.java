package com.justbeatit.smartguide;

import java.util.Set;

/**
 * Created by aurel on 09.06.2017.
 */

public class Place {
    private final String name;
    private final String mapImagePath;
    private final String discounts;
    private final String timetable;
    private final Set<Beacon> beacons;

    public Place(String name, String mapImagePath, String discounts, String timetable, Set<Beacon> beacons) {
        this.name = name;
        this.mapImagePath = mapImagePath;
        this.discounts = discounts;
        this.timetable = timetable;
        this.beacons = beacons;
    }

    public String getName() {
        return name;
    }

    public String getMapImagePath() {
        return mapImagePath;
    }

    public String getDiscounts() {
        return discounts;
    }

    public String getTimetable() {
        return timetable;
    }

    public Set<Beacon> getBeacons() {
        return beacons;
    }
}
