package com.justbeatit.smartguide;

/**
 * Created by aurel on 09.06.2017.
 */

public class Beacon {
    public String Name;
    public String Info;
    public String Location;
    public String DeviceId;

    public Beacon() {

    }

    public Beacon(String name, String info, String location, String deviceId) {
        Name = name;
        Info = info;
        Location = location;
        DeviceId = deviceId;
    }
}
