package com.justbeatit.smartguide;

/**
 * Created by aurel on 09.06.2017.
 */

public class Beacon {
    private final String name;
    private final String info;
    private final String pathTips;
    private final String deviceId;

    public Beacon(String name, String info, String pathTips, String deviceId) {
        this.name = name;
        this.info = info;
        this.pathTips = pathTips;
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public String getPathTips() {
        return pathTips;
    }

    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public String toString() {
        return "Beacon{" +
                "deviceId='" + deviceId + '\'' +
                '}';
    }
}
