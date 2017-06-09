package com.justbeatit.smartguide;

import java.util.Set;

/**
 * Created by aurel on 09.06.2017.
 */

public class Place {
    public String Name;
    public String MapImagePath;
    public String Discounts;
    public String Timetable;
    public Set<Beacon> Beacons;

    public Place() {

    }

    public Place(String name, String mapImagePath, String discounts, String timetable, Set<Beacon> beacons) {
        Name = name;
        MapImagePath = mapImagePath;
        Discounts = discounts;
        Timetable = timetable;
        Beacons = beacons;
    }
}
