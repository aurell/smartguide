package com.justbeatit.smartguide;

/**
 * Created by aurel on 09.06.2017.
 */

public class Beacon {
    private final String name;
    private final String info;
    private final String location;
    private final String deviceId;

    public Beacon(String name, String info, String location, String deviceId) {
        this.name = name;
        this.info = info;
        this.location = location;
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public String getLocation() {
        return location;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
