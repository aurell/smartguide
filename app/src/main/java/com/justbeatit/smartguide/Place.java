package com.justbeatit.smartguide;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * Created by aurel on 09.06.2017.
 */

public class Place {
    private final String name;
    private final String mapImagePath;
    private final String discounts;
    private final String timetable;
    private final List<Beacon> beacons;
    private final List<Beacon> defaultPath;
    private ListIterator<Beacon> pathWalker;
    private Beacon currentBeacon;

    public Place(String name, String mapImagePath, String discounts, String timetable, List<Beacon> beacons) {
        this.name = name;
        this.mapImagePath = mapImagePath;
        this.discounts = discounts;
        this.timetable = timetable;
        this.beacons = beacons;
        defaultPath = new LinkedList<>();
        setDefaultPath(beacons);
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

    public void startDefaultPath() {
        pathWalker = defaultPath.listIterator();
        currentBeacon = null;
    }

    public Beacon getNextBeaconOnActivePath() {
        if(pathWalker.hasNext()) {
            return pathWalker.next();
        }
        else {
            // end of path
            return null;
        }
    }

    public Beacon getPreviousBeaconOnActivePath() {
        if (pathWalker.hasPrevious()) {
            return pathWalker.previous();
        }
        else {
            // beginning of the path
            return null;
        }
    }

    public boolean isBeaconOnActivePath(String deviceId) {
        for (Beacon beacon : beacons) {
            if (beacon.getDeviceId().equalsIgnoreCase(deviceId)) {
                return true;
            }
        }
        return false;
    }

    public Beacon getCurrentBeaconOnActivePath() {
        return currentBeacon;
    }

    public void setCurrentBeaconOnActivePath(Beacon beacon) {
        if (defaultPath.contains(beacon)) {
            currentBeacon = beacon;
        }
    }

    private void setDefaultPath(Collection<Beacon> beacons) {
        defaultPath.addAll(beacons);
    }
}
